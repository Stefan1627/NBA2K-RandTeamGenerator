package com.example.nba_team_rand_gen.ui.screens.showplayer

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nba_team_rand_gen.data.model.PlayerWithTeam
import kotlin.collections.forEach

@Composable
fun ShowPlayerScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    vm: ShowPlayerViewModel = viewModel()
) {
    val s by vm.state.collectAsStateWithLifecycle()

    // listeaza echipele
    TeamList(s.teams.take(s.teams.size/2))
    Spacer(Modifier.height(8.dp))
    TeamList(s.teams.drop(s.teams.size/2))

    var showDialog by remember { mutableStateOf(false) }

    Row {
        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Back") }
        Spacer(Modifier.width(8.dp))
        Button(onClick = { showDialog = true }, enabled = s.teams.isNotEmpty() && !s.saving, modifier = Modifier.weight(1f)) {
            if (s.saving) CircularProgressIndicator(strokeWidth = 2.dp) else Text("Accept")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (!s.saving) showDialog = false },
            title = { Text("Name your match") },
            text = {
                OutlinedTextField(s.matchName, onValueChange = vm::updateName, label = { Text("Enter match name") })
            },
            confirmButton = { TextButton(enabled = s.matchName.isNotBlank() && !s.saving, onClick = vm::save) { Text("Save") } },
            dismissButton = { TextButton(enabled = !s.saving, onClick = { showDialog = false }) { Text("Return") } }
        )
    }

    LaunchedEffect(s.saved) { if (s.saved) onSaved() }
    s.error?.let { LaunchedEffect(it) { Log.e("Save Failed", "Save Failed") } }
}

@Composable
private fun TeamList(team: List<PlayerWithTeam>) {
    androidx.compose.foundation.layout.Column {
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
