import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.buildInKotlin)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "ru.sokolovromann.myshopping.core.ui"
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
    api(libs.activity.compose)
    api(libs.compose.ui)
    api(libs.compose.ui.preview)
    api(libs.hilt.navigation)
    api(libs.kotlin.collections.immutable)
    api(libs.material3)
    api(libs.material3.adaptive)
    api(libs.material3.icons)
    api(libs.material3.windowSize)
    implementation(libs.android.core)
}