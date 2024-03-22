plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("io.github.bkmbigo.gallery.ksp")
}

kotlin {
    androidTarget()
    jvm("desktop")
    js(IR) {
        browser()
    }
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.material3)

            implementation("io.github.bkmbigo.gallery:api")
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

android {
    namespace = "io.github.bkmbigo.gallery"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

dependencies {
    add(
        "ksp",
        "io.github.bkmbigo.gallery:processor"
    )
}
