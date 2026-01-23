package ru.sokolovromann.myshopping.data.local.datasource

import ru.sokolovromann.myshopping.data.local.dao.AppConfigDao
import ru.sokolovromann.myshopping.data39.old.Api15AutocompletesDao
import ru.sokolovromann.myshopping.data.local.dao.BackupDao
import ru.sokolovromann.myshopping.data.local.dao.CodeVersion14Dao
import ru.sokolovromann.myshopping.data39.old.Api15ProductsDao
import ru.sokolovromann.myshopping.data.local.dao.ResourcesDao
import ru.sokolovromann.myshopping.data39.LocalRoomDatabase
import ru.sokolovromann.myshopping.data39.old.Api15ShoppingListsDao
import javax.inject.Inject

class LocalDatasource @Inject constructor(
    private val localRoomDatabase: LocalRoomDatabase,
    private val appSQLiteOpenHelper: AppSQLiteOpenHelper,
    private val appContent: AppContent,
    private val backupDao: BackupDao
) {

    fun getShoppingListsDao(): Api15ShoppingListsDao {
        return localRoomDatabase.getApi15ShoppingListsDao()
    }

    fun getProductsDao(): Api15ProductsDao {
        return localRoomDatabase.getApi15ProductsDao()
    }

    fun getAutocompletesDao(): Api15AutocompletesDao {
        return localRoomDatabase.getApi15AutocompletesDao()
    }

    fun getCodeVersion14Dao(): CodeVersion14Dao {
        return CodeVersion14Dao(appSQLiteOpenHelper)
    }

    fun getAppConfigDao(): AppConfigDao {
        return AppConfigDao(appContent)
    }

    fun getResourcesDao(): ResourcesDao {
        return ResourcesDao(appContent)
    }

    fun getBackupDao(): BackupDao {
        return backupDao
    }
}