package com.nba_team_rand_gen.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nba_team_rand_gen.data.model.Match

@Composable
fun StringList(
    items: List<Match>,                          // id, name, isFavorite
    onFavoriteClick: (id: String) -> Unit,
    onTrashClick: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        itemsIndexed(
            items = items,
            key = { _, m -> m.id }                  // stable key by ID
        ) { index, m ->
            Column {
                StringListItem(
                    text = m.name,
                    isFavorite = m.favorite,
                    onFavoriteClick = { onFavoriteClick(m.id) },
                    onTrashClick = { onTrashClick(m.id) }
                )
                if (index != items.lastIndex) Divider()
            }
        }
    }
}

@Composable
private fun StringListItem(
    text: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onTrashClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, color = Color.White)
        // Replaces your ImageButton with "selected" state
        IconToggleButton(
            checked = isFavorite,
            onCheckedChange = { onFavoriteClick() }
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite) "Unfavorite" else "Favorite"
            )
        }

        // Trash button
        IconButton(onClick = onTrashClick) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete"
            )
        }
    }
}