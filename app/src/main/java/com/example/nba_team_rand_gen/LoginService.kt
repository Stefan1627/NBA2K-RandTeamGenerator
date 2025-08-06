package com.example.nba_team_rand_gen

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth
import kotlin.time.Duration.Companion.hours

val SESSION_DURATION_MS = 1.hours.inWholeMilliseconds

class LoginService : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()

    private lateinit var etEmail:    EditText
    private lateinit var etPassword: EditText
    private lateinit var btnPrimary: Button
    private lateinit var btnToggle:  Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // bind views
        etEmail    = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnPrimary = findViewById(R.id.btnPrimary)
        btnToggle  = findViewById(R.id.btnToggleMode)

        // toggle between login <-> signup
        btnToggle.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // primary button does either login or sign up
        btnPrimary.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass  = etPassword.text.toString()
            if (email.isEmpty() || pass.isEmpty()) {
                return@setOnClickListener Toast
                    .makeText(this, "Email & password required", Toast.LENGTH_SHORT)
                    .show()
            }
            signIn(email, pass)
        }
    }

    private fun signIn(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onAuthSuccess()
                } else {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun onAuthSuccess() {
        // save session expiry…
        val expiry = System.currentTimeMillis() + SESSION_DURATION_MS
        getSharedPreferences("session_prefs", MODE_PRIVATE).edit {
            putLong("session_expiry", expiry)
        }
        // go to main
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}