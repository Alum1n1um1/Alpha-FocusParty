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

    fun addRoom(room: Room){
        TODO()
    }

    fun getRooms(): Flow<List<Room>> = callbackFlow { // Récupère toutes les rooms
        val col = store.collection("rooms")
        val reg = col.addSnapshotListener { snap, err ->
            if (err != null) {
                // on peut close ou envoyer liste vide selon ton choix
                trySend(emptyList())
                return@addSnapshotListener
            }
            val list = snap?.documents?.mapNotNull { doc ->
                val name = doc.getString("name") ?: ""
                val owner = doc.getString("owner") ?: ""
                val description = doc.getString("description") ?: ""
                val status = (doc.getLong("status") ?: 0L).toInt()
                val members = doc.get("members") as? List<String> ?: emptyList()
                val jalons = doc.get("jalons") as? List<String> ?: emptyList()

                Room(name = name,
                    owner = owner,
                    description = description,
                    status = status,
                    members = members,
                    jalons = jalons
                )

            }.orEmpty()
            trySend(list)
        }
        awaitClose { reg.remove() }
    }

    fun getRoomsOf(uid:String) : Flow<List<Room>> = callbackFlow { // Récupère les salons de l'utilisateur
        val query = store
            .collection("rooms")
            .whereArrayContains("members", uid)

        val registration = query.addSnapshotListener { snap, err ->
            if (err != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            val list = snap?.documents?.mapNotNull { doc ->
                Room(
                    name = doc.getString("name") ?: "",
                    owner = doc.getString("owner") ?: "",
                    description = doc.getString("description") ?: "",
                    status = (doc.getLong("status") ?: 0L).toInt(),
                    members = doc.get("members") as? List<String> ?: emptyList(),
                    jalons = doc.get("jalons") as? List<String> ?: emptyList()
                )
            }.orEmpty()

            trySend(list)
        }

        awaitClose { registration.remove() }
    }


    fun getUsers() { // Récupère tous les utilisateurs
        TODO()
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

    fun getFriends(user:String) { // Récupère les amis de l'utilisateur
        TODO()
    }

    fun getTodo() {
        TODO()
    }

    fun getUser() {
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