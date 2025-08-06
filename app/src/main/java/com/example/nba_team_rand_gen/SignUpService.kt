package com.example.nba_team_rand_gen

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

class SignUpActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()

    private lateinit var etFullName: EditText
    private lateinit var etEmail:    EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignUp:  Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        etFullName = findViewById(R.id.etFullname)
        etEmail    = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignUp  = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            val name = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val pass  = etPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // create & profile-update
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }
                    auth.currentUser
                        ?.updateProfile(userProfileChangeRequest { displayName = name })
                        ?.addOnCompleteListener {
                            // go to main
                            onAuthSuccess()
                        }
                }
        }
    }

    private fun onAuthSuccess() {
        val expiry = System.currentTimeMillis() + SESSION_DURATION_MS
        getSharedPreferences("session_prefs", MODE_PRIVATE).edit {
            putLong("session_expiry", expiry)
        }
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity() // clear back-stack
    }
}
