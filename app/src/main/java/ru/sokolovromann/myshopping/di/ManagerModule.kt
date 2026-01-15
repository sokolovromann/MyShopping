package ru.sokolovromann.myshopping.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetailsRepository
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsConfigRepository
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsRepository
import ru.sokolovromann.myshopping.manager.SuggestionsManager

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    @Provides
    fun providesSuggestionsManager(
        suggestionsRepository: SuggestionsRepository,
        suggestionsConfigRepository: SuggestionsConfigRepository,
        detailsRepository: SuggestionDetailsRepository,
    ): SuggestionsManager {
        return SuggestionsManager(suggestionsRepository, suggestionsConfigRepository, detailsRepository)
    }
}