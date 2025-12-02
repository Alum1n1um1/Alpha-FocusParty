package com.example.focusparty.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.example.focusparty.model.Database
import com.example.focusparty.viewmodel.LeaderboardViewModel

class LeaderboardViewModelFactory(
    private val db: Database,
    private val navController: NavHostController
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaderboardViewModel::class.java)) {
            return LeaderboardViewModel(db, navController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
