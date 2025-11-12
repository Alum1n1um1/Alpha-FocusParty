package com.example.focusparty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BooksViewModel(private val db: BooksDb) : ViewModel() {

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _books.value = withContext(Dispatchers.IO) { db.getAll() }
    }

    fun add(t: String, a: String, y: Int) = viewModelScope.launch(Dispatchers.IO) {
        db.insert(Book(titre = t, auteur = a, annee = y))
        refresh()
    }

    fun update(b: Book) = viewModelScope.launch(Dispatchers.IO) {
        db.update(b)
        refresh()
    }

    fun delete(b: Book) = viewModelScope.launch(Dispatchers.IO) {
        db.delete(b.id)
        refresh()
    }
}