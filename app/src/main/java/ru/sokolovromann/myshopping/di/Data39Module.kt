package ru.sokolovromann.myshopping.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.sokolovromann.myshopping.data39.LocalBase64
import ru.sokolovromann.myshopping.data39.LocalFile
import ru.sokolovromann.myshopping.data39.LocalJson
import ru.sokolovromann.myshopping.data39.LocalRoomDatabase
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetailsMapper
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionDetailsRepository
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsConfigMapper
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsConfigRepository
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsMapper
import ru.sokolovromann.myshopping.data39.suggestions.SuggestionsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Data39Module {

    // DATA

    @Provides
    fun providesLocalBase64(): LocalBase64 {
        return LocalBase64()
    }

    @Provides
    fun providesLocalJson(): LocalJson {
        return LocalJson()
    }

    @Singleton
    @Provides
    fun providesLocalFile(@ApplicationContext context: Context): LocalFile {
        return LocalFile(context)
    }

    @Singleton
    @Provides
    fun providesLocalRoomDatabase(@ApplicationContext context: Context): LocalRoomDatabase {
        return LocalRoomDatabase.build(context)
    }

    // SUGGESTIONS CONFIG

    @Provides
    fun providesSuggestionsConfigRepository(
        @ApplicationContext context: Context,
        mapper: SuggestionsConfigMapper
    ): SuggestionsConfigRepository {
        return SuggestionsConfigRepository(context, mapper)
    }

    @Provides
    fun providesSuggestionsConfigMapper(): SuggestionsConfigMapper {
        return SuggestionsConfigMapper()
    }

    // SUGGESTIONS

    @Provides
    fun providesSuggestionsRepository(
        database: LocalRoomDatabase,
        mapper: SuggestionsMapper
    ): SuggestionsRepository {
        return SuggestionsRepository(database.getSuggestionsDao(), mapper)
    }

    @Provides
    fun providesSuggestionsMapper(detailsMapper: SuggestionDetailsMapper): SuggestionsMapper {
        return SuggestionsMapper(detailsMapper)
    }

    // SUGGESTION DETAILS

    @Provides
    fun providesSuggestionDetailsRepository(
        database: LocalRoomDatabase,
        mapper: SuggestionDetailsMapper
    ): SuggestionDetailsRepository {
        return SuggestionDetailsRepository(database.getSuggestionDetailsDao(), mapper)
    }

    @Provides
    fun providesSuggestionDetailsMapper(): SuggestionDetailsMapper {
        return SuggestionDetailsMapper()
    }
}