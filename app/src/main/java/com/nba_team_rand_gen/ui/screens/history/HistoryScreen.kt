package com.nba_team_rand_gen.ui.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nba_team_rand_gen.ui.StringList

@Composable
fun HistoryScreen(vm: HistoryViewModel = viewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()

    when {
        s.loading -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(
            Alignment.Center)) }
        s.items.isEmpty() -> Box(Modifier.fillMaxSize()) {
            Text("No favorites yet",
                Modifier.align(Alignment.Center),
                color = Color.White)}
        else -> StringList(
            items = s.items,
            onFavoriteClick = vm::onToggleFavorite,
            onTrashClick = vm::onDelete
        )
    }
}