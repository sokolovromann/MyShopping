import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "ru.sokolovromann.myshopping.feature.addeditsuggestion"
    buildToolsVersion = libs.versions.myShopping.buildTools.get()
    compileSdk = libs.versions.myShopping.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.myShopping.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
    implementation(libs.android.core)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}