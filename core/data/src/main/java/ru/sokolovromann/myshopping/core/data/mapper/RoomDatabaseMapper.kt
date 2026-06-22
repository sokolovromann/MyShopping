package ru.sokolovromann.myshopping.core.data.mapper

import ru.sokolovromann.myshopping.core.domain.model.Position
import ru.sokolovromann.myshopping.core.domain.model.TimeInMillis
import ru.sokolovromann.myshopping.core.domain.model.UID

abstract class RoomDatabaseMapper<E,M> {

    abstract fun toEntity(model: M): E

    abstract fun toModel(entity: E): M

    fun toEntities(models: Collection<M>) = models.map { toEntity(it) }

    fun toModels(entities: Collection<E>) = entities.map { toModel(it) }

    fun toUidsStrings(uids: Collection<UID>) = uids.map { it.value }

    fun toPositionOrMin(str: String) =
        str.toIntOrNull()?.let { Position(it) } ?: Position.MIN

    fun toTimeInMillisOrCurrent(str: String) =
        str.toLongOrNull()?.let { TimeInMillis(it) } ?: TimeInMillis.getCurrent()
}