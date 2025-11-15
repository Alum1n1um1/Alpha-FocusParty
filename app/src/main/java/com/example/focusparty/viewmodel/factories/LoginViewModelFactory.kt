package com.example.focusparty.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.focusparty.model.Database
import com.example.focusparty.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth


class LoginViewModelFactory(

) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(clazz: Class<T>): T {
        if (clazz.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel() as T
        }
        error("Unknown VM: $clazz")
    }
}