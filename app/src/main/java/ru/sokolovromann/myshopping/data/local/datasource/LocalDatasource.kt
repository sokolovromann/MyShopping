package ru.sokolovromann.myshopping.data.local.datasource

import ru.sokolovromann.myshopping.data.local.dao.AppConfigDao
import ru.sokolovromann.myshopping.old.OldAutocompletesDao
import ru.sokolovromann.myshopping.data.local.dao.BackupDao
import ru.sokolovromann.myshopping.data.local.dao.CodeVersion14Dao
import ru.sokolovromann.myshopping.old.OldProductsDao
import ru.sokolovromann.myshopping.data.local.dao.ResourcesDao
import ru.sokolovromann.myshopping.old.OldShoppingListsDao
import ru.sokolovromann.myshopping.io.LocalRoomDatabase
import javax.inject.Inject

class LocalDatasource @Inject constructor(
    private val localRoomDatabase: LocalRoomDatabase,
    private val appSQLiteOpenHelper: AppSQLiteOpenHelper,
    private val appContent: AppContent,
    private val backupDao: BackupDao
) {

    fun getShoppingListsDao(): OldShoppingListsDao {
        return localRoomDatabase.getOldShoppingListsDao()
    }

    fun getProductsDao(): OldProductsDao {
        return localRoomDatabase.getOldProductsDao()
    }

    fun getAutocompletesDao(): OldAutocompletesDao {
        return localRoomDatabase.getOldAutocompletesDao()
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