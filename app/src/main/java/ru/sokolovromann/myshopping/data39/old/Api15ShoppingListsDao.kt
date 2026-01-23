package ru.sokolovromann.myshopping.data39.old

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface Api15ShoppingListsDao {

    @Transaction
    @Query("SELECT * FROM shoppings")
    fun getAllShoppingLists(): Flow<List<Api15ShoppingListEntity>>

    @Query("SELECT * FROM shoppings")
    fun getAllShoppings(): Flow<List<Api15ShoppingEntity>>

    @Transaction
    @Query("SELECT * FROM shoppings WHERE archived = 0 AND deleted = 0")
    fun getPurchases(): Flow<List<Api15ShoppingListEntity>>

    @Transaction
    @Query("SELECT * FROM shoppings WHERE archived = 1 AND deleted = 0")
    fun getArchive(): Flow<List<Api15ShoppingListEntity>>

    @Transaction
    @Query("SELECT * FROM shoppings WHERE archived = 1 AND deleted = 0 AND last_modified >= :minLastModified")
    fun getArchive(minLastModified: Long): Flow<List<Api15ShoppingListEntity>>

    @Transaction
    @Query("SELECT * FROM shoppings WHERE archived = 0 AND deleted = 1")
    fun getTrash(): Flow<List<Api15ShoppingListEntity>>

    @Transaction
    @Query("SELECT * FROM shoppings WHERE reminder > 0")
    fun getReminders(): Flow<List<Api15ShoppingListEntity>>

    @Transaction
    @Query("SELECT * FROM shoppings WHERE archived = 0 AND deleted = 0 ORDER BY last_modified DESC LIMIT :limit")
    fun getShortcuts(limit: Int): Flow<List<Api15ShoppingListEntity>>

    @Transaction
    @Query("SELECT * FROM shoppings WHERE uid = :uid")
    fun getShoppingList(uid: String): Flow<Api15ShoppingListEntity?>

    @Query("SELECT position FROM shoppings ORDER BY position ASC LIMIT 1")
    fun getFirstPosition(): Flow<Int?>

    @Query("SELECT position FROM shoppings ORDER BY position DESC LIMIT 1")
    fun getLastPosition(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insertShoppings(shoppings: List<Api15ShoppingEntity>)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insertShopping(shopping: Api15ShoppingEntity)

    @Query("UPDATE shoppings SET position = :position WHERE uid = :uid")
    fun updatePosition(uid: String, position: Int)

    @Query("UPDATE shoppings SET last_modified = :lastModified WHERE uid = :uid")
    fun updateLastModified(uid: String, lastModified: Long)

    @Query("UPDATE shoppings SET name = :name, last_modified = :lastModified WHERE uid = :uid")
    fun updateName(uid: String, name: String, lastModified: Long)

    @Query("UPDATE shoppings SET reminder = :reminder, last_modified = :lastModified WHERE uid = :uid")
    fun updateReminder(uid: String, reminder: Long, lastModified: Long)

    @Query("UPDATE shoppings SET total = :total, total_formatted = 1, discount = :discount, discount_as_percent = :discountAsPercent, last_modified = :lastModified WHERE uid = :uid")
    fun updateTotal(uid: String, total: Float, discount: Float, discountAsPercent: Boolean, lastModified: Long)

    @Query("UPDATE shoppings SET budget = :budget, budget_products = :budgetProducts, last_modified = :lastModified WHERE uid = :uid")
    fun updateBudget(uid: String, budget: Float, budgetProducts: String, lastModified: Long)

    @Query("UPDATE shoppings SET pinned = 1 WHERE uid IN (:uids)")
    fun pinShoppings(uids: List<String>)

    @Query("UPDATE shoppings SET pinned = 0 WHERE uid IN (:uids)")
    fun unpinShoppings(uids: List<String>)

    @Query("UPDATE shoppings SET archived = 0, deleted = 0, reminder = 0, last_modified = :lastModified WHERE uid IN (:uids)")
    fun moveToPurchases(uids: List<String>, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 0, deleted = 0, reminder = 0, last_modified = :lastModified WHERE uid = :uid")
    fun moveToPurchases(uid: String, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 1, deleted = 0, reminder = 0, last_modified = :lastModified WHERE uid IN (:uids)")
    fun moveToArchive(uids: List<String>, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 1, deleted = 0, reminder = 0, last_modified = :lastModified WHERE uid = :uid")
    fun moveToArchive(uid: String, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 0, deleted = 1, reminder = 0, last_modified = :lastModified WHERE uid IN (:uids)")
    fun moveToTrash(uids: List<String>, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 0, deleted = 1, reminder = 0, last_modified = :lastModified WHERE uid = :uid")
    fun moveToTrash(uid: String, lastModified: Long)

    @Query("UPDATE shoppings SET sort_by = :sortBy, sort_ascending = :sortAscending, sort_formatted = 1, last_modified = :lastModified WHERE uid = :uid")
    fun enableAutomaticSorting(uid: String, sortBy: String, sortAscending: Boolean, lastModified: Long)

    @Query("UPDATE shoppings SET sort_by = :sortBy, sort_ascending = :sortAscending, sort_formatted = 0, last_modified = :lastModified WHERE uid = :uid")
    fun disableAutomaticSorting(uid: String, sortBy: String, sortAscending: Boolean, lastModified: Long)

    @Query("DELETE FROM shoppings")
    fun deleteAllShoppings()

    @Query("DELETE FROM shoppings WHERE uid IN (:uids)")
    fun deleteShoppingsByUids(uids: List<String>)

    @Query("UPDATE shoppings SET reminder = 0, last_modified = :lastModified WHERE uid IN (:uids)")
    fun deleteReminders(uids: List<String>, lastModified: Long)

    @Query("UPDATE shoppings SET total = 0, total_formatted = 0, discount = 0, discount_as_percent = 0, last_modified = :lastModified WHERE uid = :uid")
    fun deleteTotal(uid: String, lastModified: Long)
}