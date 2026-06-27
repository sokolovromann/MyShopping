package ru.sokolovromann.myshopping.core.data.datasource

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
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

    abstract fun getCartsDao(): CartsDao

    abstract fun getProductsDao(): ProductsDao

    abstract fun getSuggestionsDao(): SuggestionsDao

    abstract fun getFabricsDao(): FabricsDao
}