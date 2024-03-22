plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

group = "io.github.bkmbigo.gallery"

kotlin {
    androidTarget()
    jvm("desktop")
    js(IR) {
        browser()
    }
    wasmJs {
        browser()
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
}
