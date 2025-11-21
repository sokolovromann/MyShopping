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
}