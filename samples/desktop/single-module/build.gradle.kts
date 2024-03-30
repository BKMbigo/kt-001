import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("io.github.bkmbigo.gallery")

    // WORKAROUND
    id("io.github.bkmbigo.gallery.ksp")
}

repositories {
    mavenCentral()
    google()
    mavenLocal()
}

dependencies {
    implementation(compose.material3)
    implementation(compose.components.uiToolingPreview)
    @OptIn(ExperimentalComposeLibrary::class)
    implementation(compose.desktop.components.splitPane)

    implementation("io.github.bkmbigo.gallery:api-design") // In this example, I am going to create design components
}

compose {
    desktop {
        application {

        }
    }
}

gallery {
    desktopTarget {
        enabled = true
    }
}
