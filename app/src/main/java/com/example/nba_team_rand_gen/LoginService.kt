package com.example.nba_team_rand_gen

import android.content.Context
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth

const val SESSION_DURATION_MS = 96L * 60 * 60 * 1000

class LoginService : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin   = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etUsername.text.toString().trim()
            val pass  = etPassword.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authenticate(email, pass) { success ->
                Toast.makeText(
                    this,
                    if (success) "Logged in" else "Not logged in",
                    Toast.LENGTH_SHORT
                ).show()
                val expiry = System.currentTimeMillis() + SESSION_DURATION_MS
                getSharedPreferences("session_prefs", Context.MODE_PRIVATE)
                    .edit {
                        putLong("session_expiry", expiry)
                    }
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun authenticate(email: String, password: String, callback: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }
}