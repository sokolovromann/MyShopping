package ru.sokolovromann.myshopping.data39.suggestions

import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import ru.sokolovromann.myshopping.utils.UID
import javax.inject.Inject

class SuggestionDetailsRepository @Inject constructor(
    private val detailsRoomDao: SuggestionDetailsRoomDao,
    private val detailsMapper: SuggestionDetailsMapper
) {

    suspend fun getAll(): SuggestionDetails = withIoContext {
        val entities = detailsRoomDao.getAll()
        return@withIoContext detailsMapper.toDetails(entities)
    }

    suspend fun get(uid: UID): SuggestionDetail? = withIoContext {
        val strUid = detailsMapper.fromUid(uid)
        val entity = detailsRoomDao.get(strUid)
        return@withIoContext entity?.let {
            detailsMapper.fromEntity(it)
        }
    }

    suspend fun insertAll(details: Collection<SuggestionDetail>): Unit = withIoContext {
        val entities = detailsMapper.toEntities(details)
        detailsRoomDao.insertAll(entities)
    }

    suspend fun insert(detail: SuggestionDetail): Unit = withIoContext {
        val entity = detailsMapper.toEntity(detail)
        detailsRoomDao.insert(entity)
    }

    suspend fun deleteAll(uids: Collection<UID>): Unit = withIoContext {
        val strUids = detailsMapper.fromUids(uids)
        detailsRoomDao.deleteAll(strUids)
    }

    suspend fun delete(uid: UID): Unit = withIoContext {
        val strUid = detailsMapper.fromUid(uid)
        detailsRoomDao.delete(strUid)
    }

    suspend fun clear(): Unit = withIoContext {
        detailsRoomDao.clear()
    }
}