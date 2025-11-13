package com.example.focusparty

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.example.focusparty.view.*

class RegisterActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setContent {
            RegisterScreen(
                onRegisterClick = { email, password ->
                    registerUser(email, password)
                },
                onLoginClick = {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            )
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {

                                // Déconnexion immédiate après l'inscription
                                auth.signOut()

                                // Redirection obligatoire vers Login
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                        }
                }
            }
    }
}
