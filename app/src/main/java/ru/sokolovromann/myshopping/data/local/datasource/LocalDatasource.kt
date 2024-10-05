package ru.sokolovromann.myshopping.data.local.datasource

import ru.sokolovromann.myshopping.data.local.dao.AppConfigDao
import ru.sokolovromann.myshopping.data.local.dao.AutocompletesDao
import ru.sokolovromann.myshopping.data.local.dao.BackupDao
import ru.sokolovromann.myshopping.data.local.dao.CodeVersion14Dao
import ru.sokolovromann.myshopping.data.local.dao.FilesDao
import ru.sokolovromann.myshopping.data.local.dao.ProductsDao
import ru.sokolovromann.myshopping.data.local.dao.ResourcesDao
import ru.sokolovromann.myshopping.data.local.dao.ShoppingListsDao
import javax.inject.Inject

class LocalDatasource @Inject constructor(
    private val appRoomDatabase: AppRoomDatabase,
    private val appSQLiteOpenHelper: AppSQLiteOpenHelper,
    private val appContent: AppContent
) {

    fun getShoppingListsDao(): ShoppingListsDao {
        return appRoomDatabase.getShoppingListsDao()
    }

    fun getProductsDao(): ProductsDao {
        return appRoomDatabase.getProductsDao()
    }

    fun getAutocompletesDao(): AutocompletesDao {
        return appRoomDatabase.getAutocompletesDao()
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

    fun getFilesDao(): FilesDao {
        return FilesDao(appContent)
    }

    fun getBackupDao(): BackupDao {
        return BackupDao(appContent)
    }
}