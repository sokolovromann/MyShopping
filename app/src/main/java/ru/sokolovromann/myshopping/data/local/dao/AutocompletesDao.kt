package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity

@Dao
interface AutocompletesDao {

    @Query("SELECT * FROM autocompletes")
    fun getAutocompletes(): Flow<List<AutocompleteEntity>>

    @Query("DELETE FROM autocompletes WHERE uid = :uid")
    fun deleteAutocomplete(uid: String)
}