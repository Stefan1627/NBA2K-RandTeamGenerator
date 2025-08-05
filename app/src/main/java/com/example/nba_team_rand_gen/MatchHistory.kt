package com.example.nba_team_rand_gen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class MatchHistory : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val data = mutableListOf<String>()
    val favorites = mutableSetOf<Int>()
    private lateinit var adapter: StringListAdapter

    companion object {
        private const val TAG = "MatchHistory"
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_matches)

        if (supportActionBar != null) {
            supportActionBar?.elevation = 10F
            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.action_bar_gradient, theme)
            supportActionBar?.setBackgroundDrawable(drawable)
        }

        adapter = StringListAdapter(data, favorites) { pos ->
            toggleFavorite(pos)
        }

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MatchHistory)
            adapter = this@MatchHistory.adapter
        }

        loadMatchesAndFavorites()

        val backbtn = findViewById<Button>(R.id.back_btn)
        backbtn.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    /**
     * 1. Fetches both "matchesList" and "favoritesList"
     * 2. Fills `data` and `favorites`
     * 3. Prints each match + favorite state
     * 4. Notifies adapter
     */
    private fun loadMatchesAndFavorites() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            println("Please log in first.")
            return
        }

        val userDoc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)

        userDoc.get()
            .addOnSuccessListener { snap ->
                @Suppress("UNCHECKED_CAST")
                val matches = snap.get("matchesList") as? List<String> ?: emptyList()
                @Suppress("UNCHECKED_CAST")
                val favs    = snap.get("favoritesList") as? List<String> ?: emptyList()

                // build new favorites index set
                val newFavs = mutableSetOf<Int>()
                matches.forEachIndexed { idx, json ->
                    if (favs.contains(json)) newFavs.add(idx)
                }

                // remember old size, then apply changes
                val oldSize = data.size
                data.clear()
                data.addAll(matches)
                favorites.clear()
                favorites.addAll(newFavs)

                // now fire specific change events
                if (oldSize > 0) {
                    adapter.notifyItemRangeRemoved(0, oldSize)
                }
                if (data.isNotEmpty()) {
                    adapter.notifyItemRangeInserted(0, data.size)
                }

                // print each with its favorite flag
                data.forEachIndexed { idx, json ->
                    println("$json — favorite: ${favorites.contains(idx)}")
                }
            }
            .addOnFailureListener { e ->
                println("Failed to load histories: ${e.message}")
                Log.e(TAG, "loadMatchesAndFavorites", e)
            }
    }

    /**
     * Toggle locally + in Firestore, then redraw item.
     */
    private fun toggleFavorite(position: Int) {
        val matchJson = data[position]
        val user = FirebaseAuth.getInstance().currentUser
            ?: return  // no‐one’s signed in

        val userDoc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)

        // 1) check current state
        val isCurrentlyFav = favorites.contains(position)

        // 2) pick the right FieldValue op
        val op = if (isCurrentlyFav) {
            FieldValue.arrayRemove(matchJson)
        } else {
            FieldValue.arrayUnion(matchJson)
        }

        // 3) send it to Firestore
        userDoc.update("favoritesList", op)
            .addOnSuccessListener {
                // 4) only now update your local set + UI
                if (isCurrentlyFav) {
                    favorites.remove(position)
                } else {
                    favorites.add(position)
                }
                adapter.notifyItemChanged(position)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "toggleFavorite failed", e)
                Toast.makeText(this,
                    "Couldn’t update favorites, please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}