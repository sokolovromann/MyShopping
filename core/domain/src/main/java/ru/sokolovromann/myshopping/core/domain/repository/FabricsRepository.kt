package ru.sokolovromann.myshopping.core.domain.repository

import ru.sokolovromann.myshopping.core.domain.model.Fabric
import ru.sokolovromann.myshopping.core.domain.model.FabricDirectory
import ru.sokolovromann.myshopping.core.domain.model.UID

interface FabricsRepository {

    suspend fun insertFabrics(fabrics: Collection<Fabric>)

    suspend fun deleteFabrics(directory: FabricDirectory)

    suspend fun deleteFabrics(uids: Collection<UID>)

    suspend fun clearFabrics()
}