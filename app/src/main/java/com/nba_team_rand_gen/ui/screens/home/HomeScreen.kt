package com.nba_team_rand_gen.ui.screens.home

import android.app.Application
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.ui.res.colorResource
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nba_team_rand_gen.R

@Composable
fun HomeScreen(
    vm: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(LocalContext.current.applicationContext as Application)
    ),
    onNavigateShowRoute: (String) -> Unit
) {
    val s by vm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Dropdown(
            label = "Type",
            options = listOf("All","Current","Classic","All-time"),
            selected = s.type,
            onSelect = { vm.onEvent(HomeEvent.OnType(it)) }
        )

        Dropdown(
            label = "Game",
            options = listOf("1vs1","2vs2","3vs3","4vs4","5vs5"),
            selected = s.game,
            onSelect = { vm.onEvent(HomeEvent.OnGame(it)) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier.width(200.dp),
                onClick = { vm.onEvent(HomeEvent.Randomize) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )


            ) { Text("Randomize") }
        }
    }

    // Navigare identica logicii Legacy: encode JSON -> adauga ca query param
    s.navigateToShow?.let { json ->
        val encoded = Uri.encode(json)
        onNavigateShowRoute("showPlayer?teamsJson=$encoded")
        vm.onEvent(HomeEvent.NavConsumed)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun Dropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit )
{
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clickable { expanded = true },
            readOnly = true, value = selected,
            onValueChange = {},
            label = { Text(
                text =label,
                color = Color.LightGray
                ) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                // outline & label when focused
                focusedBorderColor = Color.Red,
                focusedLabelColor = Color.Red,
                unfocusedLabelColor = Color.Red,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.Red,
                // optional: keep the rest as you like
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = colorResource(id = R.color.menu_color)
            )
        {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = opt,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                                color = colorResource(id = R.color.white)
                            )
                            if (opt == selected) {
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    onClick = {
                        onSelect(opt)
                        expanded = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
