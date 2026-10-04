import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.buildInKotlin)
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    namespace = "ru.sokolovromann.myshopping.core.data"
    buildToolsVersion = libs.versions.myShopping.buildTools.get()
    compileSdk = libs.versions.myShopping.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.myShopping.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(libs.android.core)
    implementation(libs.datastore)
    implementation(libs.hilt.android)
    implementation(libs.kotlin.serialization)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.hilt.compiler)
    ksp(libs.room.compiler)
    androidTestImplementation(libs.test.espresso)
    androidTestImplementation(libs.test.junit.android)
    testImplementation(libs.test.junit)
}