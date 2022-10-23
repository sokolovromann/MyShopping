package ru.sokolovromann.myshopping.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.sokolovromann.myshopping.AppDispatchers
import ru.sokolovromann.myshopping.data.local.datasource.LocalDataStore
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatabase

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = LocalDataStore.DATASTORE_NAME
    )

    fun providesLocalDataStore(
        @ApplicationContext context: Context,
        dispatchers: AppDispatchers
    ): LocalDataStore {
        return LocalDataStore(context.dataStore, dispatchers)
    }

    fun providesLocalDatabase(@ApplicationContext context: Context): LocalDatabase {
        return LocalDatabase.build(context)
    }

    fun providesAppDispatchers(): AppDispatchers {
        return AppDispatchers()
    }
}