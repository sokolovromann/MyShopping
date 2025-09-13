package ru.sokolovromann.myshopping.autocompletes

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AutocompleteWithDetailsDao {

    @Transaction
    @Query("SELECT * FROM api_39_autocompletes")
    fun observeAll(): Flow<List<AutocompleteWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM api_39_autocompletes WHERE id = :id")
    fun observeById(id: String): Flow<AutocompleteWithDetailsEntity?>
}