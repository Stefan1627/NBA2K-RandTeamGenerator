package com.nba_team_rand_gen.di

import android.app.Application
import android.content.Context
import com.nba_team_rand_gen.core.session.SessionManager
import com.nba_team_rand_gen.data.repo.AuthRepositoryImpl
import com.nba_team_rand_gen.data.repo.MatchesRepositoryImpl
import com.nba_team_rand_gen.data.repo.RosterRepository
import com.nba_team_rand_gen.domain.repo.AuthRepository
import com.nba_team_rand_gen.domain.repo.MatchesRepository
import com.nba_team_rand_gen.domain.usecase.RandomizeTeamsUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Providers {

    @Provides @Singleton
    fun provideSessionManager(@ApplicationContext ctx: Context): SessionManager =
        SessionManager.from(ctx)

    @Provides @Singleton
    fun provideRosterRepository(app: Application): RosterRepository =
        RosterRepository(app)

    @Provides @Singleton
    fun provideRandomizeTeamsUseCase(repo: RosterRepository): RandomizeTeamsUseCase =
        RandomizeTeamsUseCase(repo)
}
