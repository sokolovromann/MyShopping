package ru.sokolovromann.myshopping.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.sokolovromann.myshopping.data39.old.Api15Repository
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetailsRepository
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsConfigRepository
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsRepository
import ru.sokolovromann.myshopping.manager.Api15Manager
import ru.sokolovromann.myshopping.manager.SuggestionsManager

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    @Provides
    fun providesApi15Manager(api15Repository: Api15Repository): Api15Manager {
        return Api15Manager(api15Repository)
    }

    @Provides
    fun providesSuggestionsManager(
        suggestionsRepository: SuggestionsRepository,
        suggestionsConfigRepository: SuggestionsConfigRepository,
        detailsRepository: SuggestionDetailsRepository,
    ): SuggestionsManager {
        return SuggestionsManager(suggestionsRepository, suggestionsConfigRepository, detailsRepository)
    }
}