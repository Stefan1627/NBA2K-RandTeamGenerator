package com.nba_team_rand_gen.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun SignUpScreen(
    vm: SignUpViewModel = hiltViewModel(),
    onSignInClick: () -> Unit = {}
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) {
            vm.onEvent(SignUpEvent.SuccessConsumed)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create your account",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = state.fullName,
            onValueChange = { vm.onEvent(SignUpEvent.FullNameChanged(it)) },
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
            value = state.email,
            onValueChange = { vm.onEvent(SignUpEvent.EmailChanged(it)) },
            label = { Text("Email") },
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
            value = state.password,
            onValueChange = { vm.onEvent(SignUpEvent.PasswordChanged(it)) },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
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
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { vm.onEvent(SignUpEvent.Submit) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            )
        ) {
            if (state.isLoading) CircularProgressIndicator() else Text("Sign up")
        }

        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onSignInClick) {
            Text("Already have an account? Sign in", color = Color.White)
        }

        if (state.error != null) {
            Spacer(Modifier.height(16.dp))
            Snackbar { Text(state.error!!) }
        }
    }
}