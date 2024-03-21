plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") // Here, we use the official Kotlin Symbol Processing
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.23-1.0.19")

}
