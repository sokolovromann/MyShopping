package ru.sokolovromann.myshopping.data39.old

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OldAutocompletesDao {

    @Query("SELECT * FROM autocompletes")
    fun getAllAutocompletes(): Flow<List<OldAutocompleteEntity>>

    @Query("SELECT * FROM autocompletes WHERE personal = 0")
    fun getDefaultAutocompletes(): Flow<List<OldAutocompleteEntity>>

    @Query("SELECT * FROM autocompletes WHERE personal = 1")
    fun getPersonalAutocompletes(): Flow<List<OldAutocompleteEntity>>

    @Query("SELECT * FROM autocompletes WHERE name LIKE '%' || :search || '%'")
    fun searchAutocompletesLikeName(search: String): Flow<List<OldAutocompleteEntity>>

    @Query("SELECT * FROM autocompletes WHERE uid = :uid")
    fun getAutocomplete(uid: String): Flow<OldAutocompleteEntity?>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insertAutocompletes(autocompletes: List<OldAutocompleteEntity>)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insertAutocomplete(autocomplete: OldAutocompleteEntity)

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