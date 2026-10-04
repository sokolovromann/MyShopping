import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "ru.sokolovromann.myshopping"
    buildToolsVersion = libs.versions.myShopping.buildTools.get()
    compileSdk = libs.versions.myShopping.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "ru.sokolovromann.myshopping"
        versionCode = libs.versions.myShopping.versionCode.get().toInt()
        versionName = libs.versions.myShopping.versionName.get()
        minSdk = libs.versions.myShopping.minSdk.get().toInt()
        targetSdk = libs.versions.myShopping.targetSdk.get().toInt()

        resValue("string", "app_version_code", "\"${libs.versions.myShopping.versionCode.get()}\"")
        resValue("string", "app_version_name", "\"${libs.versions.myShopping.versionName.get()}\"")
    }

    buildTypes {
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isDebuggable = true
            isShrinkResources = true
            isMinifyEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        resValues = true
        buildConfig = false
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlin.compilerExtension.get()
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
    implementation(project(":feature:addeditsuggestion"))
    implementation(project(":feature:dictionary"))
    implementation(libs.android.core)
    implementation(libs.hilt.android)
    implementation(libs.navigation)
    implementation(libs.splashscreen)
    ksp(libs.hilt.compiler)
}