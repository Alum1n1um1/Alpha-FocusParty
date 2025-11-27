package com.example.focusparty.model

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import com.google.firebase.Timestamp
import kotlin.time.Duration.Companion.milliseconds



class Database(
    private val store: FirebaseFirestore = Firebase.firestore
) {
    companion object {
        private val _db = Database()
        fun getInstance(): Database {
            return _db
        }
    }

    private val users = store.collection("Users")
    private val rooms = store.collection("rooms")
    private val events = store.collection("Events")

    fun addUser(uid: String, email:String){ // Ajoute un utilisateur
        val user = hashMapOf(
            "uid" to uid,
            "email" to email,
            "level" to 1,
            "exp" to 0,
            "friends" to listOf<String>(),
            "rooms" to listOf<String>(),
            "comment" to "",
            "first_connection" to true,
            "points" to 500
        )
        users.document(uid).set(user)
    }

    fun addRoom(room: Room) {

        val doc = rooms.document()

        val data = hashMapOf(
            "name" to room.name,
            "owner" to room.owner,
            "description" to room.description,
            "status" to room.status,
            "members" to room.members,
            "jalons" to room.jalons.map {
                mapOf(
                    "name" to it.name,
                    "isDone" to it.isDone
                )
            },
            "timer" to mapOf(
                "state" to room.timer.state.name,
                "startTime" to room.timer.startTime,
                "durationMs" to room.timer.durationMs,
                "remainingMs" to room.timer.remainingMs
            )
        )

        doc.set(data)
    }

    fun getRoomsOf(uid: String): Flow<List<Room>> = callbackFlow {
        val query = store.collection("rooms").whereArrayContains("members", uid)

        val registration = query.addSnapshotListener { snap, err ->
            if (err != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            val rooms = snap?.documents?.mapNotNull { doc ->
                val id = doc.id
                val name = doc.getString("name") ?: ""
                val owner = doc.getString("owner") ?: ""
                val description = doc.getString("description") ?: ""
                val status = (doc.getLong("status") ?: 0L).toInt()
                val members = doc.get("members") as? List<String> ?: emptyList()

                val jalons = (doc.get("jalons") as? List<Map<String, Any>>)
                    ?.map { map ->
                        Jalon(
                            name = map["name"] as? String ?: "",
                            isDone = map["isDone"] as? Boolean ?: false
                        )
                    } ?: emptyList()

                // ---------- TIMER ----------
                val timerMap = doc.get("timer") as? Map<String, Any>
                val timer = if (timerMap != null) {

                    val stateString = timerMap["state"] as? String ?: "NONE"
                    val state = TimerState.valueOf(stateString)

                    val startTimestamp = timerMap["startTime"] as? com.google.firebase.Timestamp
                    val startDate = startTimestamp?.toDate() ?: Date(0)

                    val durationMs = (timerMap["durationMs"] as? Long) ?: 0L
                    val remainingMs = (timerMap["remainingMs"] as? Long) ?: 0L

                    Timer(
                        state = state,
                        startTime = startDate,
                        durationMs = durationMs,
                        remainingMs = remainingMs
                    )
                } else {
                    Timer()
                }

                Room(
                    id = id,
                    name = name,
                    owner = owner,
                    description = description,
                    status = status,
                    members = members,
                    jalons = jalons,
                    timer = timer
                )
            }.orEmpty()

            trySend(rooms)
        }

        awaitClose { registration.remove() }
    }

    fun getRoomById(roomId: String): Flow<Room?> = callbackFlow {
        val docRef = store.collection("rooms").document(roomId)

        val registration = docRef.addSnapshotListener { snap, err ->
            if (err != null) {
                trySend(null)
                return@addSnapshotListener
            }

            if (snap != null && snap.exists()) {

                val name = snap.getString("name") ?: ""
                val owner = snap.getString("owner") ?: ""
                val description = snap.getString("description") ?: ""
                val status = (snap.getLong("status") ?: 0L).toInt()
                val members = snap.get("members") as? List<String> ?: emptyList()

                val jalons = (snap.get("jalons") as? List<Map<String, Any>>)
                    ?.map { map ->
                        Jalon(
                            name = map["name"] as? String ?: "",
                            isDone = map["isDone"] as? Boolean ?: false
                        )
                    } ?: emptyList()

                // ---------- TIMER ----------
                val timerMap = snap.get("timer") as? Map<String, Any>
                val timer = if (timerMap != null) {

                    val stateString = timerMap["state"] as? String ?: "NONE"
                    val state = TimerState.valueOf(stateString)

                    val startTimestamp = timerMap["startTime"] as? com.google.firebase.Timestamp
                    val startDate = startTimestamp?.toDate() ?: Date(0)

                    val durationMs = (timerMap["durationMs"] as? Long) ?: 0L
                    val remainingMs = (timerMap["remainingMs"] as? Long) ?: 0L

                    Timer(
                        state = state,
                        startTime = startDate,
                        durationMs = durationMs,
                        remainingMs = remainingMs
                    )
                } else {
                    Timer()
                }

                trySend(
                    Room(
                        id = roomId,
                        name = name,
                        owner = owner,
                        description = description,
                        status = status,
                        members = members,
                        jalons = jalons,
                        timer = timer
                    )
                )

            } else {
                trySend(null)
            }
        }

        awaitClose { registration.remove() }
    }

    fun getUsers() { // Récupère tous les utilisateurs
        // TODO
    }

    fun getEvents(): Flow<List<Event>> = callbackFlow { // Récupère tous les évènements
        val col = store.collection("events")
        val reg = col.addSnapshotListener { snap, err ->
            if (err != null) {
                // on peut close ou envoyer liste vide selon ton choix
                trySend(emptyList())
                return@addSnapshotListener
            }
            val list = snap?.documents?.mapNotNull { doc ->
                val name = doc.getString("name") ?: ""
                val date_start = doc.getDate("date_start")?: Date()
                val deadline = doc.getDate("deadline")?: Date()
                val perodicity = doc.getString("periodicity") ?: ""
                val members = doc.get("members") as? List<String> ?: emptyList()
                val notif = doc.get("notif") as? List<String> ?: emptyList()
                val priority = doc.getString("priority") ?: ""

                Event(name = name,
                    date_start = date_start,
                    deadline = deadline,
                    perodicity = perodicity,
                    members = members,
                    notif = notif,
                    priority = priority
                )
            }.orEmpty()
            trySend(list)
        }
        awaitClose { reg.remove() }
    }
    fun getEventsOf(uid: String): Flow<List<Event>> = callbackFlow {
        val query = store
            .collection("events")
            .whereArrayContains("members", uid)

        val registration = query.addSnapshotListener { snap, err ->
            if (err != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            val list = snap?.documents?.mapNotNull { doc ->
                Event(
                    name = doc.getString("name") ?: "",
                    date_start = doc.getDate("date_start") ?: Date(),
                    deadline = doc.getDate("deadline") ?: Date(),
                    perodicity = doc.getString("periodicity") ?: "",
                    members = doc.get("members") as? List<String> ?: emptyList(),
                    notif = doc.get("notif") as? List<String> ?: emptyList(),
                    priority = doc.getString("priority") ?: ""
                )
            }.orEmpty()

            trySend(list)
        }

        awaitClose { registration.remove() }
    }

    fun getFriends(user:String) { // Récupère les amis de l'utilisateur
        TODO()
    }

    fun addFriend(user:String, friend:String) { // Ajoute un ami à l'utilisateur
        TODO()
    }

    suspend fun getDocumentSnapshot(collectionPath: String, doc:String): DocumentSnapshot?{
        return try {
            store
                .collection(collectionPath)
                .document(doc)
                .get()
                .await()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserPoints(uid: String?):Int {
        if (uid == null) return 0
        val snapshot = getDocumentSnapshot("Users",uid)
        return snapshot?.getLong("points")?.toInt() ?: 0
    }

    suspend fun getUserLevel(uid: String?):Int {
        if (uid == null) return 1
        val snapshot = getDocumentSnapshot("Users",uid)
        return snapshot?.getLong("level")?.toInt() ?: 1
    }

    suspend fun getUserExp(uid: String?):Int {
        if (uid == null) return 0
        val snapshot = getDocumentSnapshot("Users",uid)
        return snapshot?.getLong("exp")?.toInt() ?: 0
    }

    suspend fun startTimer(roomId: String, duration: Duration) {
        val d = duration.toMillis()
        val timer = mapOf(
            "state" to "RUNNING",
            "startTime" to Date(),
            "durationMs" to d,
            "remainingMs" to d
        )
        rooms.document(roomId).update("timer", timer).await()
    }

    suspend fun pauseTimer(roomId: String, timer: Timer) {
        val now = Date().time
        val elapsed = now - timer.startTime.time
        val remaining = (timer.durationMs - elapsed).coerceAtLeast(0L)

        val newTimer = mapOf(
            "state" to "PAUSED",
            "startTime" to Date(0),
            "durationMs" to timer.durationMs,
            "remainingMs" to remaining
        )

        rooms.document(roomId).update("timer", newTimer).await()
    }

    suspend fun resumeTimer(roomId: String, timer: Timer) {
        val rem = timer.remainingMs.coerceAtLeast(0L)

        val newTimer = mapOf(
            "state" to "RUNNING",
            "startTime" to Date(),           // nouvelle référence
            "durationMs" to rem,             // durée totale remise à remaining
            "remainingMs" to rem
        )

        rooms.document(roomId).update("timer", newTimer).await()
    }

    suspend fun stopTimer(roomId: String) {
        val newTimer = mapOf(
            "state" to "NONE",
            "startTime" to Date(0),
            "durationMs" to 0L,
            "remainingMs" to 0L
        )

        rooms.document(roomId).update("timer", newTimer).await()
    }

    suspend fun endJalon(roomId: String, index: Int, jalon: Jalon) {

        Log.w(
            "DEBUG_END_JALON",
            ">>> Appel endJalon(roomId=$roomId, index=$index, name=${jalon.name}, isDone=${jalon.isDone})"
        )

        val roomRef = rooms.document(roomId)

        try {
            store.runTransaction { tx ->

                // ----------- 1) Lire TOUT en premier -----------
                val snapRoom = tx.get(roomRef)
                if (!snapRoom.exists()) {
                    throw IllegalStateException("Room introuvable : $roomId")
                }

                val jalons = snapRoom.get("jalons") as? List<Map<String, Any>>
                    ?: throw IllegalStateException("Champ 'jalons' absent ou invalide")

                if (index !in jalons.indices) {
                    throw IllegalArgumentException("Index jalon invalide : $index")
                }

                val members = snapRoom.get("members") as? List<String> ?: emptyList()

                // Lire tous les utilisateurs AVANT toute écriture
                val userSnaps = members.associateWith { uid ->
                    val ref = users.document(uid)
                    ref to tx.get(ref)      // lecture obligatoire en amont
                }

                // ----------- 2) Préparer les nouvelles valeurs -----------

                // Mise à jour du jalon
                val updatedJalon = mapOf(
                    "name" to jalon.name,
                    "isDone" to jalon.isDone
                )
                val updatedList = jalons.toMutableList()
                updatedList[index] = updatedJalon

                // Nouvelles valeurs pour les users
                val newPoints = userSnaps.mapValues { (_, pair) ->
                    val (_, snapUser) = pair
                    (snapUser.getLong("points") ?: 0L) + 50L
                }

                // ----------- 3) Toutes les écritures UNIQUEMENT maintenant -----------

                // Mettre à jour les jalons
                tx.update(roomRef, "jalons", updatedList)

                // Mettre à jour les points des membres
                for ((uid, pts) in newPoints) {
                    tx.update(users.document(uid), "points", pts)
                }

            }.await()

            Log.w("DEBUG_END_JALON", ">>> FIN transaction Firestore OK")

        } catch (e: Exception) {
            Log.e("DEBUG_END_JALON", "Erreur Firestore : ${e.message}", e)
            throw e
        }
    }




    suspend fun addExpToUser(uid: String, exp: Long): Pair<Int, Int> {

        val userRef = users.document(uid)

        return store.runTransaction { tx ->

            val snap = tx.get(userRef)

            var currentExp = (snap.getLong("exp") ?: 0L).toInt()
            var currentLevel = (snap.getLong("level") ?: 1L).toInt()
            var currentPoints = (snap.getLong("points") ?: 1L).toInt()

            // Ajout des points gagnés
            currentExp += exp.toInt()

            var levelsAugment = 0

            // Boucle de montée de niveau
            while (true) {
                val required = 50 * currentLevel

                if (currentExp >= required) {
                    currentExp -= required
                    levelsAugment += 1
                } else {
                    break
                }
            }

            tx.update(userRef, mapOf(
                "exp" to currentExp,
                "level" to currentLevel+levelsAugment,
                "points" to currentPoints+levelsAugment*100
            ))

            Pair(currentLevel, currentExp)
        }.await()
    }


    fun getJalonsOfRoom(roomId: String, onResult: (List<Jalon>) -> Unit) {
//        rooms.document(roomId).collection("jalons").get()
//            .addOnSuccessListener { result ->
//                val jalons = result.map { doc ->
//                    Jalon(
//                        name = doc.getString("name") ?: "NameNotFound",
//                        isDone = doc.getBoolean("isDone") ?: false
//                    )
//                }
//                onResult(jalons)
//            }
    }

    fun getRooms(): Flow<List<Room>> = callbackFlow {
//        val col = store.collection("rooms")
//        val reg = col.addSnapshotListener { snap, err ->
//            if (err != null) {
//                trySend(emptyList())
//                return@addSnapshotListener
//            }
//
//            val list = snap?.documents?.mapNotNull { doc ->
//                val id = doc.id
//                val name = doc.getString("name") ?: ""
//                val owner = doc.getString("owner") ?: ""
//                val description = doc.getString("description") ?: ""
//                val status = (doc.getLong("status") ?: 0L).toInt()
//                val members = doc.get("members") as? List<String> ?: emptyList()
//                val jalons = (doc.get("jalons") as? List<Map<String, Any>>)
//                    ?.map { map ->
//                        Jalon(
//                            name = map["name"] as? String ?: "",
//                            isDone = map["isDone"] as? Boolean ?: false
//                        )
//                    } ?: emptyList()
//
//                Room(
//                    id = id,
//                    name = name,
//                    owner = owner,
//                    description = description,
//                    status = status,
//                    members = members,
//                    jalons = jalons
//                )
//            }.orEmpty()
//
//            trySend(list)
//        }
//        awaitClose { reg.remove() }
    }


}