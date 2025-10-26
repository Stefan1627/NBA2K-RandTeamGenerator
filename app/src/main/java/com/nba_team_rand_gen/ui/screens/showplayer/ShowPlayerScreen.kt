package com.nba_team_rand_gen.ui.screens.showplayer

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nba_team_rand_gen.R
import com.nba_team_rand_gen.data.model.PlayerWithTeam
import kotlin.collections.forEach

@Composable
fun ShowPlayerScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    vm: ShowPlayerViewModel
) {
    val s by vm.state.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // listeaza echipele
        Text(text = stringResource(R.string.first_team), fontSize = 18.sp, color = Color.Red)
        TeamList(s.teams.take(s.teams.size / 2))

        Text(text = stringResource(R.string.second_team), fontSize = 18.sp, color = Color.Red)
        TeamList(s.teams.drop(s.teams.size / 2))

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) { Text("Back", color = Color.White) }

            Button(
                onClick = { showDialog = true },
                enabled = s.teams.isNotEmpty() && !s.saving,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                if (s.saving) CircularProgressIndicator(strokeWidth = 2.dp)
                else Text("Accept")
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (!s.saving) showDialog = false },
            title = { Text("Name your match", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = s.matchName,
                    onValueChange = { new -> vm.updateName(new.take(21)) },
                    label = { Text("Enter match name") },
                    singleLine = true,
                    supportingText = { Text("${s.matchName.length}/21") },
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
            },
            confirmButton = {
                TextButton(
                    enabled = s.matchName.isNotBlank() && !s.saving,
                    onClick = vm::save,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.splash_color),
                        contentColor = Color.White
                    )
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(enabled = !s.saving, onClick = { showDialog = false }) {
                    Text("Return", color = Color.White)
                }
            },
            containerColor = colorResource(id = R.color.splash_color)
        )
    }

    LaunchedEffect(s.saved) { if (s.saved) onSaved() }
    s.error?.let { LaunchedEffect(it) { Log.e("Save Failed", it) } }
}

@Composable
private fun TeamList(team: List<PlayerWithTeam>) {
    Column {
        team.forEach { item ->
            Text(
                text = "${item.player.playerName} (${item.player.ovr}) - ${item.teamName}",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
