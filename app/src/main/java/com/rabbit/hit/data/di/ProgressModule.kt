package com.rabbit.hit.data.di

import com.rabbit.hit.data.progress.PlayerProgressRepository
import com.rabbit.hit.data.progress.PlayerProgressRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProgressModule {

    @Binds
    @Singleton
    abstract fun bindPlayerProgressRepository(
        impl: PlayerProgressRepositoryImpl
    ): PlayerProgressRepository
}
