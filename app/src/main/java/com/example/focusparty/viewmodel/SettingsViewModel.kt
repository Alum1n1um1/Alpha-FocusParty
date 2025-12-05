package com.example.focusparty.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.focusparty.MyApplication
import com.example.focusparty.model.Database
import com.example.focusparty.model.User
import com.example.focusparty.model.auth
import com.example.focusparty.model.uid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val db: Database,
    private val navController: NavController,
    application: Application
) : AndroidViewModel(application) {

    private val userFlow: StateFlow<User?> =
        db.getUser(uid)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val darkMode: StateFlow<Boolean> =
        userFlow.map { it?.preferences?.darkMode ?: false }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val notifications: StateFlow<Boolean> =
        userFlow.map { it?.preferences?.notifications ?: true }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            db.updatePreference(uid, "darkMode", enabled)
        }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch {
            db.updatePreference(uid, "notifications", enabled)
        }
    }

    fun clearStats() {
        viewModelScope.launch {
            db.clearStats(uid)
        }
    }

    fun logout() {
        if (uid != null) {
            MyApplication.lifecycleListener.stopHeartbeat()

            CoroutineScope(Dispatchers.IO).launch {
                db.setUserConnected(uid, false)
            }
        }

        auth.signOut()

        navController.navigate("Login") {
            popUpTo("Home") { inclusive = true }
            launchSingleTop = true
        }
    }



}