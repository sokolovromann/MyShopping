package ru.sokolovromann.myshopping.core.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.core.data.model.SuggestionEntity
import ru.sokolovromann.myshopping.core.data.model.SuggestionWithFabricsEntity

@Dao
interface SuggestionsDao {

    @Transaction
    @Query("SELECT * FROM suggestions")
    fun observeSuggestionWithFabrics(): Flow<List<SuggestionWithFabricsEntity>>

    @Query("SELECT * FROM suggestions WHERE uid = :uid")
    fun getSuggestionWithFabrics(uid: String): SuggestionWithFabricsEntity?

    @Insert(onConflict = REPLACE)
    fun insertSuggestions(suggestions: Collection<SuggestionEntity>)

    @Query("DELETE FROM suggestions WHERE uid IN(:uids)")
    fun deleteSuggestions(uids: Collection<String>)

    @Query("DELETE FROM suggestions")
    fun clearSuggestions()
}