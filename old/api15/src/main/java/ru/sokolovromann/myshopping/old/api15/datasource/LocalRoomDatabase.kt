package ru.sokolovromann.myshopping.old.api15.datasource

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import ru.sokolovromann.myshopping.old.api15.model.Api15AutocompleteEntity
import ru.sokolovromann.myshopping.old.api15.model.Api15ProductEntity
import ru.sokolovromann.myshopping.old.api15.model.Api15ShoppingEntity
import ru.sokolovromann.myshopping.old.api15.model.Api39CartEntity
import ru.sokolovromann.myshopping.old.api15.model.Api39ProductEntity
import ru.sokolovromann.myshopping.old.api15.model.Api39SuggestionDetailEntity
import ru.sokolovromann.myshopping.old.api15.model.Api39SuggestionEntity

@Database(
    entities = [
        Api15ShoppingEntity::class,
        Api15ProductEntity::class,
        Api15AutocompleteEntity::class,
        Api39CartEntity::class,
        Api39ProductEntity::class,
        Api39SuggestionEntity::class,
        Api39SuggestionDetailEntity::class
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
internal abstract class LocalRoomDatabase : RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "local_database"

        const val API15_DATABASE_VERSION = 1
        const val API27_DATABASE_VERSION = 2
        const val API39_DATABASE_VERSION = 3
        const val API40_DATABASE_VERSION = 4

        fun build(context: Context): LocalRoomDatabase =
            Room.databaseBuilder(
                context,
                LocalRoomDatabase::class.java,
                DATABASE_NAME
            ).build()
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

    abstract fun getApi15Dao(): Api15Dao
}