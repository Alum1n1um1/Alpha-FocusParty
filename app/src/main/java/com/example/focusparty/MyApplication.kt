package com.example.focusparty

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.focusparty.model.Database
import com.example.focusparty.utils.AppLifecycleListener
import com.google.firebase.auth.FirebaseAuth

class MyApplication : Application() {

    companion object {
        lateinit var lifecycleListener: AppLifecycleListener
            private set
    }

    override fun onCreate() {
        super.onCreate()

        val auth = FirebaseAuth.getInstance()
        val db = Database.getInstance()

        lifecycleListener = AppLifecycleListener(
            uidProvider = { auth.currentUser?.uid },
            db = db
        )

        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleListener)
    }
}

