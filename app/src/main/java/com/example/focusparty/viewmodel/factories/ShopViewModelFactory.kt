package com.example.focusparty.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.example.focusparty.model.Database
import com.example.focusparty.viewmodel.ShopViewModel

class ShopViewModelFactory(
    private val db: Database,
    private val navController: NavHostController
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
            return ShopViewModel(db, navController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
