package ru.sokolovromann.myshopping.data.local.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.sokolovromann.myshopping.data.local.dao.*
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity
import ru.sokolovromann.myshopping.data.local.entity.ProductEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingEntity

@Database(entities = [AutocompleteEntity::class, ProductEntity::class, ShoppingEntity::class], version = 1)
abstract class LocalDatabase : RoomDatabase() {

    companion object {
        fun build(context: Context): LocalDatabase {
            return Room.databaseBuilder(context, LocalDatabase::class.java, "local_database")
                .build()
        }
    }

    abstract fun purchasesDao(): PurchasesDao

    abstract fun purchasesNotificationDao(): PurchasesNotificationDao

    abstract fun archiveDao(): ArchiveDao

    abstract fun trashDao(): TrashDao

    abstract fun productsDao(): ProductsDao

    abstract fun autocompletesDao(): AutocompletesDao

    abstract fun addEditProductDao(): AddEditProductDao

    abstract fun addEditAutocompleteDao(): AddEditAutocompleteDao

    abstract fun editReminderDao(): EditReminderDao

    abstract fun editShoppingListDao(): EditShoppingListNameDao

    abstract fun copyProductDao(): CopyProductDao

    abstract fun moveProductDao(): MoveProductDao

    abstract fun calculateChangeDao(): CalculateChangeDao

    abstract fun mainDao(): MainDao
}