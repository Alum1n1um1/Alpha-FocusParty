package com.example.focusparty.model

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

        val doc = rooms.document() // ID auto-généré

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
            }
        )

        doc.set(data)
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

                Room(
                    id = id,
                    name = name,
                    owner = owner,
                    description = description,
                    status = status,
                    members = members,
                    jalons = jalons
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

                trySend(
                    Room(
                        id = roomId,
                        name = name,
                        owner = owner,
                        description = description,
                        status = status,
                        members = members,
                        jalons = jalons
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
}