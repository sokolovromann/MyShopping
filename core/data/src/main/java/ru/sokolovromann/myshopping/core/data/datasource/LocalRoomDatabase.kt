package ru.sokolovromann.myshopping.core.data.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.sokolovromann.myshopping.core.data.datasource.LocalRoomDatabase.Companion.API42_DATABASE_VERSION
import ru.sokolovromann.myshopping.core.data.model.CartEntity
import ru.sokolovromann.myshopping.core.data.model.ProductEntity
import ru.sokolovromann.myshopping.core.data.model.FabricEntity
import ru.sokolovromann.myshopping.core.data.model.SuggestionEntity

@Database(
    entities = [
        CartEntity::class,
        ProductEntity::class,
        SuggestionEntity::class,
        FabricEntity::class
    ],
    version = API42_DATABASE_VERSION,
    exportSchema = true
)
abstract class LocalRoomDatabase : RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "api42_database"
        const val API42_DATABASE_VERSION = 1

        fun build(context: Context): LocalRoomDatabase =
            Room.databaseBuilder(
                context,
                LocalRoomDatabase::class.java,
                DATABASE_NAME
            ).build()
    }

    abstract fun getCartsDao(): CartsDao

    abstract fun getProductsDao(): ProductsDao

    abstract fun getSuggestionsDao(): SuggestionsDao

    abstract fun getFabricsDao(): FabricsDao
}