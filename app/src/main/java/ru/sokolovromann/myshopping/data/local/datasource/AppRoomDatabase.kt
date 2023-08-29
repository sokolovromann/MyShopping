package ru.sokolovromann.myshopping.data.local.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.sokolovromann.myshopping.data.local.dao.AutocompletesDao
import ru.sokolovromann.myshopping.data.local.dao.ProductsDao
import ru.sokolovromann.myshopping.data.local.dao.ShoppingListsDao
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingEntity

@Database(
    entities = [AutocompleteEntity::class, ProductEntity::class, ShoppingEntity::class],
    version = AppRoomDatabase.DATABASE_VERSION
)
abstract class AppRoomDatabase : RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "local_database"
        private const val CODE_VERSION_15_DATABASE_VERSION = 1

        const val DATABASE_VERSION = CODE_VERSION_15_DATABASE_VERSION

        fun build(context: Context): AppRoomDatabase {
            return Room.databaseBuilder(context, AppRoomDatabase::class.java, DATABASE_NAME)
                .build()
        }
    }

    abstract fun getShoppingListsDao(): ShoppingListsDao

    abstract fun getProductsDao(): ProductsDao

    abstract fun getAutocompletesDao(): AutocompletesDao
}