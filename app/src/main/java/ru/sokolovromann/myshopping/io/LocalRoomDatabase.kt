package ru.sokolovromann.myshopping.io

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import ru.sokolovromann.myshopping.old.OldAutocompletesDao
import ru.sokolovromann.myshopping.old.OldProductsDao
import ru.sokolovromann.myshopping.old.OldShoppingListsDao
import ru.sokolovromann.myshopping.old.OldAutocompleteEntity
import ru.sokolovromann.myshopping.old.OldProductEntity
import ru.sokolovromann.myshopping.old.OldShoppingEntity

@Database(
    entities = [
        OldProductEntity::class,
        OldShoppingEntity::class,
        OldAutocompleteEntity::class,
    ],
    version = LocalRoomDatabase.API_27_DATABASE_VERSION,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = LocalRoomDatabase.API_15_DATABASE_VERSION,
            to = LocalRoomDatabase.API_27_DATABASE_VERSION,
            spec = LocalRoomDatabase.MigrationFrom15To27Spec::class
        )
    ]
)
abstract class LocalRoomDatabase : RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "local_database"

        const val API_15_DATABASE_VERSION = 1
        const val API_27_DATABASE_VERSION = 2

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

    abstract fun getOldShoppingListsDao(): OldShoppingListsDao

    abstract fun getOldProductsDao(): OldProductsDao

    abstract fun getOldAutocompletesDao(): OldAutocompletesDao
}