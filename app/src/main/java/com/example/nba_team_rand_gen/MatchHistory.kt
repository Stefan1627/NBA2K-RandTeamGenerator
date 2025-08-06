package com.example.nba_team_rand_gen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MatchHistory : BaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private val matchEntries = mutableListOf<Map<String,Any>>()
    private val data = mutableListOf<String>()
    val favorites = mutableSetOf<Int>()
    private lateinit var adapter: StringListAdapter

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
            onFavoriteClick = { pos -> ManageMatches.toggleFavorite(pos) },
            onTrashClick   = { pos -> ManageMatches.deleteMatch(pos) },
            onDescriptionClick = { pos -> ManageMatches.descriptionShow(pos)}
        )

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MatchHistory)
            adapter = this@MatchHistory.adapter
        }

        loadMatchesAndFavorites()

        val backBtn = findViewById<Button>(R.id.back_btn)
        backBtn.setOnClickListener{
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
            }
    }
}