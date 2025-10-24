package com.example.nba_team_rand_gen.ui.screens.favorites

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FavoritesScreen(vm: FavoritesViewModel = viewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()
    LazyColumn {
        items(s.items) { m -> Text(m.name, color = Color.White, modifier = Modifier.padding(12.dp)) }
    }
}
