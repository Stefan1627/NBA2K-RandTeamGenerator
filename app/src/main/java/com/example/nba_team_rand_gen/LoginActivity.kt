package com.example.nba_team_rand_gen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val next = if (isUserLoggedIn()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginService::class.java)
        }

        startActivity(next)
        finish()
    }

    private fun isUserLoggedIn(): Boolean {
        val prefs = getSharedPreferences("session_prefs", MODE_PRIVATE)
        var expiry = prefs.getLong("session_expiry", -1L)
        if (auth.currentUser != null && expiry > System.currentTimeMillis()) {
            return true
        } else {
            auth.signOut()
            prefs.edit { clear() }
            return false
        }
    }

//    override fun onStart() {
//        super.onStart()
//        auth.currentUser?.let {
//            val prefs = getSharedPreferences("session_prefs", MODE_PRIVATE)
//            if (prefs.getLong("session_expiry", 0L) > System.currentTimeMillis()) {
//                startActivity(Intent(this, MainActivity::class.java))
//                finish()
//            }
//        }
//    }
}