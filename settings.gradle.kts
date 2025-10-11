pluginManagement {
    repositories {
        gradlePluginPortal()  // Needed for Kotlin plugins
        google()              // Needed for Android and Firebase plugins
        mavenCentral()        // General dependencies
    }
    plugins {
        // Optional: Predeclare versions of commonly used plugins
        kotlin("android") version "1.9.10"
        id("com.android.application") version "8.2.0"
        id("com.google.gms.google-services") version "4.4.0"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NewsPulse_FINAL"
include(":app")
