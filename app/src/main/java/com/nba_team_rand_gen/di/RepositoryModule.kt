package com.nba_team_rand_gen.di

import com.nba_team_rand_gen.data.repo.AuthRepositoryImpl
import com.nba_team_rand_gen.data.repo.MatchesRepositoryImpl
import com.nba_team_rand_gen.domain.repo.AuthRepository
import com.nba_team_rand_gen.domain.repo.MatchesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindMatchesRepository(impl: MatchesRepositoryImpl): MatchesRepository
}