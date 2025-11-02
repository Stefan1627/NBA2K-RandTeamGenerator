package com.nba_team_rand_gen.ui.screens.edit_profile

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.nba_team_rand_gen.ui.auth.SignUpEvent

@Composable
fun EditProfileScreen(
    vm : EditProfileScreenViewModel = hiltViewModel(),
    onSaveClick: () -> Unit
) {
    val state by vm.state.collectAsState()

//    OutlinedTextField(
//        value = state.fullName,
//        onValueChange = { vm.onEvent(SignUpEvent.FullNameChanged(it)) },
//        label = { Text("Full name") },
//        singleLine = true,
//        modifier = Modifier.fillMaxWidth(),
//        colors = OutlinedTextFieldDefaults.colors(
//            unfocusedBorderColor = Color.White,
//            focusedBorderColor = Color.White,
//            unfocusedTextColor = Color.LightGray,
//            focusedTextColor = Color.White,
//            focusedLabelColor = Color.White,
//            unfocusedLabelColor = Color.White,
//            cursorColor = Color.Red
//        )
//    )
//    Spacer(Modifier.height(12.dp))

//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//    }

//    OutlinedTextField(
//        value = state.email,
//        onValueChange = { vm.onEvent(SignUpEvent.EmailChanged(it)) },
//        label = { Text("Email") },
//        singleLine = true,
//        modifier = Modifier.fillMaxWidth(),
//        colors = OutlinedTextFieldDefaults.colors(
//            unfocusedBorderColor = Color.White,
//            focusedBorderColor = Color.White,
//            unfocusedTextColor = Color.LightGray,
//            focusedTextColor = Color.White,
//            focusedLabelColor = Color.White,
//            unfocusedLabelColor = Color.White,
//            cursorColor = Color.Red
//        )
//    )
//    Spacer(Modifier.height(12.dp))

//    Button(
//        onClick = { vm.onEvent(SignUpEvent.Submit) },
//        enabled = !state.isLoading,
//        modifier = Modifier.fillMaxWidth(),
//        colors = ButtonDefaults.buttonColors(
//            containerColor = Color.Red
//        )
//    ) {
//        if (state.isLoading) CircularProgressIndicator() else Text("Sign up")
//    }
//
//    Spacer(Modifier.height(12.dp))
//    TextButton(onClick = onSignInClick) {
//        Text("Already have an account? Sign in", color = Color.White)
//    }
//
//    if (state.error != null) {
//        Spacer(Modifier.height(16.dp))
//        Snackbar { Text(state.error!!) }
//    }
}