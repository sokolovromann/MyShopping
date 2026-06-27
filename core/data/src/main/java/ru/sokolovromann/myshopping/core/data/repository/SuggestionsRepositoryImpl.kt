package ru.sokolovromann.myshopping.core.data.repository

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.datasource.SuggestionsDao
import ru.sokolovromann.myshopping.core.data.mapper.SuggestionsMapper
import ru.sokolovromann.myshopping.core.data.mapper.SuggestionsWithFabricsMapper
import ru.sokolovromann.myshopping.core.domain.model.Suggestion
import ru.sokolovromann.myshopping.core.domain.model.SuggestionWithFabrics
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.repository.SuggestionsRepository

class SuggestionsRepositoryImpl @Inject constructor(
    private val suggestionsDao: SuggestionsDao,
    private val suggestionsWithFabricsMapper: SuggestionsWithFabricsMapper,
    private val suggestionsMapper: SuggestionsMapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SuggestionsRepository {

    override fun observeSuggestionsWithFabrics(): Flow<Collection<SuggestionWithFabrics>> =
        suggestionsDao.observeSuggestionWithFabrics()
            .map { suggestionsWithFabricsMapper.toModels(it) }
            .flowOn(ioDispatcher)

    override suspend fun getSuggestionWithFabrics(uid: UID): SuggestionWithFabrics? =
        withContext(ioDispatcher) {
            suggestionsDao.getSuggestionWithFabrics(uid.value)?.let {
                suggestionsWithFabricsMapper.toModel(it)
            }
        }

    override suspend fun insertSuggestions(suggestions: Collection<Suggestion>): Unit =
        withContext(ioDispatcher) {
            val entities = suggestionsMapper.toEntities(suggestions)
            suggestionsDao.insertSuggestions(entities)
        }

    override suspend fun deleteSuggestions(uids: Collection<UID>): Unit =
        withContext(ioDispatcher) {
            val uidsStrings = suggestionsMapper.toUidsStrings(uids)
            suggestionsDao.deleteSuggestions(uidsStrings)
        }

    override suspend fun clearSuggestions(): Unit =
        withContext(ioDispatcher) {
            suggestionsDao.clearSuggestions()
        }
}