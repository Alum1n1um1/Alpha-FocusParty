package com.example.focusparty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BooksViewModel(
    private val db: BooksDb
) : ViewModel() {

    val books = db.observeAll().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    fun add(titre: String, auteur: String, annee: Int) = viewModelScope.launch {
        db.insert(Book(titre = titre, auteur = auteur, annee = annee))
    }

    fun update(b: Book) = viewModelScope.launch { db.update(b) }

    fun delete(b: Book) = viewModelScope.launch { db.delete(b.id.toString()) }
}