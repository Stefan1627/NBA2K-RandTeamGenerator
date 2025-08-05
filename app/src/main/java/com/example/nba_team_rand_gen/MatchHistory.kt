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
    private val matchEntries = mutableListOf<Map<String,Any>>()
    private val data = mutableListOf<String>()
    val favorites = mutableSetOf<Int>()
    private lateinit var adapter: StringListAdapter

    companion object {
        private const val TAG = "MatchHistory"
        /**
         * Appends [json] to /users/{uid}/matchesList array.
         * @return Task<Int> that yields 0 on success, -1 otherwise.
         */
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
                "name"      to matchName,
                "data"      to json,
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_matches)

        if (supportActionBar != null) {
            supportActionBar?.elevation = 10F
            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.action_bar_gradient, theme)
            supportActionBar?.setBackgroundDrawable(drawable)
        }

        adapter = StringListAdapter(
            items           = data,
            favorites       = favorites,
            onFavoriteClick = { pos -> toggleFavorite(pos) },
            onTrashClick   = { pos -> deleteMatch(pos) },
            onDescriptionClick = { pos -> descriptionShow(pos)}
        )

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
                val matches = snap.get("matchesList") as? List<Map<String,Any>> ?: emptyList()

                val rawFavs = snap.get("favoritesList") as? List<*> ?: emptyList<Any>()

                val favMaps = rawFavs.mapNotNull { elem ->
                    when (elem) {
                        is Map<*,*> -> @Suppress("UNCHECKED_CAST")
                        (elem as? Map<String,Any>)
                        else         -> null
                    }
                }

                val oldSize = data.size

                matchEntries.clear(); matchEntries.addAll(matches)
                data.clear(); data.addAll(matches.mapNotNull { it["name"] as? String })

                val newFavs = matches.indices
                    .filter { idx ->
                        val e = matches[idx]
                        favMaps.any { it["name"] == e["name"] && it["data"] == e["data"] }
                    }
                    .toSet()
                favorites.clear(); favorites.addAll(newFavs)

                if (oldSize > 0) {
                    adapter.notifyItemRangeRemoved(0, oldSize)
                }
                if (data.isNotEmpty()) {
                    adapter.notifyItemRangeInserted(0, data.size)
                }

                data.forEachIndexed { idx, name ->
                    println("$name — favorite: ${favorites.contains(idx)}")
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
        val user = FirebaseAuth.getInstance().currentUser
            ?: return

        val userDoc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)

        val entry = matchEntries[position]
        val isFav = favorites.contains(position)
        val op    = if (isFav)
            FieldValue.arrayRemove(entry)
        else
            FieldValue.arrayUnion(entry)

        userDoc.update("favoritesList", op)
            .addOnSuccessListener {
                if (isFav) favorites.remove(position) else favorites.add(position)
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

    private fun deleteMatch(position: Int) {
        Toast.makeText(this, "Coming soon...", Toast.LENGTH_SHORT).show()
    }

    private fun descriptionShow(position: Int) {
        Toast.makeText(this, "Coming soon...", Toast.LENGTH_SHORT).show()
    }
}