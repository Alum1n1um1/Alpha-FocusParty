package com.example.focusparty

import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BooksDb(
    private val store: FirebaseFirestore = Firebase.firestore
) {
    private val col = store.collection("books")

    /** Flux live de tous les livres (ordre: id desc si tu veux un champs "createdAt"). */
    fun observeAll(): Flow<List<Book>> = callbackFlow {
        val reg = col.addSnapshotListener { snap, err ->
            if (err != null) {
                // on peut close ou envoyer liste vide selon ton choix
                trySend(emptyList())
                return@addSnapshotListener
            }
            val list = snap?.documents?.mapNotNull { doc ->
                val titre  = doc.getString("titre") ?: return@mapNotNull null
                val auteur = doc.getString("auteur") ?: return@mapNotNull null
                val annee  = (doc.getLong("annee") ?: 0L).toInt()
                Book(id = doc.id, titre = titre, auteur = auteur, annee = annee)
            }.orEmpty()
            trySend(list)
        }
        awaitClose { reg.remove() }
    }

    /** Ajoute un livre et retourne l’id Firestore créé. */
    suspend fun insert(book: Book): String {
        val newRef = col.document()                // id auto
        val data = mapOf(
            "titre" to book.titre,
            "auteur" to book.auteur,
            "annee" to book.annee
        )
        newRef.set(data).await()
        return newRef.id
    }

    /** Met à jour un livre (id requis). */
    suspend fun update(book: Book) {
        require(book.id.toString().isNotBlank()) { "book.id requis pour update()" }
        val data = mapOf(
            "titre" to book.titre,
            "auteur" to book.auteur,
            "annee" to book.annee
        )
        col.document(book.id).set(data, SetOptions.merge()).await()
    }

    /** Supprime un livre par id. */
    suspend fun delete(id: String) {
        require(id.isNotBlank()) { "id requis pour delete()" }
        col.document(id).delete().await()
    }
}