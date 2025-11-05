package com.nba_team_rand_gen.ui.screens.edit_profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nba_team_rand_gen.ui.components.EmptyState
import com.nba_team_rand_gen.ui.components.ErrorState
import com.nba_team_rand_gen.ui.components.LoadingState


@Composable
fun EditProfileScreen(
    navController: NavHostController,
    vm: EditProfileScreenViewModel = hiltViewModel(),
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
                title = "Nothing to edit",
                subtitle = "We could not load your profile. Try again"
            )
        }
        else -> EditProfileContent(
            displayName = state.displayName,
            email = state.email,
            onNameChange = { vm.onEvent(EditProfileEvent.OnNameChanged(it)) },
            onEmailChange = { vm.onEvent(EditProfileEvent.OnEmailChanged(it)) },
            onSave = { vm.onEvent(EditProfileEvent.OnSaveClick) },
            onBack = { vm.onEvent(EditProfileEvent.OnBackClick) }
        )
    }


    state.navigate?.let { nav ->
        when (nav) {
            is EditProfileNav.To -> navController.navigate(nav.route)
            EditProfileNav.Back -> navController.popBackStack()
        }
        vm.onEvent(EditProfileEvent.NavConsumed)
    }
}


@Composable
private fun EditProfileContent(
    displayName: String,
    email: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = displayName,
            onValueChange = onNameChange,
            label = { Text("Full name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedTextColor = Color.LightGray,
                focusedTextColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                cursorColor = Color.Red
            )
        )


        Spacer(Modifier.height(12.dp))


        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedTextColor = Color.LightGray,
                focusedTextColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                cursorColor = Color.Red
            )
        )


        Spacer(Modifier.height(16.dp))


        Button(
            onClick = onSave,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save", color = Color.Black)
        }


        Spacer(Modifier.height(12.dp))


        Button(
            onClick = onBack,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel", color = Color.Black)
        }
    }
}