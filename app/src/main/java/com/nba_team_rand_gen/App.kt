package com.nba_team_rand_gen

import android.app.Application
import com.nba_team_rand_gen.data.repo.RosterRepository

class App : Application() {
    val container : AppContainer by lazy { AppContainer(this) }
}

class AppContainer(private val app: Application) {
    val rosterRepository: RosterRepository by lazy { RosterRepository(app) }
}