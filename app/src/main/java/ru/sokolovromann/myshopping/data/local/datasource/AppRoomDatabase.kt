package ru.sokolovromann.myshopping.data.local.datasource

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import ru.sokolovromann.myshopping.data.local.dao.AutocompletesDao
import ru.sokolovromann.myshopping.data.local.dao.ProductsDao
import ru.sokolovromann.myshopping.data.local.dao.ShoppingListsDao
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingEntity

@Database(
    entities = [AutocompleteEntity::class, ProductEntity::class, ShoppingEntity::class],
    version = AppRoomDatabase.CURRENT_DATABASE_VERSION,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = AppRoomDatabase.CODE_15_DATABASE_VERSION,
            to = AppRoomDatabase.CODE_27_DATABASE_VERSION,
            spec = MigrationFrom15To27::class
        )
    ]
)
abstract class AppRoomDatabase : RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "local_database"
        const val CODE_15_DATABASE_VERSION = 1
        const val CODE_27_DATABASE_VERSION = 2
        const val CURRENT_DATABASE_VERSION = CODE_27_DATABASE_VERSION

        fun build(context: Context): AppRoomDatabase {
            return Room.databaseBuilder(context, AppRoomDatabase::class.java, DATABASE_NAME)
                .build()
        }
    }

    abstract fun getShoppingListsDao(): ShoppingListsDao

    abstract fun getProductsDao(): ProductsDao

    abstract fun getAutocompletesDao(): AutocompletesDao
}

@DeleteColumn(
    tableName = "shoppings",
    columnName = "created"
)
@DeleteColumn(
    tableName = "products",
    columnName = "created"
)
@DeleteColumn(
    tableName = "autocompletes",
    columnName = "created"
)
class MigrationFrom15To27 : AutoMigrationSpec