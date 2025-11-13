package com.example.focusparty.model

import com.google.firebase.Firebase
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
    private val users = store.collection("Users")
    private val rooms = store.collection("rooms")
    private val events = store.collection("Events")

    fun addUser(uid: String, email:String){ // Ajoute un utilisateur
        val user = hashMapOf(
            "uid" to uid,
            "email" to email,
            "level" to 1,
            "exp" to 0,
            "friends" to {},
            "rooms" to {},
            "comment" to "",
            "first_connection" to true
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

    fun getRoomsOf(uid:String) { // Récupère les salons de l'utilisateur
        TODO()
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



}