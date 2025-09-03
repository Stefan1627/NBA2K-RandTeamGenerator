package com.example.nba_team_rand_gen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nba_team_rand_gen.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            MainScreen(
                onConfirmSignOut = {
                    // Exactly what BaseActivity used to do
                    FirebaseAuth.getInstance().signOut()
                    startActivity(
                        Intent(this, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                    )
                    finish()
                }
            )
        }
    }

    // ------------------------------- COMPOSE UI -------------------------------

    @Composable
    fun MainScreen(onConfirmSignOut: () -> Unit) {
        val navController = rememberNavController()
        var showSignOutDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopBar(
                    onSignOutClick = { showSignOutDialog = true }
                )
            },
            bottomBar = { BottomNavigationBar(navController) },
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    Navigation(navController = navController)
                }
            },
            backgroundColor = colorResource(R.color.splash_color)
        )

        if (showSignOutDialog) {
            SignOutDialog(
                onConfirm = {
                    showSignOutDialog = false
                    onConfirmSignOut()
                },
                onDismiss = { showSignOutDialog = false }
            )
        }
    }

    @Composable
    fun Navigation(navController: NavHostController) {
        NavHost(navController, startDestination = NavigationItem.Home.route) {
            // HOME TAB hosts your original XML screen + behavior
            composable(NavigationItem.Home.route) { LegacyHomeScreen() }
            composable(NavigationItem.Music.route) { SimpleTabBody("Music") }
            composable(NavigationItem.Movies.route) { SimpleTabBody("Movies") }
            composable(NavigationItem.Books.route) { SimpleTabBody("Books") }
            composable(NavigationItem.Profile.route) { SimpleTabBody("Profile") }
        }
    }

    @Composable
    fun TopBar(onSignOutClick: () -> Unit) {
        TopAppBar(
            title = { Text(text = stringResource(R.string.app_name), fontSize = 18.sp) },
            backgroundColor = colorResource(id = R.color.splash_color),
            contentColor = Color.White,
            actions = {
                // Use an icon if you have one (recommended):
                //   IconButton(onClick = onSignOutClick) {
                //       Icon(painterResource(R.drawable.ic_logout), contentDescription = "Sign out")
                //   }
                // Or a simple text action (works without extra drawables):
                IconButton(onClick = onSignOutClick) {
                    // fallback to an icon you already have in the project; if not, show text
                    runCatching {
                        Icon(
                            painter = painterResource(id = R.drawable.sign_out_svg),
                            contentDescription = "Sign out",
                            tint = Color.White
                        )
                    }.getOrElse {
                        Text("Sign out", color = Color.White)
                    }
                }
            }
        )
    }

    @Composable
    fun SignOutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Sign Out?") },
            text = { Text("Do you really want to sign out?") },
            confirmButton = {
                androidx.compose.material.TextButton(onClick = onConfirm) {
                    Text("Yes")
                }
            },
            dismissButton = {
                androidx.compose.material.TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }

    @Composable
    fun BottomNavigationBar(navController: NavController) {
        val items = listOf(
            NavigationItem.Home,
            NavigationItem.Music,
            NavigationItem.Movies,
            NavigationItem.Books,
            NavigationItem.Profile
        )
        BottomNavigation(
            backgroundColor = colorResource(id = R.color.splash_color),
            contentColor = Color.White
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { item ->
                BottomNavigationItem(
                    icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                    label = { Text(text = item.title) },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.White.copy(alpha = 0.4f),
                    alwaysShowLabel = true,
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) { saveState = true }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }

    /**
     * Embeds your original activity_main.xml and re-applies the same behavior:
     * spinners, adapters, buttons, randomize -> ShowPlayer, history, favorites.
     */
    @Composable
    fun LegacyHomeScreen() {
        val context = LocalContext.current
        AndroidViewBinding(ActivityMainBinding::inflate) {
            // ----- TYPE SPINNER -----
            val typeOptions = arrayOf("All", "Current", "Classic", "All-time")
            val typeAdapter = ArrayAdapter(root.context, R.layout.spinner_list, typeOptions).apply {
                setDropDownViewResource(R.layout.spinner_list)
            }
            chooseType.adapter = typeAdapter
            var finalType = typeOptions.getOrElse(chooseType.selectedItemPosition) { "All" }
            chooseType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) { finalType = typeOptions[position] }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(root.context, "nothing selected", Toast.LENGTH_LONG).show()
                }
            }

            // ----- GAME TYPE SPINNER -----
            val gameOptions = arrayOf("1vs1", "2vs2", "3vs3", "4vs4", "5vs5")
            val gameAdapter = ArrayAdapter(root.context, R.layout.spinner_list, gameOptions).apply {
                setDropDownViewResource(R.layout.spinner_list)
            }
            chooseGameType.adapter = gameAdapter
            var finalGame = gameOptions.getOrElse(chooseGameType.selectedItemPosition) { "1vs1" }
            chooseGameType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) { finalGame = gameOptions[position] }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(root.context, "nothing selected", Toast.LENGTH_LONG).show()
                }
            }

            // ----- BUTTONS -----
            randomButton.setOnClickListener {
                val randomizeGame = RandomizeGame(root.context)
                val teams: List<PlayerWithTeam> = randomizeGame.randomize(finalType, finalGame)

                val teamsJson = Json.encodeToString(teams)
                val intent = Intent(root.context, ShowPlayer::class.java).apply {
                    putExtra("teamsJson", teamsJson)
                }
                root.context.startActivity(intent)
                (context as? Activity)?.finish()
            }

            historyButton.setOnClickListener {
                root.context.startActivity(Intent(root.context, MatchHistory::class.java))
                (context as? Activity)?.finish()
            }

            favButton.setOnClickListener {
                root.context.startActivity(Intent(root.context, FavoriteService::class.java))
                (context as? Activity)?.finish()
            }
        }
    }

    // Simple placeholders; replace with real content any time
    @Composable private fun SimpleTabBody(label: String) {
        Text(text = label, color = Color.White, modifier = Modifier.padding(16.dp))
    }

    // ------------------------------- Previews --------------------------------
    @Preview(showBackground = true)
    @Composable
    fun MainScreenPreview() {
        MainScreen(onConfirmSignOut = {})
    }
}
