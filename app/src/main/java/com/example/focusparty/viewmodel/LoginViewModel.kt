package com.example.focusparty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.focusparty.model.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow


class LoginViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful && auth.currentUser?.isEmailVerified == true) {
                    _loginSuccess.value = true
                }
            }
    }
}
