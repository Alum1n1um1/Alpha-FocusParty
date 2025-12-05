package com.example.focusparty.viewmodel.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.focusparty.model.Database
import com.example.focusparty.viewmodel.AccountCustomizationViewModel

class AccountCustomizationViewModelFactory(
    private val db: Database,
    private val navController: NavController,
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(AccountCustomizationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountCustomizationViewModel(
                db = db,
                navController = navController,
                application = application
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
