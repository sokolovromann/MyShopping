package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.ShoppingEntity
import ru.sokolovromann.myshopping.data.local.entity.ShoppingListEntity

@Dao
interface ShoppingListsDao {

    @Transaction
    @Query("SELECT * FROM shoppings")
    fun getAllShoppingLists(): Flow<List<ShoppingListEntity>>

    @Query("SELECT * FROM shoppings")
    fun getAllShoppings(): Flow<List<ShoppingEntity>>

    @Transaction
    @Query("SELECT * FROM shoppings WHERE archived = 0 AND deleted = 0")
    fun getPurchases(): Flow<List<ShoppingListEntity>>

    @Transaction
    @Query("SELECT * FROM shoppings WHERE archived = 1 AND deleted = 0")
    fun getArchive(): Flow<List<ShoppingListEntity>>

    @Transaction
    @Query("SELECT * FROM shoppings WHERE archived = 0 AND deleted = 1")
    fun getTrash(): Flow<List<ShoppingListEntity>>

    @Transaction
    @Query("SELECT * FROM shoppings WHERE reminder > 0")
    fun getReminders(): Flow<List<ShoppingListEntity>>

    @Transaction
    @Query("SELECT * FROM shoppings WHERE archived = 0 AND deleted = 0 ORDER BY last_modified DESC LIMIT :limit")
    fun getShortcuts(limit: Int): Flow<List<ShoppingListEntity>>

    @Transaction
    @Query("SELECT * FROM shoppings WHERE uid = :uid")
    fun getShoppingList(uid: String): Flow<ShoppingListEntity?>

    @Query("SELECT position FROM shoppings ORDER BY position ASC LIMIT 1")
    fun getFirstPosition(): Flow<Int?>

    @Query("SELECT position FROM shoppings ORDER BY position DESC LIMIT 1")
    fun getLastPosition(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShoppings(shoppings: List<ShoppingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShopping(shopping: ShoppingEntity)

    @Query("UPDATE shoppings SET position = :position, last_modified = :lastModified WHERE uid = :uid")
    fun updatePosition(uid: String, position: Int, lastModified: Long)

    @Query("UPDATE shoppings SET last_modified = :lastModified WHERE uid = :uid")
    fun updateLastModified(uid: String, lastModified: Long)

    @Query("UPDATE shoppings SET name = :name, last_modified = :lastModified WHERE uid = :uid")
    fun updateName(uid: String, name: String, lastModified: Long)

    @Query("UPDATE shoppings SET reminder = :reminder, last_modified = :lastModified WHERE uid = :uid")
    fun updateReminder(uid: String, reminder: Long, lastModified: Long)

    @Query("UPDATE shoppings SET total = :total, total_formatted = 1, last_modified = :lastModified WHERE uid = :uid")
    fun updateTotal(uid: String, total: Float, lastModified: Long)

    @Query("UPDATE shoppings SET pinned = 1, last_modified = :lastModified WHERE uid IN (:uids)")
    fun pinShoppings(uids: List<String>, lastModified: Long)

    @Query("UPDATE shoppings SET pinned = 0, last_modified = :lastModified WHERE uid IN (:uids)")
    fun unpinShoppings(uids: List<String>, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 0, deleted = 0, last_modified = :lastModified WHERE uid IN (:uids)")
    fun moveToPurchases(uids: List<String>, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 0, deleted = 0, last_modified = :lastModified WHERE uid = :uid")
    fun moveToPurchases(uid: String, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 1, deleted = 0, last_modified = :lastModified WHERE uid IN (:uids)")
    fun moveToArchive(uids: List<String>, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 1, deleted = 0, last_modified = :lastModified WHERE uid = :uid")
    fun moveToArchive(uid: String, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 0, deleted = 1, last_modified = :lastModified WHERE uid IN (:uids)")
    fun moveToTrash(uids: List<String>, lastModified: Long)

    @Query("UPDATE shoppings SET archived = 0, deleted = 1, last_modified = :lastModified WHERE uid = :uid")
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

    @Query("UPDATE shoppings SET reminder = 0, last_modified = :lastModified WHERE uid = :uid")
    fun deleteReminder(uid: String, lastModified: Long)

    @Query("UPDATE shoppings SET total = 0, total_formatted = 0, last_modified = :lastModified WHERE uid = :uid")
    fun deleteTotal(uid: String, lastModified: Long)
}