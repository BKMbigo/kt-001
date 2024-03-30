plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(kotlin("gradle-plugin"))
    compileOnly(kotlin("gradle-plugin-api"))
    compileOnly(gradleApi())

    // Compose Compiler Artifact
    compileOnly("org.jetbrains.compose:compose-gradle-plugin:1.6.1")

    // Android Gradle Plugin Artifact
    compileOnly("com.android.tools.build:gradle:8.2.0")
}

gradlePlugin {
    plugins {
        create("gallery-gradle-plugin") {
            id = "io.github.bkmbigo.gallery"
            displayName = "Gallery Gradle Plugin"
            implementationClass = "io.github.bkmbigo.gallery.gradle.GalleryGradlePlugin"
        }
    }
}
