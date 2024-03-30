package io.github.bkmbigo.gallery.gradle.internal.desktop

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.jetbrains.compose.desktop.application.dsl.JvmApplicationBuildType

internal const val TASK_GROUP = "gallery"

internal class JvmTasks(
    private val project: Project,
    private val taskGroup: String = TASK_GROUP
) {

    /**
     * Registers new Compose/Desktop tasks.
     * Naming pattern for tasks is: [taskNameAction][taskNameClassifier][taskNameObject]
     * Where:
     *   [taskNameAction] -- name for a task's action (e.g. 'run' or 'package')
     *   taskNameDisambiguationClassifier -- optional name for an disambiguation classifier (e.g. 'release')
     *   [taskNameObject] -- name for an object of action (e.g. 'distributable' or 'dmg')
     * Examples: 'runDistributable', 'runReleaseDistributable', 'packageDmg', 'packageReleaseDmg'
     */
    inline fun <reified T: Task> register(
        taskNameAction: String,
        taskNameObject: String = "",
        args: List<Any> = emptyList(),
        noinline configureFn: T.() -> Unit = {}
    ): TaskProvider<T> {
        val buildTypeClassifier = "Gallery"
        val objectClassifier = taskNameObject.uppercaseFirstChar()
        val taskName = "$taskNameAction$buildTypeClassifier$objectClassifier"
        return register(taskName, klass = T::class.java, args = args, configureFn = configureFn)
    }

    fun <T: Task> register(
        name: String,
        klass: Class<T>,
        args: List<Any>,
        configureFn: T.() -> Unit
    ): TaskProvider<T> =
        project.tasks.register(name, klass, *args.toTypedArray()).apply {
            configure {
                group = taskGroup
                configureFn()
            }
        }

    fun getTaskByName(
        name: String
    ): Task = project.tasks.getByName(name)

}
