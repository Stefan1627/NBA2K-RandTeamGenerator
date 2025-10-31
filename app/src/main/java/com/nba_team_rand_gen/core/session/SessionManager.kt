package com.nba_team_rand_gen.core.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionManager private constructor(
    private val prefs: SharedPreferences
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _isValid = MutableStateFlow(checkValid())
    val isValid: StateFlow<Boolean> = _isValid.asStateFlow()

    init {
        scope.launch {
            while (isActive) {
                _isValid.value = checkValid()
                delay(5_000)
            }
        }
    }

    private fun checkValid(): Boolean {
        val expiry = prefs.getLong(KEY_EXPIRY, 0L)
        return System.currentTimeMillis() < expiry
    }

    fun start(hours: Long = 1L) {
        val expiry = System.currentTimeMillis() + hours * 60 * 60 * 1000
        prefs.edit { putLong(KEY_EXPIRY, expiry) }
        _isValid.value = true
    }

    fun clear() {
        prefs.edit { remove(KEY_EXPIRY) }
        _isValid.value = false
    }

    companion object {
        private const val PREFS = "session_prefs"
        private const val KEY_EXPIRY = "expiry_epoch_millis"

        @Volatile private var INSTANCE: SessionManager? = null

        fun from(context: Context): SessionManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(
                    context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                ).also { INSTANCE = it }
            }
    }
}