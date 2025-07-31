package com.example.nba_team_rand_gen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class MatchHistory : AppCompatActivity() {
    companion object {
        /**
         * Appends [json] to /users/{uid}/matchesList array.
         * @return Task<Int> that yields 0 on success, -1 otherwise.
         */
        fun uploadMatch(json: String): Task<Int> {
            // must have a logged-in user
            val user = FirebaseAuth.getInstance().currentUser
                ?: return Tasks.forResult(-1)

            val userDoc = FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.uid)

            // try to append to the array
            return userDoc.update("matchesList", FieldValue.arrayUnion(json))
                .continueWithTask { updateTask ->
                    if (updateTask.isSuccessful) {
                        // update succeeded
                        Tasks.forResult(0)
                    } else {
                        // fallback: create the array with this first entry
                        userDoc.set(
                            mapOf("matchesList" to listOf(json)),
                            SetOptions.merge()
                        ).continueWith { setTask ->
                            if (setTask.isSuccessful) 0 else -1
                        }
                    }
                }
        }

        /**
         * Fetches /users/{uid}/matchesList and prints each entry via println().
         * Prints an error message if no user is signed in, if the list is empty,
         * or if the fetch fails.
         */
        fun printMatchHistory() {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                println("Please log in first.")
                return
            }

            val userDoc = FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.uid)

            userDoc.get()
                .addOnSuccessListener { snapshot ->
                    @Suppress("UNCHECKED_CAST")
                    val matches = snapshot.get("matchesList") as? List<String> ?: emptyList()
                    if (matches.isEmpty()) {
                        println("No matches saved.")
                    } else {
                        matches.forEach { println(it) }
                    }
                }
                .addOnFailureListener { e ->
                    println("Failed to load history: ${e.message}")
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_matches)
        printMatchHistory()

        val backBtn = findViewById<Button>(R.id.back_btn)
        backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}