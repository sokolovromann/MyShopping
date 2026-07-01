package ru.sokolovromann.myshopping.core.data.repository

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.data.datasource.FabricsDao
import ru.sokolovromann.myshopping.core.data.mapper.FabricsMapper
import ru.sokolovromann.myshopping.core.domain.model.Fabric
import ru.sokolovromann.myshopping.core.domain.model.FabricDirectory
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.domain.repository.FabricsRepository

class FabricsRepositoryImpl @Inject constructor(
    private val fabricsDao: FabricsDao,
    private val fabricsMapper: FabricsMapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FabricsRepository {

    override suspend fun insertFabrics(fabrics: Collection<Fabric>): Unit =
        withContext(ioDispatcher) {
            val entities = fabricsMapper.toEntities(fabrics)
            fabricsDao.insertFabrics(entities)
        }

    override suspend fun deleteFabrics(directory: FabricDirectory): Unit =
        withContext(ioDispatcher) {
            fabricsDao.deleteFabrics(directory.value.value)
        }

    override suspend fun deleteFabrics(uids: Collection<UID>): Unit =
        withContext(ioDispatcher) {
            val uidsStrings = fabricsMapper.toUidsStrings(uids)
            fabricsDao.deleteFabrics(uidsStrings)
        }

    override suspend fun clearFabrics(): Unit =
        withContext(ioDispatcher) {
            fabricsDao.clearFabrics()
        }
}