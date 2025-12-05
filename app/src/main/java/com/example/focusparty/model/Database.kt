package com.example.focusparty.model

import android.util.Log
import com.example.focusparty.viewmodel.LeaderboardEntry
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
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import kotlin.math.max
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
            "points" to 500,
            "tempsTotal" to 0L,
            "jalonsTermines" to 0,
            "isConnected" to false
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
            ),
            "tempsTotal" to room.tempsTotal,
            "points" to room.points,
            "jalonsTermines" to room.jalonsTermines,
            "level" to room.level,
            "exp" to room.exp
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
                val tempsTotal = doc.getLong("tempsTotal") ?: 0L
                val points = (doc.getLong("points") ?: 0L).toInt()
                val jalonsTermines = (doc.getLong("jalonsTermines") ?: 0L).toInt()
                val level = (doc.getLong("level") ?: 1L).toInt()
                val exp = (doc.getLong("exp") ?: 0L).toInt()


                Room(
                    id = id,
                    name = name,
                    owner = owner,
                    description = description,
                    status = status,
                    members = members,
                    jalons = jalons,
                    timer = timer,
                    tempsTotal = tempsTotal,
                    points = points,
                    jalonsTermines = jalonsTermines,
                    level = level,
                    exp = exp
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
                val tempsTotal = snap.getLong("tempsTotal") ?: 0L
                val points = (snap.getLong("points") ?: 0L).toInt()
                val jalonsTermines = (snap.getLong("jalonsTermines") ?: 0L).toInt()
                val level = (snap.getLong("level") ?: 1L).toInt()
                val exp = (snap.getLong("exp") ?: 0L).toInt()


                trySend(
                    Room(
                        id = roomId,
                        name = name,
                        owner = owner,
                        description = description,
                        status = status,
                        members = members,
                        jalons = jalons,
                        timer = timer,
                        tempsTotal = tempsTotal,
                        points = points,
                        jalonsTermines = jalonsTermines,
                        level = level,
                        exp = exp
                    )
                )

            } else {
                trySend(null)
            }
        }

        awaitClose { registration.remove() }
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
        val roomRef = rooms.document(roomId)

        try {
            store.runTransaction { tx ->

                val snapRoom = tx.get(roomRef)
                if (!snapRoom.exists())
                    throw IllegalStateException("Room introuvable : $roomId")

                val jalons = snapRoom.get("jalons") as? List<Map<String, Any>>
                    ?: throw IllegalStateException("Champ 'jalons' absent ou invalide")

                if (index !in jalons.indices)
                    throw IllegalArgumentException("Index jalon invalide : $index")

                val members = snapRoom.get("members") as? List<String> ?: emptyList()

                val currentRoomMilestones = (snapRoom.getLong("jalonsTermines") ?: 0L)
                val currentRoomExp = (snapRoom.getLong("exp") ?: 0L)
                val currentRoomLevel = (snapRoom.getLong("level") ?: 1L).toInt()

                val userSnaps = members.associateWith { uid ->
                    val ref = users.document(uid)
                    ref to tx.get(ref)
                }

                // --- Mise à jour du jalon ---
                val updatedJalon = mapOf(
                    "name" to jalon.name,
                    "isDone" to jalon.isDone
                )
                val updatedList = jalons.toMutableList()
                updatedList[index] = updatedJalon

                // --- Points des users ---
                val newPoints = userSnaps.mapValues { (_, pair) ->
                    val (_, snapUser) = pair
                    (snapUser.getLong("points") ?: 0L) + 50L
                }

                // --- Jalons personnels des users ---
                val newUserMilestones = userSnaps.mapValues { (_, pair) ->
                    val (_, snapUser) = pair
                    (snapUser.getLong("jalonsTermines") ?: 0L) + 1L
                }

                // --- Écritures Firestore (toutes après les lectures) ---

                tx.update(roomRef, "jalons", updatedList)
                tx.update(roomRef, "jalonsTermines", currentRoomMilestones + 1L)


                // Users : points
                for ((uid, pts) in newPoints) {
                    tx.update(users.document(uid), "points", pts)
                }

                // Users : jalons
                for ((uid, newM) in newUserMilestones) {
                    tx.update(users.document(uid), "jalonsTermines", newM)
                }

            }.await()

        } catch (e: Exception) {
            Log.e("DEBUG_END_JALON", "Erreur Firestore : ${e.message}", e)
            throw e
        }
    }

    suspend fun addWorkedTimeToUser(uid: String, durationMs: Long) {

        val userRef = users.document(uid)

        store.runTransaction { tx ->
            val snapUser = tx.get(userRef)
            val old = snapUser.getLong("tempsTotal") ?: 0L
            tx.update(userRef, "tempsTotal", old + durationMs)
        }.await()
    }

    suspend fun addWorkedTimeToRoom(roomId: String, durationMs: Long) {

        val roomRef = rooms.document(roomId)

        store.runTransaction { tx ->
            val snapRoom = tx.get(roomRef)
            val old = snapRoom.getLong("tempsTotal") ?: 0L
            tx.update(roomRef, "tempsTotal", old + durationMs)
        }.await()
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

    suspend fun getPlayersRankedByTime(): List<LeaderboardEntry> {
        return users
            .orderBy("tempsTotal", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                val email = doc.getString("email") ?: return@mapNotNull null
                val value = doc.getLong("tempsTotal") ?: return@mapNotNull null
                LeaderboardEntry(email, value)
            }
    }

    suspend fun getPlayersRankedByPoints(): List<LeaderboardEntry> {
        return users
            .orderBy("points", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                val email = doc.getString("email") ?: return@mapNotNull null
                val value = doc.getLong("points") ?: return@mapNotNull null
                LeaderboardEntry(email, value)
            }
    }

    suspend fun getPlayersRankedByMilestones(): List<LeaderboardEntry> {
        return users
            .orderBy("jalonsTermines", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                val email = doc.getString("email") ?: return@mapNotNull null
                val value = doc.getLong("jalonsTermines") ?: return@mapNotNull null
                LeaderboardEntry(email, value)
            }
    }

    suspend fun getRoomsRankedByTime(): List<LeaderboardEntry> {
        return rooms
            .orderBy("tempsTotal", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                val name = doc.getString("name") ?: return@mapNotNull null
                val value = doc.getLong("tempsTotal") ?: return@mapNotNull null
                LeaderboardEntry(name, value)
            }
    }

    suspend fun getRoomsRankedByPoints(): List<LeaderboardEntry> {
        return rooms
            .orderBy("points", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                val name = doc.getString("name") ?: return@mapNotNull null
                val value = doc.getLong("points") ?: return@mapNotNull null
                LeaderboardEntry(name, value)
            }
    }

    suspend fun getRoomsRankedByMilestones(): List<LeaderboardEntry> {
        return rooms
            .orderBy("jalonsTermines", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                val name = doc.getString("name") ?: return@mapNotNull null
                val value = doc.getLong("jalonsTermines") ?: return@mapNotNull null
                LeaderboardEntry(name, value)
            }
    }

    suspend fun getPlayersRankedByLevel(): List<LeaderboardEntry> {
        return users
            .orderBy("level", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                val email = doc.getString("email") ?: return@mapNotNull null
                val level = doc.getLong("level") ?: return@mapNotNull null
                LeaderboardEntry(email, level)
            }
    }

    suspend fun getRoomsRankedByLevel(): List<LeaderboardEntry> {
        return rooms
            .orderBy("level", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                val name = doc.getString("name") ?: return@mapNotNull null
                val value = doc.getLong("level") ?: return@mapNotNull null
                LeaderboardEntry(name, value)
            }
    }

    suspend fun addExpToRoom(roomId: String, expGained: Long) {
        val ref = rooms.document(roomId)

        store.runTransaction { tx ->
            val snap = tx.get(ref)

            val currentExp = snap.getLong("exp") ?: 0L
            val currentLevel = (snap.getLong("level") ?: 1L).toInt()
            val currentPoints = snap.getLong("points") ?: 0L

            var newExp = currentExp + expGained
            var newLvl = currentLevel
            var gainedLevels = 0

            while (true) {
                val cost = newLvl * 50L
                if (newExp >= cost) {
                    newExp -= cost
                    newLvl++
                    gainedLevels++
                } else break
            }

            val newPoints = currentPoints + (gainedLevels * 250L)

            tx.update(
                ref,
                mapOf(
                    "exp" to newExp,
                    "level" to newLvl,
                    "points" to newPoints
                )
            )
        }.await()
    }

    fun getConnectedCount(uids: List<String>): Flow<Int> = callbackFlow {
        if (uids.isEmpty()) {
            trySend(0)
            close()
            return@callbackFlow
        }

        val now: () -> Long = { System.currentTimeMillis() }
        val ttl = 90000L // 90 sec

        val chunks = uids.chunked(10)

        val listeners = chunks.map { chunk ->
            store.collection("Users")
                .whereIn(FieldPath.documentId(), chunk)
                .addSnapshotListener { snap, _ ->
                    if (snap != null) {

                        val count = max(0,(snap.documents.count { doc ->
                            val lastSeen = doc.getTimestamp("lastSeen")?.toDate()?.time ?: 0L
                            (now() - lastSeen) < ttl
                        }-1)) // On ne veut que les autres utilisateurs

                        trySend(count)
                    }
                }
        }

        awaitClose { listeners.forEach { it.remove() } }
    }

    suspend fun setUserConnected(uid: String, value: Boolean) {
        users.document(uid)
            .update("isConnected", value)
            .await()
    }

    suspend fun updateLastSeen(uid: String) {
        users.document(uid).update("lastSeen", com.google.firebase.Timestamp.now()).await()
    }

    fun getUserWorkedTimeFlow(uid: String): Flow<Long> = callbackFlow {
        val reg = users.document(uid).addSnapshotListener { snap, _ ->
            val value = snap?.getLong("tempsTotal") ?: 0L
            trySend(value)
        }

        awaitClose { reg.remove() }
    }

    fun getUser(uid: String): Flow<User?> = callbackFlow {
        val reg = users
            .document(uid)
            .addSnapshotListener { snap, _ ->

                if (snap != null && snap.exists()) {

                    val prefsMap = snap.get("Preferences") as? Map<*, *> ?: emptyMap<String, Any>()
                    val prefs = UserPreferences(
                        darkMode = prefsMap["darkMode"] as? Boolean ?: false,
                        notifications = prefsMap["notifications"] as? Boolean ?: true
                    )

                    val user = User(
                        uid = snap.getString("uid") ?: uid,
                        email = snap.getString("email") ?: "",
                        level = snap.getLong("level")?.toInt() ?: 1,
                        exp = snap.getLong("exp")?.toInt() ?: 0,
                        friends = snap.get("friends") as? List<String> ?: emptyList(),
                        rooms = snap.get("rooms") as? List<String> ?: emptyList(),
                        comment = snap.getString("comment") ?: "",
                        points = snap.getLong("points")?.toInt() ?: 0,
                        tempsTotal = snap.getLong("tempsTotal") ?: 0L,
                        jalonsTermines = snap.getLong("jalonsTermines")?.toInt() ?: 0,
                        isConnected = snap.getBoolean("isConnected") ?: false,
                        preferences = prefs
                    )

                    trySend(user)

                } else {
                    trySend(null)
                }
            }

        awaitClose { reg.remove() }
    }


    fun addFriend(
        uid: String
    ) {
        //TODO
    }

    suspend fun updatePreference(uid: String, key: String, value: Any) {
        users
            .document(uid)
            .update("Preferences.$key", value)
            .await()
    }

    suspend fun clearStats(uid: String) {
        users
            .document(uid)
            .update(
                mapOf(
                    "tempsTotal" to 0L,
                    "jalonsTermines" to 0,
                    "exp" to 0,
                    "level" to 1
                )
            )
            .await()
    }



}