pluginManagement {
    plugins {
        id("com.google.devtools.ksp") version "2.0.21-1.0.17" apply false
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    }
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OldBalance"
include(":app")
