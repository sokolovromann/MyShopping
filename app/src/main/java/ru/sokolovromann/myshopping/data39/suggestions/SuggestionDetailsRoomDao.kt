package ru.sokolovromann.myshopping.data39.suggestions

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
interface SuggestionDetailsRoomDao {

    @Query("SELECT * FROM api39_suggestion_details")
    fun getAll(): List<SuggestionDetailRoomEntity>

    @Query("SELECT * FROM api39_suggestion_details WHERE uid = :uid")
    fun get(uid: String): SuggestionDetailRoomEntity

    @Insert(onConflict = REPLACE)
    fun insertAll(details: List<SuggestionDetailRoomEntity>)

    @Insert(onConflict = REPLACE)
    fun insert(detail: SuggestionDetailRoomEntity)

    @Query("DELETE FROM api39_suggestion_details WHERE uid IN(:uids)")
    fun deleteAll(uids: List<String>)

    @Query("DELETE FROM api39_suggestion_details WHERE uid = :uid")
    fun delete(uid: String)

    @Query("DELETE FROM api39_suggestion_details")
    fun clear()
}