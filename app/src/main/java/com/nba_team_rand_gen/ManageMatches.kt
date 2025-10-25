package com.nba_team_rand_gen

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ManageMatches {
    companion object {
        // Hold references to the screen’s state
        private var matchEntries: MutableList<Map<String, Any?>>? = null
        private var favorites: MutableSet<Int>? = null
        private var adapter: StringListAdapter? = null
        private var appContext: Context? = null

        /** Call this once from the screen after you create the adapter */
        fun bind(
            entries: MutableList<Map<String, Any?>>,
            favs: MutableSet<Int>,
            adapterRef: StringListAdapter,
            context: Context
        ) {
            matchEntries = entries
            favorites = favs
            adapter = adapterRef
            appContext = context.applicationContext
        }

        fun toggleFavorite(position: Int) {
            val entries = matchEntries
            val favs = favorites
            val ctx = appContext

            if (entries == null || favs == null || ctx == null || adapter == null) {
                // Not bound yet; safely ignore or log
                return
            }
            if (position !in entries.indices) {
                // Defensive guard against stale clicks
                return
            }

            val user = FirebaseAuth.getInstance().currentUser ?: return
            val userDoc = FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)

            val entry = entries[position]
            val isFav = favs.contains(position)
            val op = if (isFav) FieldValue.arrayRemove(entry) else FieldValue.arrayUnion(entry)

            userDoc.update("favoritesList", op)
                .addOnSuccessListener {
                    if (isFav) favs.remove(position) else favs.add(position)
                    adapter?.notifyItemChanged(position)
                }
                .addOnFailureListener {
                    Toast.makeText(
                        ctx,
                        "Couldn’t update favorites, please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        fun deleteMatch(position: Int) {
            appContext?.let {
                Toast.makeText(it, "Coming soon...", Toast.LENGTH_SHORT).show()
            }
        }

        fun descriptionShow(position: Int) {
            appContext?.let {
                Toast.makeText(it, "Coming soon...", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
