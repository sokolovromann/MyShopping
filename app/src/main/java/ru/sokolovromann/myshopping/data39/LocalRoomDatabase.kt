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
import ru.sokolovromann.myshopping.data39.old.OldAutocompletesDao
import ru.sokolovromann.myshopping.data39.old.OldProductsDao
import ru.sokolovromann.myshopping.data39.old.OldShoppingListsDao
import ru.sokolovromann.myshopping.data39.old.OldAutocompleteEntity
import ru.sokolovromann.myshopping.data39.old.OldProductEntity
import ru.sokolovromann.myshopping.data39.old.OldShoppingEntity
import ru.sokolovromann.myshopping.data39.products.ProductRoomEntity
import ru.sokolovromann.myshopping.data39.products.ProductsRoomDao
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetailRoomDao
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetailRoomEntity
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionRoomEntity
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsRoomDao

@Database(
    entities = [
        CartRoomEntity::class,
        ProductRoomEntity::class,
        SuggestionRoomEntity::class,
        SuggestionDetailRoomEntity::class,
        OldProductEntity::class,
        OldShoppingEntity::class,
        OldAutocompleteEntity::class
    ],
    version = LocalRoomDatabase.API39_DATABASE_VERSION,
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
        )
    ]
)
abstract class LocalRoomDatabase : RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "local_database"

        const val API15_DATABASE_VERSION = 1
        const val API27_DATABASE_VERSION = 2
        const val API39_DATABASE_VERSION = 3

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

    abstract fun getCartsDao(): CartsRoomDao

    abstract fun getProductsDao(): ProductsRoomDao

    abstract fun getSuggestionsDao(): SuggestionsRoomDao

    abstract fun getSuggestionDetailsDao(): SuggestionDetailRoomDao

    abstract fun getOldShoppingListsDao(): OldShoppingListsDao

    abstract fun getOldProductsDao(): OldProductsDao

    abstract fun getOldAutocompletesDao(): OldAutocompletesDao
}