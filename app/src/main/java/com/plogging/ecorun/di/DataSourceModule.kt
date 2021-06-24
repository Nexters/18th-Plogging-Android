package com.plogging.ecorun.di

import com.plogging.ecorun.data.remote.auth.AuthDataSource
import com.plogging.ecorun.data.remote.auth.AuthDataSourceImpl
import com.plogging.ecorun.data.remote.plogging.PloggingDataSource
import com.plogging.ecorun.data.remote.plogging.PloggingDataSourceImpl
import com.plogging.ecorun.data.remote.ranking.RankingDataSource
import com.plogging.ecorun.data.remote.ranking.RankingDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindAuthDatasource(impl: AuthDataSourceImpl): AuthDataSource

    @Binds
    @Singleton
    abstract fun bindPloggingDatasource(impl: PloggingDataSourceImpl): PloggingDataSource

    @Binds
    @Singleton
    abstract fun bindRankingDatasource(impl: RankingDataSourceImpl): RankingDataSource
}