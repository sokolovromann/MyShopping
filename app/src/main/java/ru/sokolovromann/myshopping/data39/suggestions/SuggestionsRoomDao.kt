package ru.sokolovromann.myshopping.data39.suggestions

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SuggestionsRoomDao {

    @Transaction
    @Query("SELECT * FROM api39_suggestions")
    fun observeAll(): Flow<List<SuggestionWithDetailsRoomEntity>>

    @Transaction
    @Query("SELECT * FROM api39_suggestions WHERE uid = :uid")
    fun observe(uid: String): Flow<SuggestionWithDetailsRoomEntity?>

    @Transaction
    @Query("SELECT * FROM api39_suggestions")
    fun getAll(): List<SuggestionWithDetailsRoomEntity>

    @Transaction
    @Query("SELECT * FROM api39_suggestions WHERE uid = :uid")
    fun get(uid: String): SuggestionWithDetailsRoomEntity?

    @Insert(onConflict = REPLACE)
    fun insertAll(suggestions: List<SuggestionRoomEntity>)

    @Insert(onConflict = REPLACE)
    fun insert(suggestion: SuggestionRoomEntity)

    @Query("DELETE FROM api39_suggestions WHERE uid IN(:uids)")
    fun deleteAll(uids: List<String>)

    @Query("DELETE FROM api39_suggestions WHERE uid = :uid")
    fun delete(uid: String)

    @Query("DELETE FROM api39_suggestions")
    fun clear()
}