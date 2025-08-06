package com.example.nba_team_rand_gen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoriteService: BaseActivity() {
    private lateinit var adapter: StringListAdapter
    private lateinit var recyclerView: RecyclerView
    private val data = mutableListOf<String>()
    val favorites = mutableSetOf<Int>()
    private val matchEntries = mutableListOf<Map<String,Any?>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_matches)

        if (supportActionBar != null) {
            supportActionBar?.elevation = 10F
            val drawable =
                ResourcesCompat.getDrawable(resources, R.drawable.action_bar_gradient, theme)
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
            layoutManager = LinearLayoutManager(this@FavoriteService)
            adapter = this@FavoriteService.adapter
        }

        loadMatches()

        val backBtn = findViewById<Button>(R.id.back_btn)
        backBtn.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun loadMatches() {
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
                // 1) Load the raw favorites array
                @Suppress("UNCHECKED_CAST")
                val rawFavs = snap.get("favoritesList") as? List<*> ?: emptyList<Any>()

                // 2) Keep only the Map<String,Any> entries
                @Suppress("UNCHECKED_CAST")
                val favMaps = rawFavs.mapNotNull { elem ->
                    (elem as? Map<*, *>)
                        ?.filterKeys { it is String }
                        ?.mapKeys { it.key as String } as? Map<String, Any>
                }

                // 3) Remember old size for RecyclerView diff
                val oldSize = data.size

                // 4) Populate your lists from favMaps
                matchEntries.clear()
                matchEntries.addAll(favMaps)

                data.clear()
                data.addAll(favMaps.mapNotNull { it["name"] as? String })

                // 5) Since everything you load here is a favorite, mark all indices
                favorites.clear()
                favorites.addAll(data.indices)

                // 6) Notify adapter of the changes
                if (oldSize > 0) {
                    adapter.notifyItemRangeRemoved(0, oldSize)
                }
                if (data.isNotEmpty()) {
                    adapter.notifyItemRangeInserted(0, data.size)
                }

                // 7) Log what you’ve got
                data.forEachIndexed { idx, name ->
                    println("$name — favorite: true")
                }
            }
            .addOnFailureListener { e ->
                println("Failed to load favorites: ${e.message}")
            }
    }
}