package com.example.focusparty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BooksVMFactory(private val db: BooksDb) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BooksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BooksViewModel(db) as T
        }
        error("Unknown VM")
    }
}