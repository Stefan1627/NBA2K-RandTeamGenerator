package com.nba_team_rand_gen.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LoadingState(
    modifier: Modifier = Modifier
) {
    Box(Modifier.fillMaxSize().then(modifier)) {
        CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
}

@Composable
fun EmptyState(
    title: String,
    subtitle: String? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    Box(Modifier.fillMaxSize().then(modifier)) {
        Column(
            Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = Color.White)
            if (subtitle != null) {
                Spacer(Modifier.height(8.dp))
                Text(subtitle, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(Modifier.fillMaxSize().then(modifier)) {
        Column(
            Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(message, color = Color.Red)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}