package ru.sokolovromann.myshopping.data39.suggestions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.flowOnIo
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import ru.sokolovromann.myshopping.utils.UID
import javax.inject.Inject

class SuggestionsRepository @Inject constructor(
    private val suggestionsRoomDao: SuggestionsRoomDao,
    private val suggestionsMapper: SuggestionsMapper
) {

    fun observeAll(): Flow<Collection<SuggestionWithDetails>> {
        return suggestionsRoomDao.observeAll()
            .map { suggestionsMapper.fromEntitiesWithDetails(it) }
            .flowOnIo()
    }

    fun observe(uid: UID): Flow<SuggestionWithDetails?> {
        val strUid = suggestionsMapper.fromUid(uid)
        return suggestionsRoomDao.observe(strUid).map { suggestions ->
            suggestions?.let { suggestionsMapper.fromEntityWithDetails(it) }
        }.flowOnIo()
    }

    suspend fun getAll(): Collection<SuggestionWithDetails> = withIoContext {
        val entities = suggestionsRoomDao.getAll()
        return@withIoContext suggestionsMapper.fromEntitiesWithDetails(entities)
    }

    suspend fun get(uid: UID): SuggestionWithDetails? = withIoContext {
        val strUid = suggestionsMapper.fromUid(uid)
        val entity = suggestionsRoomDao.get(strUid)
        return@withIoContext entity?.let {
            suggestionsMapper.fromEntityWithDetails(it)
        }
    }

    suspend fun insertAll(suggestions: Collection<Suggestion>): Unit = withIoContext {
        val entities = suggestionsMapper.toEntities(suggestions)
        suggestionsRoomDao.insertAll(entities)
    }

    suspend fun insert(suggestion: Suggestion): Unit = withIoContext {
        val entity = suggestionsMapper.toEntity(suggestion)
        suggestionsRoomDao.insert(entity)
    }

    suspend fun deleteAll(uids: Collection<UID>): Unit = withIoContext {
        val strUids = suggestionsMapper.fromUids(uids)
        suggestionsRoomDao.deleteAll(strUids)
    }

    suspend fun delete(uid: UID): Unit = withIoContext {
        val strUid = suggestionsMapper.fromUid(uid)
        suggestionsRoomDao.delete(strUid)
    }

    suspend fun clear(): Unit = withIoContext {
        suggestionsRoomDao.clear()
    }
}