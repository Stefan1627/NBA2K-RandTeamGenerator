package com.nba_team_rand_gen.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nba_team_rand_gen.data.model.Match
import com.nba_team_rand_gen.R

@Composable
fun StringList(
    items: List<Match>,
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
            key = { _, m -> m.id }
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
        Text(
            text = text,
            color = Color.White,
            fontSize = 25.sp,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconToggleButton(
                checked = isFavorite,
                onCheckedChange = { onFavoriteClick() }
            ) {
                Icon(
                    painter = if (isFavorite) painterResource(id = R.drawable.fav_sel_svg) else
                        painterResource(id = R.drawable.fav_unsel_svg),
                    contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                    tint = Color.Unspecified
                )
            }

            // Trash button
            IconButton(onClick = onTrashClick) {
                Icon(
                    painter = painterResource(id = R.drawable.trash_svg),
                    contentDescription = "Delete",
                    tint = Color.Unspecified
                )
            }
        }
    }
}