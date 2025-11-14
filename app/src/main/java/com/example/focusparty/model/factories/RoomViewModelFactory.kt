package com.example.focusparty.model.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.focusparty.model.Database
import com.example.focusparty.viewmodel.RoomViewModel


class RoomViewModelFactory(
    private val db: Database,
    private val navController: NavController
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(clazz: Class<T>): T {
        if (clazz.isAssignableFrom(RoomViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoomViewModel(db,navController) as T
        }
        error("Unknown VM: $clazz")
    }
}