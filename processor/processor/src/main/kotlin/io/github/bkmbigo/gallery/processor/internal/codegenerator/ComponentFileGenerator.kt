package io.github.bkmbigo.gallery.processor.internal.codegenerator

import io.github.bkmbigo.gallery.processor.internal.models.ComponentRegistrar
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.PageSubstituteWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ParamWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ScreenComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.StateComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.verifiers.matcher.ComponentMatched

private const val STATE_SUFFIX = "State"
internal const val COMPONENT_SCREEN_SUFFIX = "ComponentScreen"

internal fun generateComponentScreenFunction(
    componentRegistrar: ComponentRegistrar,
    screenComponent: ScreenComponentWrapper,
    componentMatched: ComponentMatched
): String {

    val packageName = componentMatched.fqName.getQualifier()

    val fileImportsHandler = FileImportsHandlerImpl()

    fileImportsHandler.addImports(componentMatched.importList)

    val screenComponentFunctionName = "${componentMatched.fqName.getShortName()}$COMPONENT_SCREEN_SUFFIX"

    val screenParameters = componentMatched.parameters.map { (param, _) ->
        param.generateComponentScreenParameter()
    }.joinToString("\n") { it }

    // The optional `onNavigateBack` parameter
    val onNavigateBackParameter = screenComponent.onNavigateBackParameterName?.let {
        "onNavigateBack: () -> Unit = {}"
    }

    val statePropertyDeclarations = componentMatched.parameters.map { (param, _) ->
        with(fileImportsHandler) {
            param.generateStatePropertyDeclaration()
        }
    }.joinToString("\n") { it }

    val screenComponentCallee = with(fileImportsHandler) {
        generateGalleryScreenCallee(componentRegistrar, screenComponent, componentMatched)
    }

    val themeStateComponentParameter = "themeStateComponents: @Composable () -> Unit = {}"

    val hasThemeComponentsParameter = screenComponent.hasThemeComponentParameterName?.let { hasThemeComponentParameterName ->
        "$hasThemeComponentParameterName: Boolean"
    }

    with(fileImportsHandler){
        addImport("androidx.compose.runtime.Composable")
    }

    return """|
        |package $packageName
        |
        |${fileImportsHandler.getAllImportsDeclaration()}
        |
        |// This is the function that is used to display the component ${componentMatched.fqName.asString()}
        |@Composable
        |fun $screenComponentFunctionName(
        |    $screenParameters
        |    $themeStateComponentParameter${onNavigateBackParameter?.let { ",\n\t$it" } ?: ""}${hasThemeComponentsParameter?.let { ",\n\t$it" } ?: ""}
        |) {
        |
        |   // State
        |   $statePropertyDeclarations
        |   
        |   // screen
        |   $screenComponentCallee
        |   
        |}
        |
    """.trimMargin()
}

private fun ParamWrapper.generateComponentScreenParameter(): String {

    val paramName = name.getShortName()
    val expression = defaultExpression!!.getExpressionAsString()

    // We don't need to add the type as an import as packageName is preserved and imports are transferred directly
    val typeSimpleName = type.declaration.simpleName.getShortName()

    return "$paramName: $typeSimpleName = $expression,"
}



context(FileImportsHandler)
private fun ParamWrapper.generateStatePropertyDeclaration(): String {

    val paramName = name.getShortName()
    val stateName = "${paramName}$STATE_SUFFIX"

    // The following imports are needed:
    //      androidx.compose.runtime.remember
    //      androidx.compose.runtime.mutableStateOf
    //      androidx.compose.runtime.getValue
    //      androidx.compose.runtime.setValue
    addImport("androidx.compose.runtime", "remember")
    addImport("androidx.compose.runtime.mutableStateOf")
    addImport("androidx.compose.runtime.getValue")
    addImport("androidx.compose.runtime.setValue")


    return """|var $stateName by remember($paramName) { mutableStateOf($paramName) }
    """.trimMargin()
}

context(FileImportsHandler)
private fun generateGalleryScreenCallee(
    componentRegistrar: ComponentRegistrar,
    screenComponent: ScreenComponentWrapper,
    componentMatched: ComponentMatched
): String {

    addImport(screenComponent.fqName.asString())

    val screenComponentName = screenComponent.fqName.getShortName()
    val componentParameterName = screenComponent.componentParameterName
    val stateComponentsParameterName = screenComponent.stateComponentsParameterName

    val stateComponentParameters = generateStateComponentCallee(componentRegistrar, componentMatched.parameters)

    val onNavigateBackParameter = screenComponent.onNavigateBackParameterName?.let { onNavigateBackParameterName ->
        """|$onNavigateBackParameterName = {
            |   onNavigateBack()
            |}
        """.trimMargin()
    }

    val componentNameArgument = screenComponent.componentNameParameterName?.let { componentNameParameterName ->
        """|$componentNameParameterName = "${componentMatched.componentName}"
        """.trimMargin()
    }

    val themeStateArgument = screenComponent.themeStateComponentsParameterName?.let { themeStateComponentsParameterName ->
        """|$themeStateComponentsParameterName = {
            |   themeStateComponents()
            |}
        """.trimMargin()
    }

    val hasStateComponentsArgument = screenComponent.hasStateComponentsParameterName?.let { hasStateComponentsParameterName ->
        """|$hasStateComponentsParameterName = ${if(componentMatched.parameters.isEmpty()) "false" else "true"}"""
    }

    val hasThemeComponentsArgument = screenComponent.hasThemeComponentParameterName?.let { hasThemeComponentParameterName ->
        // At this stage, I cannot determine whether there are theme StateComponents. I choose to pass it as a parameter
        """|$hasThemeComponentParameterName = $hasThemeComponentParameterName"""
    }

    return """|$screenComponentName(
        |   $componentParameterName = {
        |       ${componentMatched.generateGalleryComponentCallee()}
        |   },
        |   $stateComponentsParameterName = {
        |       $stateComponentParameters
        |   }${themeStateArgument?.let { ",\n\t$it" } ?: ""}${onNavigateBackParameter?.let { ",\n\t$it" } ?: ""}${componentNameArgument?.let{ ",\n\t$it" } ?: "" }${hasStateComponentsArgument?.let{ ",\n\t$it" } ?: "" }${hasThemeComponentsArgument?.let{ ",\n\t$it" } ?: "" }
        |)
    """.trimMargin()
}

context(FileImportsHandler)
private fun ComponentMatched.generateGalleryComponentCallee(): String {
    val componentSimpleName = fqName.getShortName()

    val parameters = parameters.map { (param, _) ->
        generateComponentCalleeParameters(param)
    }.joinToString("\n") { it }

    return """|$componentSimpleName(
        |   $parameters
        |)
    """.trimMargin()
}

context(FileImportsHandler)
private fun generateComponentCalleeParameters(
    param: ParamWrapper
): String {

    val paramName = param.name.getShortName()
    val stateName = "${paramName}$STATE_SUFFIX"

    return "$paramName = $stateName,"
}

context(FileImportsHandler)
private fun generateStateComponentCallee(
    componentRegistrar: ComponentRegistrar,
    paramMap: Map<ParamWrapper, StateComponentWrapper>
): String {

    val hasComponentPages = paramMap.values.any { !it.isRow }

    return if (!hasComponentPages) {
        paramMap.map { generateStateComponentRowCallee(it.key, it.value) }.joinToString("\n") { it }
    } else {
        check(componentRegistrar.genericPageSubstitute != null) {
            "The project does not have a Page Substitute"
        }

        val paramsWithPages = paramMap.filterValues { !it.isRow }

        val pageStateDeclaration = "var currentStatePage by remember { mutableStateOf(0) }"
        val backStackDeclaration = "val pageBackStack = remember { mutableListOf<Int>() }"

        var paramsWithPagesCounter = 0
        val initialPageDeclaration = paramMap.map { (param, stateComponentWrapper) ->
            if(stateComponentWrapper.isRow) {
                generateStateComponentRowCallee(
                    param,
                    stateComponentWrapper
                )
            } else {
                paramsWithPagesCounter++
                generatePageSubstituteCallee(
                    screenDestinationNumber = paramsWithPagesCounter,
                    param = param,
                    pageSubstituteWrapper = componentRegistrar.genericPageSubstitute!!
                )
            }
        }.joinToString("\n") { it }

        // The following part heavily relies on the fact that the map passed preserves the order of keys
        paramsWithPagesCounter = 0
        val otherPages = paramsWithPages.map { (param, stateComponentWrapper) ->
            val galleryScreenCallee = generateStateComponentRowCallee(
                param = param,
                stateComponentWrapper = stateComponentWrapper
            )

            paramsWithPagesCounter++
            """|$paramsWithPagesCounter -> {
                |   $galleryScreenCallee
                |}
            """.trimMargin()
        }.joinToString("\n") { it }

        """|$pageStateDeclaration
            |$backStackDeclaration
            |
            |when (currentStatePage) {
            |   0 -> {
            |       $initialPageDeclaration
            |   }
            |   $otherPages
            |}
        """.trimMargin()
    }
}

context(FileImportsHandler)
private fun generateStateComponentRowCallee(
    param: ParamWrapper,
    stateComponentWrapper: StateComponentWrapper
): String {
    addImport(stateComponentWrapper.fqName.asString())

    val stateComponentSimpleName = stateComponentWrapper.fqName.getShortName()
    val stateParamName = stateComponentWrapper.stateParameterName
    val onStateParamName = stateComponentWrapper.onStateParameterName

    val stateName = "${param.name.getShortName()}State"

    val paramNameArgument = stateComponentWrapper.paramNameParameterName?.let { paramNameParameterName ->
        "$paramNameParameterName = \"${param.name.getShortName()}\""
    }

    val onNavigateBackArgument = if (!stateComponentWrapper.isRow && stateComponentWrapper.onNavigateBackParameterName != null) {

        """|${stateComponentWrapper.onNavigateBackParameterName} = {
            |   currentStatePage = 0
            |}
        """.trimMargin()
    } else null

    return """|$stateComponentSimpleName(
        |   $stateParamName = $stateName,
        |   $onStateParamName = { newState ->
        |       $stateName = newState
        |   }${paramNameArgument?.let { ",\n$it" } ?: ""}${onNavigateBackArgument?.let { ",\n$it" } ?: ""}
        |)
    """.trimMargin()
}

context(FileImportsHandler)
private fun generatePageSubstituteCallee(
    screenDestinationNumber: Int,
    param: ParamWrapper,
    pageSubstituteWrapper: PageSubstituteWrapper
): String {

    val fqName = pageSubstituteWrapper.fqName.asString()
    val simpleName = pageSubstituteWrapper.fqName.getShortName()

    // Arguments
    val paramNameArgument = pageSubstituteWrapper.paramNameParameterName?.let { paramNameParameterName ->
        "$paramNameParameterName = \"${param.name.getShortName()}\""
    }

    val onNavigateToScreenArgument = """|onNavigateToScreen = {
        |   currentStatePage = $screenDestinationNumber
        |}""".trimMargin()

    addImport(fqName)

    return """|$simpleName(
        |   ${paramNameArgument?.let { "$it," } ?: ""}
        |   $onNavigateToScreenArgument
        |)
    """.trimMargin()
}
