package ru.sokolovromann.myshopping.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.BuildInfoImpl
import ru.sokolovromann.myshopping.NavigatorImpl
import ru.sokolovromann.myshopping.core.domain.repository.BuildInfo
import ru.sokolovromann.myshopping.core.navigation.Navigator

@Module
@InstallIn(SingletonComponent::class)
abstract class MainModule {

    @Binds
    @Singleton
    abstract fun bindBuildInfo(buildInfo: BuildInfoImpl): BuildInfo

    @Binds
    @Singleton
    abstract fun bindNavigator(navigator: NavigatorImpl): Navigator
}