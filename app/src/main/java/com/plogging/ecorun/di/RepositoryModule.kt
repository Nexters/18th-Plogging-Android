package com.plogging.ecorun.di

import com.plogging.ecorun.data.repository.auth.AuthRepository
import com.plogging.ecorun.data.repository.auth.AuthRepositoryImpl
import com.plogging.ecorun.data.repository.plogging.PloggingRepository
import com.plogging.ecorun.data.repository.plogging.PloggingRepositoryImpl
import com.plogging.ecorun.data.repository.ranking.RankingRepository
import com.plogging.ecorun.data.repository.ranking.RankingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPloggingRepository(impl: PloggingRepositoryImpl): PloggingRepository

    @Binds
    @Singleton
    abstract fun bindRankingRepository(impl: RankingRepositoryImpl): RankingRepository
}