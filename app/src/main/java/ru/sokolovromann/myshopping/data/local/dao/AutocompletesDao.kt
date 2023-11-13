package ru.sokolovromann.myshopping.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.local.entity.AutocompleteEntity

@Dao
interface AutocompletesDao {

    @Query("SELECT * FROM autocompletes")
    fun getAllAutocompletes(): Flow<List<AutocompleteEntity>>

    @Query("SELECT * FROM autocompletes WHERE personal = 0")
    fun getDefaultAutocompletes(): Flow<List<AutocompleteEntity>>

    @Query("SELECT * FROM autocompletes WHERE personal = 1")
    fun getPersonalAutocompletes(): Flow<List<AutocompleteEntity>>

    @Query("SELECT * FROM autocompletes WHERE name LIKE '%' || :search || '%'")
    fun searchAutocompletesLikeName(search: String): Flow<List<AutocompleteEntity>>

    @Query("SELECT * FROM autocompletes WHERE uid = :uid")
    fun getAutocomplete(uid: String): Flow<AutocompleteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAutocompletes(autocompletes: List<AutocompleteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAutocomplete(autocomplete: AutocompleteEntity)

    @Query("UPDATE autocompletes SET " +
            "last_modified = :lastModifier," +
            "quantity = 0, " +
            "quantity_symbol = '', " +
            "price = 0, " +
            "discount = 0, " +
            "discount_as_percent = 0, " +
            "tax_rate = 0, " +
            "tax_rate_as_percent = 0, " +
            "total = 0, " +
            "manufacturer = '', " +
            "brand = '', " +
            "size = '', " +
            "color = '', " +
            "provider = '' " +
            "WHERE uid IN (:uids)")
    fun clearAutocompletes(uids: List<String>, lastModifier: Long)

    @Query("DELETE FROM autocompletes")
    fun deleteAllAutocompletes()

    @Query("DELETE FROM autocompletes WHERE uid IN (:uids)")
    fun deleteAutocompletes(uids: List<String>)
}