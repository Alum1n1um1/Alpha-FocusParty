package com.example.focusparty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.focusparty.model.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow


class RegisterViewModel(
    private val db: Database,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess = _registerSuccess

    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val user = auth.currentUser ?: return@addOnSuccessListener

                user.sendEmailVerification().addOnSuccessListener {
                    db.addUser(user.uid, email)
                    auth.signOut()
                    _registerSuccess.value = true
                }
            }
    }
}
