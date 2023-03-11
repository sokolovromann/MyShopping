package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity

@Dao
interface AutocompletesDao {

    @Query("SELECT * FROM autocompletes WHERE personal = 0")
    fun getDefaultAutocompletes(): Flow<List<AutocompleteEntity>>

    @Query("SELECT * FROM autocompletes WHERE personal = 1")
    fun getPersonalAutocompletes(): Flow<List<AutocompleteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAutocomplete(autocompleteEntity: AutocompleteEntity)

    @Query("DELETE FROM autocompletes WHERE uid = :uid")
    fun deleteAutocomplete(uid: String)
}