package com.example.nba_team_rand_gen

import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ManageMatches {
    companion object {
        private val matchEntries = mutableListOf<Map<String, Any>>()
        private val favorites = mutableSetOf<Int>()
        private lateinit var adapter: StringListAdapter

        fun uploadMatch(json: String, matchName: String): Task<Int> {
            // must have a logged-in user
            val user = FirebaseAuth.getInstance().currentUser
                ?: return Tasks.forResult(-1)

            val userDoc = FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.uid)

            // build the object we want to store in the array
            val entry = mapOf(
                "name" to matchName,
                "data" to json,
            )

            // try to append to the array
            return userDoc.update("matchesList", FieldValue.arrayUnion(entry))
                .continueWithTask { updateTask ->
                    if (updateTask.isSuccessful) {
                        // update succeeded
                        Tasks.forResult(0)
                    } else {
                        // fallback: create the array with this first entry
                        userDoc.set(
                            mapOf("matchesList" to listOf(entry)),
                            SetOptions.merge()
                        ).continueWith { setTask ->
                            if (setTask.isSuccessful) 0 else -1
                        }
                    }
                }
        }

        fun toggleFavorite(position: Int) {
            val user = FirebaseAuth.getInstance().currentUser
                ?: return

            val userDoc = FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)

            val entry = matchEntries[position]
            val isFav = favorites.contains(position)
            val op = if (isFav)
                FieldValue.arrayRemove(entry)
            else
                FieldValue.arrayUnion(entry)

            userDoc.update("favoritesList", op)
                .addOnSuccessListener {
                    if (isFav) favorites.remove(position) else favorites.add(position)
                    adapter.notifyItemChanged(position)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        MainActivity(),
                        "Couldn’t update favorites, please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        fun deleteMatch(position: Int) {
            Toast.makeText(MainActivity(), "Coming soon...", Toast.LENGTH_SHORT).show()
        }

        fun descriptionShow(position: Int) {
            Toast.makeText(MainActivity(), "Coming soon...", Toast.LENGTH_SHORT).show()
        }
    }
}