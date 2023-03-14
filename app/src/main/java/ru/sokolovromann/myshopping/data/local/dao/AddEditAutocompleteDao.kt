package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity

@Dao
interface AddEditAutocompleteDao {

    @Query("SELECT * FROM autocompletes WHERE uid = :uid")
    fun getAutocomplete(uid: String): Flow<AutocompleteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAutocomplete(autocompleteEntity: AutocompleteEntity)
}