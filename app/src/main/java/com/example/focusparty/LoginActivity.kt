package com.example.focusparty

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.example.focusparty.view.*
class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setContent {
            LoginScreen(
                onLoginClick = { email, password ->
                    loginUser(email, password)
                },
                onRegisterClick = {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
            )
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            }
    }
}
