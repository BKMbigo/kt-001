package io.github.bkmbigo.gallery.processor.internal.codegenerator

import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment
import io.github.bkmbigo.gallery.processor.internal.models.ComponentRegistrar
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ComponentSelectionScreenWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ParamWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ScreenComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.StateComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.verifiers.matcher.ComponentMatched

private const val THEME_COMPONENT_FUNCTION_NAME = "State"

/**
 * For now... I will not be using a navigation library
 *
 * Instead, I will use a `when` expression to encompass state
 * */

const val GALLERY_PACKAGE = "gallery"

context(ProcessorEnvironment)
internal class NavigationFileGenerator(
    private val componentRegistrar: ComponentRegistrar
) {

    fun generateMainFile(): String {

        val fileImportsHandler = FileImportsHandlerImpl()

        with(fileImportsHandler) {

            addImport("androidx.compose.ui.window.Window")
            addImport("androidx.compose.ui.window.application")

            return """|
            |package $GALLERY_PACKAGE
            |
            |${fileImportsHandler.getAllImportsDeclaration()}
            |
            |fun main() = application {
            |   Window(
            |       onCloseRequest = ::exitApplication,
            |       title = "Components"
            |   ) {
            |       MainNavigationComponent()
            |   }
            |}
            |
        """.trimMargin()
        }
    }

    fun generateNavigationFile(): String {

        val fileImportsHandler = FileImportsHandlerImpl()

        with (fileImportsHandler) {
            val destinationsEnumClass = generateDestinationsEnumClass()

            val mainComponent = generateMainNavigationComponent()


            return """|
            |package $GALLERY_PACKAGE
            |
            |${fileImportsHandler.getAllImportsDeclaration()}
            |
            |// All @GalleryComponents
            |$destinationsEnumClass
            |
            |// MainNavigationComponent
            |$mainComponent
            |
        """.trimMargin()
        }
    }

    /**
     * Generates an enum class of `All` @GalleryComponents
     * */
    context(FileImportsHandler)
    private fun generateDestinationsEnumClass(): String {

        val destinations = componentRegistrar.components.toList().joinToString(",\t\n") {
            // Should it be capitalized
            val name = it.fqName.getShortName()

            """|$name
            """.trimMargin()
        }

        val componentName = componentRegistrar.components.toList().joinToString("\n") {
            val name = it.fqName.getShortName()
            val componentName = it.componentName

            """|$name -> "$componentName"
            """.trimMargin()
        }

        addImport("io.github.bkmbigo.gallery.AbstractGalleryComponent")

        return """|
            |private enum class GalleryDestination: AbstractGalleryComponent {
            |   $destinations;
            |   
            |   override val componentName: String 
            |       get() = when(this) {
            |           $componentName
            |       }
            |}
        """.trimMargin()
    }

    /**
     * Generates the ThemeComponent function declaration
     * */
    context(FileImportsHandler)
    private fun generateThemeComponentFunction(
        themeComponentParameters: Map<ParamWrapper, StateComponentWrapper>
    ): String {

        addImport("androidx.compose.runtime.Composable")

        val parameters = generateThemeComponentFunctionDeclarationParameters(themeComponentParameters.keys)

        val body = generateThemeComponentFunctionDeclarationBody(themeComponentParameters)

        return """|
            |@Composable
            |fun $THEME_COMPONENT_FUNCTION_NAME(
            |   $parameters
            |) {
            |   $body
            |}
        """.trimMargin()
    }

    context(FileImportsHandler)
    private fun generateMainNavigationComponent(): String {
        check(componentRegistrar.screen != null) {
            "The project must have a screen to continue!!"
        }

        check(componentRegistrar.hasScreenComponentSelectionScreen) {
            "The project does not have a Component Selection Screen!!"
        }

        val whenEntries = componentRegistrar.components.toList().joinToString("\n") { component ->
            val componentSimpleName = component.fqName.getShortName()
            val componentScreenCallee = generateGalleryComponentScreenCallee(
                screen = componentRegistrar.screen!!,
                component = component
            )

            """|
                |$componentSimpleName -> {
                |   $componentScreenCallee
                |}
            """.trimMargin()
        }

        // Generate ComponentChooser
        val componentChooser = generateComponentSelectionScreenCallee(componentRegistrar.screenComponentSelectionScreen!!)

        // import
        addImport("androidx.compose.runtime.Composable")
        addImport("androidx.compose.runtime.remember")
        addImport("androidx.compose.runtime.mutableStateOf")
        addImport("androidx.compose.runtime.getValue")
        addImport("androidx.compose.runtime.setValue")


        return """|
            |
            |@Composable
            |fun MainNavigationComponent() {
            |   
            |   // state
            |   var navigationState by remember { mutableStateOf<GalleryDestination?>(null) }
            |   
            |   // components
            |   when (navigationState) {
            |       null -> {
            |           $componentChooser
            |       }
            |       $whenEntries
            |   }
            |}
        """.trimMargin()
    }

    context(FileImportsHandler)
    private fun generateComponentSelectionScreenCallee(
        componentSelectionScreen: ComponentSelectionScreenWrapper
    ): String {
        val componentSimpleName = componentSelectionScreen.fqName.getShortName()

        val enumListCallee = "val componentDestinations = GalleryDestination.entries.toList()"

        val listArgument = when(componentSelectionScreen.listParamIsPersistentList) {
            ComponentSelectionScreenWrapper.ListParamType.Set -> {
                "${componentSelectionScreen.listParam} = componentDestinations.toSet()"
            }
            ComponentSelectionScreenWrapper.ListParamType.List -> {
                "${componentSelectionScreen.listParam} = componentDestinations"
            }
            ComponentSelectionScreenWrapper.ListParamType.PersistentList -> {
                addImport("kotlinx.collections.immutable.toPersistentList")
                "${componentSelectionScreen.listParam} = componentDestinations.toPersistentList()"
            }
        }

        val onSelectionArgument = """|${componentSelectionScreen.onSelectionParamName} = { selectedComponent ->
            |   navigationState = selectedComponent
            |}
            |""".trimMargin()

        // import the function
        addImport(componentSelectionScreen.fqName.asString())

        return """|
            |$enumListCallee
            |
            |$componentSimpleName(
            |   $listArgument,
            |   $onSelectionArgument
            |)
        """.trimMargin()
    }

    context(FileImportsHandler)
    private fun generateGalleryComponentScreenCallee(
        screen: ScreenComponentWrapper,
        component: ComponentMatched
    ): String {

        val screenFqName = "${component.fqName.asString()}$COMPONENT_SCREEN_SUFFIX"
        val screenName = "${component.fqName.getShortName()}$COMPONENT_SCREEN_SUFFIX"

        val hasOnNavigateBackParameter = if(screen.onNavigateBackParameterName != null) {
            """|onNavigateBack = {
                |   navigationState = null
                |},
            """.trimMargin()
        } else ""

        addImport(screenFqName) // imports the screen component

        return """|
            |$screenName(
            |   $hasOnNavigateBackParameter
            |)
        """.trimMargin()
    }

    /*
    * Generates the parameters for the ThemeComponentsFunction
    * */
    context(FileImportsHandler)
    private fun generateThemeComponentFunctionDeclarationParameters(
        themeComponentParameters: Set<ParamWrapper>
    ): String = themeComponentParameters.joinToString("\n") { param ->

        val paramName = param.name.getShortName()
        val paramTypeName = param.type.declaration.simpleName.getShortName()

        // Add Import for type
        param.type.declaration.qualifiedName?.asString()?.let { addImport(it) }

        """|$paramName: $paramTypeName,
                |on$paramName: ($paramTypeName) -> Unit,
            """.trimMargin()
    }

    /**
     * Generate the body of the themeComponentFunction
     * */
    context(FileImportsHandler)
    private fun generateThemeComponentFunctionDeclarationBody(
        themeComponentParameters: Map<ParamWrapper, StateComponentWrapper>
    ): String {

        val stateWrapperCallees = themeComponentParameters.map { (_, stateComponent) ->
            generateStateWrapperCallee(stateComponent)
        }.joinToString("/n")

        return stateWrapperCallees
    }

    /**
     * Generates the function call to render a @GalleryStateComponent
     * */
    context(FileImportsHandler)
    private fun generateStateWrapperCallee(
        stateComponentWrapper: StateComponentWrapper
    ): String {

        // Import the function
        addImport(stateComponentWrapper.fqName.asString())

        val functionName = stateComponentWrapper.fqName.getShortName()
        val stateParamName = stateComponentWrapper.stateParameterName
        val onStateParamName = stateComponentWrapper.onStateParameterName

        return """|
            |$functionName(
            |   $stateParamName = $stateParamName,
            |   $onStateParamName = $onStateParamName
            |)
        """.trimMargin()
    }
}

