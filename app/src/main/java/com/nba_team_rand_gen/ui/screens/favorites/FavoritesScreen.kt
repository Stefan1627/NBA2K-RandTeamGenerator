package com.nba_team_rand_gen.ui.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nba_team_rand_gen.ui.StringList
import com.nba_team_rand_gen.ui.components.EmptyState
import com.nba_team_rand_gen.ui.components.ErrorState
import com.nba_team_rand_gen.ui.components.LoadingState

@Composable
fun FavoritesHeader(
    onRefresh: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Favorites", color = Color.White, style = MaterialTheme.typography.titleLarge)
        TextButton(onClick = onRefresh) { Text("Refresh") }
    }
}

@Composable
fun FavoritesScreen(
    vm: FavoritesViewModel = hiltViewModel(),
) {
    val s by vm.state.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        FavoritesHeader(onRefresh = vm::refresh)
        Spacer(Modifier.height(8.dp))
        when {
            s.loading -> LoadingState()
            s.error != null -> ErrorState(
                message = s.error ?: "Unknown error",
                onRetry = vm::refresh
            )

            s.items.isEmpty() -> EmptyState(
                title = "No favorites yet",
                subtitle = "Salveaza meciuri ca favorite ca sa le vezi aici."
            )

            else -> StringList(
                items = s.items,
                onFavoriteClick = vm::onToggleFavorite,
                onTrashClick = vm::onDelete
            )
        }
    }
}
