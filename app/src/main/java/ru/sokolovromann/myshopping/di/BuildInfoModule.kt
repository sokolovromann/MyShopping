package ru.sokolovromann.myshopping.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.BuildInfoImpl
import ru.sokolovromann.myshopping.core.domain.repository.BuildInfo

@Module
@InstallIn(SingletonComponent::class)
abstract class BuildInfoModule {

    @Binds
    @Singleton
    abstract fun bindBuildInfo(buildInfoImpl: BuildInfoImpl): BuildInfo
}