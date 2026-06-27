package ru.sokolovromann.myshopping.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.repository.CartsRepositoryImpl
import ru.sokolovromann.myshopping.core.data.repository.FabricsRepositoryImpl
import ru.sokolovromann.myshopping.core.data.repository.ProductsRepositoryImpl
import ru.sokolovromann.myshopping.core.data.repository.SuggestionsRepositoryImpl
import ru.sokolovromann.myshopping.core.data.repository.UserConfigRepositoryImpl
import ru.sokolovromann.myshopping.core.data.repository.UserPreferencesRepositoryImpl
import ru.sokolovromann.myshopping.core.domain.repository.CartsRepository
import ru.sokolovromann.myshopping.core.domain.repository.FabricsRepository
import ru.sokolovromann.myshopping.core.domain.repository.ProductsRepository
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsRepository
import ru.sokolovromann.myshopping.core.domain.repository.UserConfigRepository
import ru.sokolovromann.myshopping.core.domain.repository.UserPreferencesRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCartsRepository(repository: CartsRepositoryImpl): CartsRepository

    @Binds
    @Singleton
    abstract fun bindProductsRepository(repository: ProductsRepositoryImpl): ProductsRepository

    @Binds
    @Singleton
    abstract fun bindSuggestionsRepository(repository: SuggestionsRepositoryImpl): SuggestionsRepository

    @Binds
    @Singleton
    abstract fun bindFabricsRepository(repository: FabricsRepositoryImpl): FabricsRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(repository: UserPreferencesRepositoryImpl): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindUserConfigRepository(repository: UserConfigRepositoryImpl): UserConfigRepository
}