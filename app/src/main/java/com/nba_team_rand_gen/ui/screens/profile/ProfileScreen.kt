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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.nba_team_rand_gen.R
import com.nba_team_rand_gen.ui.components.EmptyState
import com.nba_team_rand_gen.ui.components.ErrorState
import com.nba_team_rand_gen.ui.components.LoadingState
import kotlin.text.ifEmpty

@Composable
fun ProfileScreen(
    vm: ProfileViewModel = hiltViewModel(),
    onHistoryRoute: (String) -> Unit,
    onMyPostsRoute: (String) -> Unit,
    onEditProfileRoute: (String) -> Unit
) {
    val state by vm.state.collectAsState()

    when {
        state.loading -> LoadingState()
        state.error != null -> ErrorState(
            message = state.error ?: "Unknown error",
            onRetry = vm::refresh
        )
        state.displayName.isEmpty() && state.email.isEmpty() -> {
            EmptyState(
                title = "No profile",
                subtitle = "We could not load your profile. Try again"
            )
        }
        else -> ProfileContent(
            displayName = state.displayName,
            email = state.email,
            onHistoryRoute = { vm.onEvent(ProfileEvent.OnHistoryClick) },
            onMyPostsRoute = { vm.onEvent(ProfileEvent.OnMyPostsClick) },
            onEditProfileRoute = { vm.onEvent(ProfileEvent.OnEditProfileClick) }
        )
    }

    state.navigateToHistory?.let {
        onHistoryRoute(it)
        vm.onEvent(ProfileEvent.NavConsumed)
    }
    state.navigateToMyPosts?.let {
        onMyPostsRoute(it)
        vm.onEvent(ProfileEvent.NavConsumed)
    }
    state.navigateToEditProfile?.let {
        onEditProfileRoute(it)
        vm.onEvent(ProfileEvent.NavConsumed)
    }
}

@Composable
private fun ProfileContent(
    displayName: String,
    email: String,
    onHistoryRoute: () -> Unit,
    onMyPostsRoute: () -> Unit,
    onEditProfileRoute: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileAvatar()
        Spacer(Modifier.height(16.dp))
        ProfileInfo(displayName = displayName, email = email)
        Spacer(Modifier.height(16.dp))
        ProfileActions(onHistoryRoute, onMyPostsRoute, onEditProfileRoute)
    }
}

@Composable
private fun ProfileAvatar() {
    val placeholder: Painter? = runCatching {
        painterResource(id = R.drawable.ic_profile_svg)
    }.getOrNull()

    if (placeholder != null) {
        Image(
            painter = placeholder,
            contentDescription = "Profile photo",
            modifier = Modifier.size(96.dp).clip(CircleShape),
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
}

@Composable
private fun ProfileInfo(
    displayName: String,
    email: String
) {
    Text(
        text = displayName.ifEmpty { "Unnamed" },
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White.copy(alpha = 0.8f)
    )

    Spacer(Modifier.height(4.dp))

    Text(
        text = email.ifEmpty { "no mail" },
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White.copy(alpha = 0.8f)
    )
}

@Composable
private fun ProfileActions(
    onHistoryRoute: () -> Unit,
    onMyPostsRoute: () -> Unit,
    onEditProfileRoute: () -> Unit
) {
    Spacer(Modifier.height(4.dp))

    Button(
        onClick = onHistoryRoute,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
    ) {
        Text("History", color = Color.Black)
    }

    Spacer(Modifier.height(12.dp))

    Button(
        onClick = onMyPostsRoute,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
    ) {
        Text("My Posts", color = Color.Black)
    }

    Spacer(Modifier.height(12.dp))

    Button(
        onClick = onEditProfileRoute,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
    ) {
        Text("Edit Profile", color = Color.Black)
    }
}
