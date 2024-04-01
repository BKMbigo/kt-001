package io.github.bkmbigo.gallery.gradle.internal.configuration

import io.github.bkmbigo.gallery.gradle.GalleryExtension
import io.github.bkmbigo.gallery.gradle.internal.Constants
import io.github.bkmbigo.gallery.gradle.internal.desktop.JvmApplicationContext
import io.github.bkmbigo.gallery.gradle.internal.desktop.GalleryJvmApplicationInternal
import io.github.bkmbigo.gallery.gradle.internal.utils.*
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.compose.desktop.tasks.AbstractUnpackDefaultComposeApplicationResourcesTask
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.targets

internal const val APP_RESOURCES_DIR = "compose.application.resources.dir"
internal const val CONFIGURE_SWING_GLOBALS = "compose.application.configure.swing.globals"

private val defaultJvmArgs = listOf("-D$CONFIGURE_SWING_GLOBALS=true")

internal fun configureDesktopGallery(
    project: Project,
    galleryExtension: GalleryExtension,
    kotlinExtension: KotlinProjectExtension
) {
    // Check if desktop gallery target is enabled
    if (galleryExtension.desktopGalleryTarget.enabled.get() == true) {
        // Check if project has compose-multiplatform plugin
        if (!project.project.hasComposeMultiplatformPlugin) {
            project.logger.error(
                "Gallery cannot be applied to desktop target without Compose Multiplatform Plugin" +
                        "Ensure you add the plugin `org.jetbrains.compose` to the module. See instructions at https://jb.gg/start-cmp"
            )
        } else {
            if (!(project.project.hasKotlinJvmPlugin || kotlinExtension.targets.any { it.platformType == KotlinPlatformType.jvm })) {
                project.logger.error("Gallery cannot find a jvm target in the module. Cannot apply desktop target")
            } else {
                // create desktopGallery configuration
                GalleryConfigurations.createGalleryConfiguration(
                    kotlinExtension = kotlinExtension,
                    platform = KotlinPlatformType.jvm
                )

                // Add API to gallery configuration
                if (project.hasKotlinMultiplatformPlugin) {
                    project.dependencies.add(
                        "galleryDesktopImplementation",
                        "${Constants.GALLERY_GROUP}:${Constants.GALLERY_API_NAME}"
                    )

                    // Add processor to galleryKsp
                    project.dependencies.add(
                        "galleryKspDesktopGallery",
                        "${Constants.GALLERY_GROUP}:${Constants.GALLERY_API_NAME}"
                    )
                    project.dependencies.add(
                        "galleryKspDesktopGallery",
                        "${Constants.GALLERY_GROUP}:${Constants.GALLERY_PROCESSOR_NAME}"
                    )

                    // Add navigation library to the configuration
                    configureGalleryNavigation(project, "galleryDesktopImplementation")
                } else if (project.hasKotlinJvmPlugin) {
                    project.dependencies.add(
                        "galleryImplementation",
                        "${Constants.GALLERY_GROUP}:${Constants.GALLERY_API_NAME}"
                    )

                    // Add processor to galleryKsp
                    project.dependencies.add(
                        "galleryKspGallery",
                        "${Constants.GALLERY_GROUP}:${Constants.GALLERY_PROCESSOR_NAME}"
                    )

                    // Add navigation library to the configuration
                    configureGalleryNavigation(project, "galleryImplementation")

                    val appInternal = createApplicationInternal(project.objects, galleryExtension)
                    val appContext = JvmApplicationContext(project, appInternal)
                    appContext.configureDesktopJvmApplication(project)
                }


            }
        }
    }
}

private fun createApplicationInternal(
    objectFactory: ObjectFactory,
    galleryExtension: GalleryExtension
): GalleryJvmApplicationInternal {

    val jvmApplicationInternal = objectFactory.newInstance(GalleryJvmApplicationInternal::class.java, "gallery")

    jvmApplicationInternal.apply {
        val modulePackageName = galleryExtension.modulePackageName.get()
//        val gallery
    }

    return jvmApplicationInternal
}

internal fun JvmApplicationContext.configureDesktopJvmApplication(
    project: Project
) {
    if (app.isDefaultConfigurationEnabled) {
        configureDefaultGalleryApp()
    }

//    validatePackageVersions()
    val commonTasks = configureCommonGalleryJvmDesktopTasks()
    configurePackagingTasks(commonTasks, project)

    // I argue that compose multiplatform plugin will apply Wix
//    if (currentOS == OS.Windows) {
//        configureWix()
//    }
}

internal class CommonJvmDesktopTasks(
//    val unpackDefaultResources: TaskProvider<AbstractUnpackDefaultComposeApplicationResourcesTask>,
//    val checkRuntime: TaskProvider<AbstractCheckNativeDistributionRuntime>,
//    val suggestRuntimeModules: TaskProvider<AbstractSuggestModulesTask>,
    val prepareAppResources: TaskProvider<Sync>,
//    val createRuntimeImage: TaskProvider<AbstractJLinkTask>
)

private fun JvmApplicationContext.configureCommonGalleryJvmDesktopTasks(): CommonJvmDesktopTasks {
//    val unpackDefaultResources = tasks.register<AbstractUnpackDefaultComposeApplicationResourcesTask>(
//        taskNameAction = "unpack",
//        taskNameObject = "DefaultComposeDesktopJvmApplicationResources"
//    ) {}

//    val checkRuntime = tasks.register<AbstractCheckNativeDistributionRuntime>(
//        taskNameAction = "check",
//        taskNameObject = "runtime"
//    ) {
//        jdkHome.set(app.javaHomeProvider)
//        checkJdkVendor.set(ComposeProperties.checkJdkVendor(project.providers))
//        jdkVersionProbeJar.from(
//            project.detachedComposeGradleDependency(
//                artifactId = "gradle-plugin-internal-jdk-version-probe"
//            ).excludeTransitiveDependencies()
//        )
//    }


//    val suggestRuntimeModules = tasks.register<AbstractSuggestModulesTask>(
//        taskNameAction = "suggest",
//        taskNameObject = "runtimeModules"
//    ) {
//        dependsOn(checkRuntime)
//        javaHome.set(app.javaHomeProvider)
//        modules.set(provider { app.nativeDistributions.modules })
//
//        useAppRuntimeFiles { (jarFiles, mainJar) ->
//            files.from(jarFiles)
//            launcherMainJar.set(mainJar)
//        }
//    }

    val prepareAppResources = tasks.register<Sync>(
        taskNameAction = "prepareGallery",
        taskNameObject = "appResources"
    ) {
        val appResourcesRootDir = app.nativeDistributions.appResourcesRootDir
        if (appResourcesRootDir.isPresent) {
            from(appResourcesRootDir.dir("common"))
            from(appResourcesRootDir.dir(currentOS.id))
            from(appResourcesRootDir.dir(currentTarget.id))
        }
        into(jvmTmpDirForTask())
    }

//    val createRuntimeImage = tasks.register<AbstractJLinkTask>(
//        taskNameAction = "create",
//        taskNameObject = "runtimeImage"
//    ) {
//        dependsOn(checkRuntime)
//        javaHome.set(app.javaHomeProvider)
//        modules.set(provider { app.nativeDistributions.modules })
//        includeAllModules.set(provider { app.nativeDistributions.includeAllModules })
//        javaRuntimePropertiesFile.set(checkRuntime.flatMap { it.javaRuntimePropertiesFile })
//        destinationDir.set(appTmpDir.dir("runtime"))
//    }

    return CommonJvmDesktopTasks(
//        unpackDefaultResources,
//        checkRuntime,
//        suggestRuntimeModules,
        prepareAppResources,
//        createRuntimeImage
    )
}

private fun JvmApplicationContext.configurePackagingTasks(
    commonTasks: CommonJvmDesktopTasks,
    project: Project
) {

    val run = tasks.register<JavaExec>(taskNameAction = "desktopGalleryRun") {
        configureRunTask(this, commonTasks.prepareAppResources, project)
    }

}

private fun JvmApplicationContext.configureRunTask(
    exec: JavaExec,
    prepareAppResources: TaskProvider<Sync>,
    project: Project
) {
    exec.dependsOn(prepareAppResources)

    exec.dependsOn(project.tasks.getByName("galleryClasses"))

    exec.mainClass.set(exec.provider { "gallery/MainKt" })

    exec.executable(javaExecutable(app.javaHome))
    exec.jvmArgs = arrayListOf<String>().apply {
        addAll(defaultJvmArgs)

        if (currentOS == OS.MacOS) {
            val file = app.nativeDistributions.macOS.iconFile.ioFileOrNull
            if (file != null) add("-Xdock:icon=$file")
        }

        addAll(app.jvmArgs)

        val appResourcesDir = prepareAppResources.get().destinationDir
        add("-D$APP_RESOURCES_DIR=${appResourcesDir.absolutePath}")
    }
    exec.args = app.args

    exec.useAppRuntimeFiles { (runtimeJars, _) ->
        project.logger.error("All classpath/runtimeJars are $runtimeJars")
        classpath = runtimeJars
    }
}
