package ru.sokolovromann.myshopping.core.data.old.datasource

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import ru.sokolovromann.myshopping.core.data.old.model.Api15AutocompleteEntity
import ru.sokolovromann.myshopping.core.data.old.model.Api15ProductEntity
import ru.sokolovromann.myshopping.core.data.old.model.Api15ShoppingEntity
import ru.sokolovromann.myshopping.core.data.old.model.Api39CartEntity
import ru.sokolovromann.myshopping.core.data.old.model.Api39ProductEntity
import ru.sokolovromann.myshopping.core.data.old.model.Api39SuggestionDetailEntity
import ru.sokolovromann.myshopping.core.data.old.model.Api39SuggestionEntity

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
    version = Api15LocalRoomDatabase.API40_DATABASE_VERSION,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = Api15LocalRoomDatabase.API15_DATABASE_VERSION,
            to = Api15LocalRoomDatabase.API27_DATABASE_VERSION,
            spec = Api15LocalRoomDatabase.MigrationFrom15To27Spec::class
        ),
        AutoMigration(
            from = Api15LocalRoomDatabase.API27_DATABASE_VERSION,
            to = Api15LocalRoomDatabase.API39_DATABASE_VERSION,
            spec = Api15LocalRoomDatabase.MigrationFrom27To39Spec::class
        ),
        AutoMigration(
            from = Api15LocalRoomDatabase.API39_DATABASE_VERSION,
            to = Api15LocalRoomDatabase.API40_DATABASE_VERSION,
            spec = Api15LocalRoomDatabase.MigrationFrom39To40Spec::class
        )
    ]
)
abstract class Api15LocalRoomDatabase : RoomDatabase() {

    companion object {

        const val DATABASE_NAME = "local_database"
        const val API15_DATABASE_VERSION = 1
        const val API27_DATABASE_VERSION = 2
        const val API39_DATABASE_VERSION = 3
        const val API40_DATABASE_VERSION = 4

        fun build(context: Context): Api15LocalRoomDatabase =
            Room.databaseBuilder(
                context,
                Api15LocalRoomDatabase::class.java,
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