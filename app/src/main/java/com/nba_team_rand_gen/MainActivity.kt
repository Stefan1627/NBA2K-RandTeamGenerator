package com.nba_team_rand_gen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nba_team_rand_gen.ui.nav.NavigationItem
import com.nba_team_rand_gen.ui.auth.AuthViewModel
import com.nba_team_rand_gen.ui.auth.LoginScreen
import com.nba_team_rand_gen.ui.auth.SignUpScreen
import com.nba_team_rand_gen.ui.nav.Navigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val authVm: AuthViewModel = hiltViewModel()
            val loggedIn by authVm.isLoggedIn.collectAsStateWithLifecycle()

            var showSignUp by rememberSaveable { mutableStateOf(false) }

            MaterialTheme {
                if (!loggedIn) {
                    if (showSignUp) {
                        SignUpScreen(
                            onSignInClick = { showSignUp = false }
                        )
                    } else {
                        LoginScreen(
                            onLoggedIn = {},
                            onCreateAccount = { showSignUp = true }
                        )
                    }
                } else {

                    MainScreen(
                        onConfirmSignOut = {
                            authVm.signOut()
                        }
                    )
                }
            }
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
            containerColor = colorResource(R.color.splash_color),
            contentWindowInsets = WindowInsets.systemBars
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                Navigation(navController = navController)
            }
        }

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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar(onSignOutClick: () -> Unit) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 18.sp,
                    color = Color.White
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(id = R.color.splash_color),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White,
                actionIconContentColor = Color.White
            ),
            actions = {
                IconButton(onClick = onSignOutClick) {
                    runCatching {
                        Icon(
                            painter = painterResource(id = R.drawable.sign_out_svg),
                            contentDescription = "Sign out"
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
                TextButton(onClick = onConfirm) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        )
    }

    @Composable
    fun BottomNavigationBar(navController: NavController) {
        val items = listOf(
            NavigationItem.Home,
            NavigationItem.Favorites,
            NavigationItem.Explore,
            NavigationItem.Post,
            NavigationItem.Profile
        )

        NavigationBar(
            containerColor = colorResource(id = R.color.splash_color)
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            items.forEach { item ->
                val isSelected = currentDestination
                    ?.hierarchy
                    ?.any { it.route == item.route } == true

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            // Switch to another tab -> show its root
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = false
                                }
                                launchSingleTop = true
                                restoreState = false   // open root of target tab
                            }
                        } else {
                            // Reselect current tab -> pop to its root
                            navController.popBackStack(item.route, inclusive = false)
                        }
                    },
                    icon = {
                        Icon(
                            painterResource(id = item.icon),
                            contentDescription = item.title
                        )
                    },
                    label = { Text(text = item.title) },
                )
            }
        }
    }

    // ------------------------------- Previews --------------------------------
    @Preview(showBackground = true)
    @Composable
    fun MainScreenPreview() {
        MaterialTheme {
            MainScreen(onConfirmSignOut = {})
        }
    }
}
