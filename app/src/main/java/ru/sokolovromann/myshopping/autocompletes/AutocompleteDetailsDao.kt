package ru.sokolovromann.myshopping.autocompletes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
interface AutocompleteDetailsDao {

    @Insert(onConflict = REPLACE)
    fun insert(details: Set<AutocompleteDetailsEntity>)

    @Query("DELETE FROM api_39_autocomplete_details WHERE id IN(:ids)")
    fun deleteByIds(ids: Set<String>)

    @Query("DELETE FROM api_39_autocomplete_details")
    fun clear()
}