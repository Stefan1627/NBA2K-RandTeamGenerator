package com.nba_team_rand_gen.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nba_team_rand_gen.R

@Composable
fun ProfileScreen(
    vm: ProfileViewModel,
    onHistoryClick: () -> Unit,
    onMyPostsClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    val state by vm.state.collectAsState()

    if (state.loading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) { CircularProgressIndicator() }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val placeholder: Painter? = runCatching {
            painterResource(id = R.drawable.ic_profile_svg)
        }.getOrNull()

        if (placeholder != null) {
            Image(
                painter = placeholder,
                contentDescription = "Profile photo",
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile photo",
                modifier = Modifier.size(96.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = state.displayName.ifEmpty { "Unnamed" },
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = state.email.ifEmpty { "no mail" },
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(Modifier.height(4.dp))

        Button(
            onClick = onHistoryClick,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("History", color = Color.Black)
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onMyPostsClick,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("My Posts", color = Color.Black)
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onEditProfileClick,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("Edit Profile", color = Color.Black)
        }
    }
}