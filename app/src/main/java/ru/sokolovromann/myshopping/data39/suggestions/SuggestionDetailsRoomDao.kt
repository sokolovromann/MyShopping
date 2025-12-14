package ru.sokolovromann.myshopping.data39.suggestions

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
interface SuggestionDetailsRoomDao {

    @Query("SELECT * FROM api39_suggestion_details")
    fun get(): List<SuggestionDetailRoomEntity>

    @Insert(onConflict = REPLACE)
    fun insert(details: List<SuggestionDetailRoomEntity>)

    @Query("DELETE FROM api39_suggestion_details WHERE uid IN(:uids)")
    fun delete(uids: List<String>)

    @Query("DELETE FROM api39_suggestion_details")
    fun clear()
}