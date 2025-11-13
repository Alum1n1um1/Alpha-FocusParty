package com.example.focusparty.model.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.focusparty.model.Database
import com.example.focusparty.viewmodel.HomeViewModel


class HomeViewModelFactory(
    private val db: Database
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(clazz: Class<T>): T {
        if (clazz.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(db) as T
        }
        error("Unknown VM: $clazz")
    }
}