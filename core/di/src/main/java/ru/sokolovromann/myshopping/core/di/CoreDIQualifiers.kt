package ru.sokolovromann.myshopping.core.di

import jakarta.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class MainDispatcher