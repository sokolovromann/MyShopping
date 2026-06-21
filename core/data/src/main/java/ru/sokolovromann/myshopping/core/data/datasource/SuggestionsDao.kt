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
    @Query("SELECT * FROM api39_suggestions")
    fun observeSuggestionWithFabrics(): Flow<List<SuggestionWithFabricsEntity>>

    @Query("SELECT * FROM api39_suggestions WHERE uid = :uid")
    fun getSuggestionWithFabrics(uid: String): SuggestionWithFabricsEntity?

    @Insert(onConflict = REPLACE)
    fun insertSuggestions(suggestions: Collection<SuggestionEntity>)

    @Query("DELETE FROM api39_suggestion_details WHERE uid IN(:uids)")
    fun deleteSuggestions(uids: Collection<String>)

    @Query("DELETE FROM api39_suggestion_details")
    fun clearSuggestions()
}