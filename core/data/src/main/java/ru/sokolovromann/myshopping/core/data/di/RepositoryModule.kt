package ru.sokolovromann.myshopping.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.repository.AddEditProductPreferencesRepositoryImpl
import ru.sokolovromann.myshopping.core.data.repository.BackupPreferencesRepositoryImpl
import ru.sokolovromann.myshopping.core.data.repository.BackupRepositoryImpl
import ru.sokolovromann.myshopping.core.data.repository.CartsPreferencesRepositoryImpl
import ru.sokolovromann.myshopping.core.data.repository.CartsRepositoryImpl
import ru.sokolovromann.myshopping.core.data.repository.FabricsRepositoryImpl
import ru.sokolovromann.myshopping.core.data.repository.GeneralPreferencesRepositoryImpl
import ru.sokolovromann.myshopping.core.data.repository.ProductsPreferencesRepositoryImpl
import ru.sokolovromann.myshopping.core.data.repository.ProductsRepositoryImpl
import ru.sokolovromann.myshopping.core.data.repository.SuggestionsPreferencesRepositoryImpl
import ru.sokolovromann.myshopping.core.data.repository.SuggestionsRepositoryImpl
import ru.sokolovromann.myshopping.core.data.repository.UserConfigRepositoryImpl
import ru.sokolovromann.myshopping.core.domain.repository.AddEditProductPreferencesRepository
import ru.sokolovromann.myshopping.core.domain.repository.BackupPreferencesRepository
import ru.sokolovromann.myshopping.core.domain.repository.BackupRepository
import ru.sokolovromann.myshopping.core.domain.repository.CartsPreferencesRepository
import ru.sokolovromann.myshopping.core.domain.repository.CartsRepository
import ru.sokolovromann.myshopping.core.domain.repository.FabricsRepository
import ru.sokolovromann.myshopping.core.domain.repository.GeneralPreferencesRepository
import ru.sokolovromann.myshopping.core.domain.repository.ProductsRepository
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsPreferencesRepository
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsRepository
import ru.sokolovromann.myshopping.core.domain.repository.UserConfigRepository

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
    abstract fun bindBackupRepository(repository: BackupRepositoryImpl): BackupRepository

    @Binds
    @Singleton
    abstract fun bindGeneralPreferencesRepository(repository: GeneralPreferencesRepositoryImpl): GeneralPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindCartsPreferencesRepository(repository: CartsPreferencesRepositoryImpl): CartsPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindProductsPreferencesRepository(repository: ProductsPreferencesRepositoryImpl): ProductsRepository

    @Binds
    @Singleton
    abstract fun bindAddEditProductPreferencesRepository(repository: AddEditProductPreferencesRepositoryImpl): AddEditProductPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindSuggestionsPreferencesRepository(repository: SuggestionsPreferencesRepositoryImpl): SuggestionsPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindBackupPreferencesRepository(repository: BackupPreferencesRepositoryImpl): BackupPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindUserConfigRepository(repository: UserConfigRepositoryImpl): UserConfigRepository
}