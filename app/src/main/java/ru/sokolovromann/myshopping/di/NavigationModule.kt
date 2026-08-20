package ru.sokolovromann.myshopping.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.navigation.NavigatorImpl
import ru.sokolovromann.myshopping.core.navigation.Navigator

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {

    @Binds
    @Singleton
    abstract fun bindNavigator(navigator: NavigatorImpl): Navigator
}