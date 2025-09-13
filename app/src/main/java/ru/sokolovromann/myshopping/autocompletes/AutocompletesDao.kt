package ru.sokolovromann.myshopping.autocompletes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
interface AutocompletesDao {

    @Query("SELECT * FROM api_39_autocompletes")
    fun getAll(): List<AutocompleteEntity>

    @Query("SELECT * FROM api_39_autocompletes WHERE id = :id")
    fun getById(id: String): AutocompleteEntity?

    @Insert(onConflict = REPLACE)
    fun insert(autocompletes: Set<AutocompleteEntity>)

    @Query("DELETE FROM api_39_autocompletes WHERE id IN(:ids)")
    fun deleteByIds(ids: Set<String>)

    @Query("DELETE FROM api_39_autocompletes")
    fun clear()
}