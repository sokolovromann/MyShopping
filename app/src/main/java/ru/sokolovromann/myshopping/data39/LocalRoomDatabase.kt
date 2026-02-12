package ru.sokolovromann.myshopping.data39

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import ru.sokolovromann.myshopping.data39.carts.CartRoomEntity
import ru.sokolovromann.myshopping.data39.carts.CartsRoomDao
import ru.sokolovromann.myshopping.data39.old.Api15AutocompletesDao
import ru.sokolovromann.myshopping.data39.old.Api15ProductsDao
import ru.sokolovromann.myshopping.data39.old.Api15ShoppingListsDao
import ru.sokolovromann.myshopping.data39.old.Api15AutocompleteEntity
import ru.sokolovromann.myshopping.data39.old.Api15ProductEntity
import ru.sokolovromann.myshopping.data39.old.Api15ShoppingEntity
import ru.sokolovromann.myshopping.data39.products.ProductRoomEntity
import ru.sokolovromann.myshopping.data39.products.ProductsRoomDao
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetailsRoomDao
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetailRoomEntity
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionRoomEntity
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsRoomDao

@Database(
    entities = [
        CartRoomEntity::class,
        ProductRoomEntity::class,
        SuggestionRoomEntity::class,
        SuggestionDetailRoomEntity::class,
        Api15ProductEntity::class,
        Api15ShoppingEntity::class,
        Api15AutocompleteEntity::class
    ],
    version = LocalRoomDatabase.API40_DATABASE_VERSION,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = LocalRoomDatabase.API15_DATABASE_VERSION,
            to = LocalRoomDatabase.API27_DATABASE_VERSION,
            spec = LocalRoomDatabase.MigrationFrom15To27Spec::class
        ),
        AutoMigration(
            from = LocalRoomDatabase.API27_DATABASE_VERSION,
            to = LocalRoomDatabase.API39_DATABASE_VERSION,
            spec = LocalRoomDatabase.MigrationFrom27To39Spec::class
        ),
        AutoMigration(
            from = LocalRoomDatabase.API39_DATABASE_VERSION,
            to = LocalRoomDatabase.API40_DATABASE_VERSION,
            spec = LocalRoomDatabase.MigrationFrom39To40Spec::class
        )
    ]
)
abstract class LocalRoomDatabase : RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "local_database"

        const val API15_DATABASE_VERSION = 1
        const val API27_DATABASE_VERSION = 2
        const val API39_DATABASE_VERSION = 3
        const val API40_DATABASE_VERSION = 4

        fun build(context: Context): LocalRoomDatabase {
            return Room.databaseBuilder(context, LocalRoomDatabase::class.java, DATABASE_NAME)
                .build()
        }
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
    class MigrationFrom15To27Spec : AutoMigrationSpec

    class MigrationFrom27To39Spec : AutoMigrationSpec

    class MigrationFrom39To40Spec : AutoMigrationSpec

    abstract fun getCartsDao(): CartsRoomDao

    abstract fun getProductsDao(): ProductsRoomDao

    abstract fun getSuggestionsDao(): SuggestionsRoomDao

    abstract fun getSuggestionDetailsDao(): SuggestionDetailsRoomDao

    abstract fun getApi15ShoppingListsDao(): Api15ShoppingListsDao

    abstract fun getApi15ProductsDao(): Api15ProductsDao

    abstract fun getApi15AutocompletesDao(): Api15AutocompletesDao
}