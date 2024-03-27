plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
//    id("com.google.devtools.ksp") // Here, we use the official Kotlin Symbol Processing
}

group = "io.github.bkmbigo.gallery"

repositories {
    mavenCentral()
    google()
    mavenLocal()
}

dependencies {
    implementation("io.github.bkmbigo.gallery.ksp:symbol-processing-api:0.0.4") // The API used in the processor is the custom KSP

//    implementation("com.google.auto.service:auto-service:1.0.1")
//    implementation("com.google.auto.service:auto-service-annotations:1.0.1")
//    ksp("dev.zacsweers.autoservice:auto-service-ksp:1.1.0")

    implementation("com.squareup:kotlinpoet:1.16.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Gallery APIs
    implementation("io.github.bkmbigo.gallery:api")
    implementation("io.github.bkmbigo.gallery:api-design")

    testImplementation(kotlin("test"))
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}
