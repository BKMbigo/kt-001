pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google {
            mavenContent {
                includeGroupByRegex(".*android.*")
                includeGroupByRegex(".*google.*")
            }
        }
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google {
            mavenContent {
                includeGroupByRegex(".*android.*")
                includeGroupByRegex(".*google.*")
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "GallerySample"

includeBuild("processor")

include(":samples:android:single-module")
include(":samples:kmp:android-desktop-js-wasmjs:android")
include(":samples:kmp:android-desktop-js-wasmjs:common")
