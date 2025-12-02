package com.example.focusparty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.focusparty.model.Database

class ShopViewModel(
    private val db: Database,
    private val navController: NavController
) : ViewModel(){
    fun goToDestination(dest : String ){
        navController.navigate(dest)
    }
}