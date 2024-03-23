plugins {
    kotlin("jvm")
//    id("com.google.devtools.ksp") // Here, we use the official Kotlin Symbol Processing
}

group = "io.github.bkmbigo.gallery"

repositories {
    mavenCentral()
    google()
    mavenLocal()
}

dependencies {
    implementation("io.github.bkmbigo.gallery.ksp:symbol-processing-api:0.0.3") // The API used in the processor is the custom KSP

//    implementation("com.google.auto.service:auto-service:1.0.1")
//    implementation("com.google.auto.service:auto-service-annotations:1.0.1")
//    ksp("dev.zacsweers.autoservice:auto-service-ksp:1.1.0")

//    implementation("com.squareup:kotlinpoet-ksp:1.16.0")

    // Gallery APIs
    implementation("io.github.bkmbigo.gallery:api")
    implementation("io.github.bkmbigo.gallery:api-design")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}
