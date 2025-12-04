package com.example.focusparty.utils

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.focusparty.model.Database
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AppLifecycleListener(
    private val uidProvider: () -> String?,
    private val db: Database
) : DefaultLifecycleObserver {

    private var heartbeatJob: Job? = null


    override fun onStop(owner: LifecycleOwner) {
        val uid = uidProvider() ?: return

        CoroutineScope(Dispatchers.IO).launch {
            db.setUserConnected(uid, false)
        }

        stopHeartbeat()
    }

    fun startHeartbeatFor(uid: String) {
        stopHeartbeat() // sécurité

        heartbeatJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                db.updateLastSeen(uid)
                delay(60000)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            db.setUserConnected(uid, true)
        }
    }

    fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

}
