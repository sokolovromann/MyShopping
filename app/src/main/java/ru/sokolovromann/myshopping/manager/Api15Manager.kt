package ru.sokolovromann.myshopping.manager

import ru.sokolovromann.myshopping.data39.old.Api15AutocompleteEntity
import ru.sokolovromann.myshopping.data39.old.Api15AutocompletesConfigEntity
import ru.sokolovromann.myshopping.data39.old.Api15Repository
import ru.sokolovromann.myshopping.utils.DispatcherExtensions.withIoContext
import javax.inject.Inject

class Api15Manager @Inject constructor(private val api15Repository: Api15Repository) {

    suspend fun getAutocompletes(): Collection<Api15AutocompleteEntity> = withIoContext {
        val displayDefault = getAutocompletesConfig().displayDefaultAutocompletes
        return@withIoContext api15Repository.getAutocompletes(displayDefault)
            .sortedBy { it.lastModified }
    }

    suspend fun getAutocompletesConfig(): Api15AutocompletesConfigEntity = withIoContext {
        return@withIoContext api15Repository.getAutocompletesConfig()
    }
}