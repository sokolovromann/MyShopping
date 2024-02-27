package ru.sokolovromann.myshopping.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.sokolovromann.myshopping.data.local.dao.*
import ru.sokolovromann.myshopping.data.local.datasource.AppContent
import ru.sokolovromann.myshopping.data.local.datasource.AppRoomDatabase
import ru.sokolovromann.myshopping.data.local.datasource.AppSQLiteOpenHelper
import ru.sokolovromann.myshopping.data.local.datasource.LocalDatasource
import ru.sokolovromann.myshopping.data.repository.*
import ru.sokolovromann.myshopping.media.BackupMediaStore
import ru.sokolovromann.myshopping.notification.purchases.PurchasesAlarmManager
import ru.sokolovromann.myshopping.notification.purchases.PurchasesNotificationManager
import ru.sokolovromann.myshopping.ui.shortcut.AppShortcutManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesAppContent(@ApplicationContext context: Context): AppContent {
        return AppContent(context)
    }

    @Singleton
    @Provides
    fun providesAppRoomDatabase(@ApplicationContext context: Context): AppRoomDatabase {
        return AppRoomDatabase.build(context)
    }

    @Singleton
    @Provides
    fun providesAppSQLiteOpenHelper(@ApplicationContext context: Context): AppSQLiteOpenHelper {
        return AppSQLiteOpenHelper(context)
    }

    @Singleton
    @Provides
    fun providesLocalDatasource(
        appRoomDatabase: AppRoomDatabase,
        appSQLiteOpenHelper: AppSQLiteOpenHelper,
        appContent: AppContent
    ): LocalDatasource {
        return LocalDatasource(appRoomDatabase, appSQLiteOpenHelper, appContent)
    }

    @Provides
    fun providesAppConfigRepository(localDatasource: LocalDatasource): AppConfigRepository {
        return AppConfigRepository(localDatasource)
    }

    @Provides
    fun providesAutocompletesRepository(localDatasource: LocalDatasource): AutocompletesRepository {
        return AutocompletesRepository(localDatasource)
    }

    @Provides
    fun providesBackupRepository(localDatasource: LocalDatasource): BackupRepository {
        return BackupRepository(localDatasource)
    }

    @Provides
    fun providesCodeVersion14Repository(localDatasource: LocalDatasource): CodeVersion14Repository {
        return CodeVersion14Repository(localDatasource)
    }

    @Provides
    fun providesShoppingListsRepository(localDatasource: LocalDatasource): ShoppingListsRepository {
        return ShoppingListsRepository(localDatasource)
    }

    @Provides
    fun providesPurchasesAlarmManager(
        @ApplicationContext context: Context
    ): PurchasesAlarmManager {
        return PurchasesAlarmManager(context)
    }

    @Provides
    fun providesPurchasesNotificationManager(
        @ApplicationContext context: Context
    ): PurchasesNotificationManager {
        return PurchasesNotificationManager(context)
    }

    @Provides
    fun providesBackupMediaStore(@ApplicationContext context: Context): BackupMediaStore {
        return BackupMediaStore(context)
    }

    @Provides
    fun providesAppShortcutManager(@ApplicationContext context: Context): AppShortcutManager {
        return AppShortcutManager(context)
    }
}