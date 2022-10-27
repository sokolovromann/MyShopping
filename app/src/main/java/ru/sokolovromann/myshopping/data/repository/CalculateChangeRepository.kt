package ru.sokolovromann.myshopping.data.repository

import kotlinx.coroutines.flow.Flow
import ru.sokolovromann.myshopping.data.repository.model.CalculateChange

interface CalculateChangeRepository {

    suspend fun getCalculateChange(uid: String): Flow<CalculateChange?>
}