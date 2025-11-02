package com.nba_team_rand_gen.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun LoginScreen(
    vm: LoginViewModel = hiltViewModel(),
    onLoggedIn: () -> Unit = {},
    onCreateAccount: () -> Unit = {}
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) {
            onLoggedIn()
            vm.onEvent(LoginEvent.SuccessConsumed)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome back", style = MaterialTheme.typography.headlineSmall, color = Color.White)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = state.email,
            onValueChange = { vm.onEvent(LoginEvent.EmailChanged(it)) },
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
            onValueChange = { vm.onEvent(LoginEvent.PasswordChanged(it)) },
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
            onClick = { vm.onEvent(LoginEvent.Submit) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            )
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Sign in")
            }
        }

        OutlinedButton(
            onClick = onCreateAccount,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White,
                containerColor = Color.Red),
            border = BorderStroke(1.dp, Color.White)
        ) {
            Text("Create account")
        }

        if (state.error != null) {
            Spacer(Modifier.height(16.dp))
            Snackbar { Text(state.error!!) }
        }
    }
}