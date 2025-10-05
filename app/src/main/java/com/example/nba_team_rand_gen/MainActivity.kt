package com.example.nba_team_rand_gen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nba_team_rand_gen.databinding.ActivityMainBinding
import com.example.nba_team_rand_gen.databinding.ActivityShowMatchesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        auth = FirebaseAuth.getInstance()

        setContent {
            MaterialTheme {
                MainScreen(
                    onConfirmSignOut = {
                        FirebaseAuth.getInstance().signOut()
                        startActivity(
                            Intent(this, LoginActivity::class.java).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        )
                        finish()
                    }
                )
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

    @Composable
    fun Navigation(navController: NavHostController) {
        NavHost(navController, startDestination = NavigationItem.Home.route) {
            composable(NavigationItem.Home.route) { LegacyHomeScreen(navController) }
            composable(NavigationItem.Favorites.route) { FavoritesScreen() }
            composable(NavigationItem.Explore.route) { MatchHistoryScreen() }
            composable(NavigationItem.Post.route)  { SimpleTabBody("Post") }
            composable(NavigationItem.Profile.route){ SimpleTabBody("Profile") }

            composable(
                route = "showPlayer?teamsJson={teamsJson}",
                arguments = listOf(
                    androidx.navigation.navArgument("teamsJson") {
                        type = androidx.navigation.NavType.StringType
                        nullable = false
                    }
                )
            ) { backStackEntry ->
                val json = backStackEntry.arguments?.getString("teamsJson") ?: "[]"
                ShowPlayerScreen(
                    json = json,
                    onBack = { navController.popBackStack() },
                    onSaved = {
                        // Go back to Home after a successful save
                        navController.popBackStack(route = NavigationItem.Home.route, inclusive = false)
                    }
                )
            }
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
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) { saveState = true }
                            }
                            launchSingleTop = true
                            restoreState = true
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

    /**
     * Embeds your original activity_main.xml and re-applies the same behavior.
     */
    @Composable
    private fun LegacyHomeScreen(navController: NavController) {
        AndroidViewBinding(
            factory = ActivityMainBinding::inflate,
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
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
                val encoded = android.net.Uri.encode(teamsJson)
                navController.navigate("showPlayer?teamsJson=$encoded")
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowPlayerScreen(
        json: String,
        onBack: () -> Unit,
        onSaved: () -> Unit
    ) {
        val context = LocalContext.current
        val teams: List<PlayerWithTeam> = remember(json) {
            runCatching { Json.decodeFromString<List<PlayerWithTeam>>(json) }.getOrElse { emptyList() }
        }

        val half = teams.size / 2
        val firstTeam = remember(teams) { teams.subList(0, half) }
        val secondTeam = remember(teams) { teams.subList(half, teams.size) }

        var showDialog by remember { mutableStateOf(false) }
        var matchName by remember { mutableStateOf("") }
        var saving by remember { mutableStateOf(false) }

        Scaffold(
            containerColor = colorResource(R.color.splash_color),
            contentWindowInsets = WindowInsets.systemBars
        ) { padding ->
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .padding(padding)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(16.dp)
            ) {
                Text(text = stringResource(R.string.first_team), fontSize = 18.sp, color = Color.Red)
                TeamList(team = firstTeam)

                androidx.compose.foundation.layout.Spacer(Modifier.padding(8.dp))

                Text(text = stringResource(R.string.second_team), fontSize = 18.sp, color = Color.Red)
                TeamList(team = secondTeam)

                androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))

                androidx.compose.foundation.layout.Row {
                    androidx.compose.material3.OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    ) { Text("Back") }

                    androidx.compose.foundation.layout.Spacer(Modifier.padding(8.dp))

                    androidx.compose.material3.Button(
                        onClick = { showDialog = true },
                        enabled = teams.isNotEmpty() && !saving,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (saving) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Accept")
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { if (!saving) showDialog = false },
                title = { Text("Name your match") },
                text = {
                    androidx.compose.material3.OutlinedTextField(
                        value = matchName,
                        onValueChange = { matchName = it },
                        label = { Text("Enter match name") },
                        singleLine = true,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                    )
                },
                confirmButton = {
                    TextButton(
                        enabled = matchName.isNotBlank() && !saving,
                        onClick = {
                            saving = true
                            ManageMatches.uploadMatch(json, matchName.trim())
                                .addOnSuccessListener { code ->
                                    saving = false
                                    if (code == 0) {
                                        Toast.makeText(
                                            context,
                                            "Match \"${matchName.trim()}\" saved!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showDialog = false
                                        onSaved()
                                    } else {
                                        Toast.makeText(context, "Save failed.", Toast.LENGTH_LONG).show()
                                    }
                                }
                                .addOnFailureListener {
                                    saving = false
                                    Toast.makeText(context, "Save failed.", Toast.LENGTH_LONG).show()
                                }
                        }
                    ) { Text("Save") }
                },
                dismissButton = {
                    TextButton(enabled = !saving, onClick = { showDialog = false }) { Text("Return") }
                }
            )
        }
    }

    @Composable
    private fun FavoritesScreen(
        modifier: Modifier = Modifier
    ) {
        // Keep one instance of my data containers and adapter across recompositions
        val data = remember { mutableListOf<String>() }
        val favorites = remember { mutableSetOf<Int>() }
        val matchEntries = remember { mutableListOf<Map<String, Any?>>() }

        // My existing adapter API
        val adapter = remember {
            StringListAdapter(
                items = data,
                favorites = favorites,
                onFavoriteClick = { pos -> ManageMatches.toggleFavorite(pos) },
                onTrashClick = { pos -> ManageMatches.deleteMatch(pos) },
                onDescriptionClick = { pos -> ManageMatches.descriptionShow(pos) }
            )
        }

        // Inflate the legacy XML and bind everything
        AndroidViewBinding(
            factory = ActivityShowMatchesBinding::inflate,
            modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            // Set up RecyclerView once
            if (recyclerView.adapter !== adapter) {
                recyclerView.layoutManager = LinearLayoutManager(root.context)
                recyclerView.adapter = adapter
            }

            // Avoid reloading on every recomposition — tag the view once loaded
            if (recyclerView.tag != "loaded") {
                recyclerView.tag = "loaded"
                loadFavorites(
                    data = data,
                    favorites = favorites,
                    matchEntries = matchEntries,
                    notifyInserted = { count ->
                        // Mirror my old diff notifications
                        if (count > 0) adapter.notifyItemRangeInserted(0, count)
                    },
                    notifyRemoved = { count ->
                        if (count > 0) adapter.notifyItemRangeRemoved(0, count)
                    }
                )
            }
        }
    }

    @Composable
    fun MatchHistoryScreen(
        modifier: Modifier = Modifier
    ) {
        // Remember lists and adapter between recompositions
        val data = remember { mutableListOf<String>() }
        val favorites = remember { mutableSetOf<Int>() }
        val matchEntries = remember { mutableListOf<Map<String, Any?>>() }

        val adapter = remember {
            StringListAdapter(
                items = data,
                favorites = favorites,
                onFavoriteClick = { pos -> ManageMatches.toggleFavorite(pos) },
                onTrashClick = { pos -> ManageMatches.deleteMatch(pos) },
                onDescriptionClick = { pos -> ManageMatches.descriptionShow(pos) }
            )
        }

        AndroidViewBinding(
            factory = ActivityShowMatchesBinding::inflate,
            modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            // Setup RecyclerView
            if (recyclerView.adapter !== adapter) {
                recyclerView.layoutManager = LinearLayoutManager(root.context)
                recyclerView.adapter = adapter
            }

            ManageMatches.bind(
                entries = matchEntries,
                favs = favorites,
                adapterRef = adapter,
                context = root.context
            )

            // Avoid multiple reloads
            if (recyclerView.tag != "loaded") {
                recyclerView.tag = "loaded"
                loadMatchesAndFavorites(
                    data = data,
                    favorites = favorites,
                    matchEntries = matchEntries,
                    notifyRemoved = { count ->
                        if (count > 0) adapter.notifyItemRangeRemoved(0, count)
                    },
                    notifyInserted = { count ->
                        if (count > 0) adapter.notifyItemRangeInserted(0, count)
                    }
                )
            }
        }
    }

    private fun loadMatchesAndFavorites(
        data: MutableList<String>,
        favorites: MutableSet<Int>,
        matchEntries: MutableList<Map<String, Any?>>,
        notifyRemoved: (count: Int) -> Unit,
        notifyInserted: (count: Int) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.w("MatchHistoryScreen", "Please log in first.")
            return
        }

        val userDoc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)

        userDoc.get()
            .addOnSuccessListener { snap ->
                @Suppress("UNCHECKED_CAST")
                val matches = snap.get("matchesList") as? List<Map<String, Any>> ?: emptyList()

                @Suppress("UNCHECKED_CAST")
                val rawFavs = snap.get("favoritesList") as? List<*> ?: emptyList<Any>()

                @Suppress("UNCHECKED_CAST")
                val favMaps = rawFavs.mapNotNull { elem ->
                    when (elem) {
                        is Map<*, *> -> (elem as? Map<String, Any>)
                        else -> null
                    }
                }

                val oldSize = data.size

                matchEntries.clear()
                matchEntries.addAll(matches)

                data.clear()
                data.addAll(matches.mapNotNull { it["name"] as? String })

                // Determine which items are favorites
                val newFavs = matches.indices.filter { idx ->
                    val e = matches[idx]
                    favMaps.any { it["name"] == e["name"] && it["data"] == e["data"] }
                }.toSet()

                favorites.clear()
                favorites.addAll(newFavs)

                if (oldSize > 0) notifyRemoved(oldSize)
                if (data.isNotEmpty()) notifyInserted(data.size)

                data.forEachIndexed { idx, name ->
                    Log.d("MatchHistoryScreen", "$name — favorite: ${favorites.contains(idx)}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("MatchHistoryScreen", "Failed to load histories", e)
            }
    }

    /**
     * The Firebase load logic lifted from your old FavoriteService,
     * adapted to fill the remembered lists and notify the adapter.
     */
    private fun loadFavorites(
        data: MutableList<String>,
        favorites: MutableSet<Int>,
        matchEntries: MutableList<Map<String, Any?>>,
        notifyInserted: (count: Int) -> Unit,
        notifyRemoved: (count: Int) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.w("FavoritesScreen", "Please log in first.")
            return
        }

        val userDoc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)

        userDoc.get()
            .addOnSuccessListener { snap ->
                @Suppress("UNCHECKED_CAST")
                val rawFavs = snap.get("favoritesList") as? List<*> ?: emptyList<Any>()

                @Suppress("UNCHECKED_CAST")
                val favMaps: List<Map<String, Any?>> = rawFavs.mapNotNull { elem ->
                    (elem as? Map<*, *>)          // keep only maps
                        ?.filterKeys { it is String }
                        ?.mapKeys { it.key as String }
                }

                val oldSize = data.size

                matchEntries.clear()
                matchEntries.addAll(favMaps)

                data.clear()
                data.addAll(favMaps.mapNotNull { it["name"] as? String })

                favorites.clear()
                favorites.addAll(data.indices) // everything shown here is a favorite

                if (oldSize > 0) notifyRemoved(oldSize)
                if (data.isNotEmpty()) notifyInserted(data.size)

                data.forEachIndexed { idx, name ->
                    Log.d("FavoritesScreen", "$idx: $name — favorite: true")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FavoritesScreen", "Failed to load favorites", e)
            }
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

    // Simple placeholders; replace with real content any time
    @Composable
    private fun SimpleTabBody(label: String) {
        Text(text = label, color = Color.White, modifier = Modifier.padding(16.dp))
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
