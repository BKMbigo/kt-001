import com.google.devtools.ksp.configureMetalava
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "io.github.bkmbigo.gallery.ksp"
description = "Kotlin Symbol Processing API"

val signingKey: String? by project
val signingPassword: String? by project
val kotlinBaseVersion: String by project

tasks.withType<KotlinCompile> {
    compilerOptions.freeCompilerArgs.add("-Xjvm-default=all-compatibility")
}

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
}

tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }
}

val dokkaJavadocJar by tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        create<MavenPublication>("default") {
            artifactId = "symbol-processing-api"
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["dokkaJavadocJar"])
            pom {
                name.set("io.github.bkmbigo.gallery.ksp:symbol-processing-api")
                description.set("Symbol processing for Kotlin")
            }
        }
    }
}

signing {
    isRequired = hasProperty("signingKey")
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(extensions.getByType<PublishingExtension>().publications)
}

//configureMetalava()
