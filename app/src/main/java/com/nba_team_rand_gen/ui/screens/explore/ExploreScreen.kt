package com.nba_team_rand_gen.ui.screens.explore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun ExploreScreen(
    vm: ExploreScreenViewModel = hiltViewModel()
) {
    val uiState by vm.state.collectAsState()

    // State for the scroll position
    val listState = rememberLazyListState()

    // Logic to detect when we scroll to the bottom
    val reachedBottom by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            // Trigger when the user is seeing the 2nd to last item
            lastVisibleItem != null &&
                    lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 2
        }
    }

    // Trigger the "Load More" event
    LaunchedEffect(reachedBottom) {
        if (reachedBottom) {
            vm.loadNextPage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Initial Loading State (Center Spinner)
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        // 2. Error State
        else if (uiState.error != null && uiState.posts.isEmpty()) {
            Text(
                text = "Error: ${uiState.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        // 3. List of Posts
        else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = uiState.posts, key = { it.id }) { post ->
                    PostItem(post = post)
                }

                // 4. Pagination Loading State (Bottom Spinner)
                if (uiState.isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }
            }
        }
    }
}