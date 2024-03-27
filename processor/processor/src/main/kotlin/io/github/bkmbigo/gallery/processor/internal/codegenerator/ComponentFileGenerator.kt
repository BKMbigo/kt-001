package io.github.bkmbigo.gallery.processor.internal.codegenerator

import com.squareup.kotlinpoet.*
import io.github.bkmbigo.gallery.ksp.symbol.KSImportDirective
import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ParamWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ScreenComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.StateComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.verifiers.matcher.ComponentMatched

private const val STATE_SUFFIX = "State"
private const val COMPONENT_SCREEN_SUFFIX = "ComponentScreen"

internal fun generateComponentScreenFunction(
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
        "onNavigateBack: () -> Unit = {},"
    }

    val statePropertyDeclarations = componentMatched.parameters.map { (param, _) ->
        with(fileImportsHandler) {
            param.generateStatePropertyDeclaration()
        }
    }.joinToString("\n") { it }

    val screenComponentCallee = with(fileImportsHandler) {
        generateGalleryScreenCallee(screenComponent, componentMatched)
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
        |   $screenParameters${onNavigateBackParameter?.let { ",\n\t$it" } ?: ""}
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


    return """
        |var $stateName by remember($paramName) { mutableStateOf($paramName) }
    """.trimMargin()
}

context(FileImportsHandler)
private fun generateGalleryScreenCallee(
    screenComponent: ScreenComponentWrapper,
    componentMatched: ComponentMatched
): String {

    addImport(screenComponent.fqName.asString())

    val screenComponentName = screenComponent.fqName.getShortName()
    val componentParameterName = screenComponent.componentParameterName
    val stateComponentsParameterName = screenComponent.stateComponentsParameterName

    val stateComponentParameters = componentMatched.parameters.map { (param, stateComponentWrapper) ->
        generateStateComponentCallee(param, stateComponentWrapper)
    }.joinToString("\n") { it }

    val onNavigateBackParameter = screenComponent.onNavigateBackParameterName?.let { onNavigateBackParameterName ->
        """|$onNavigateBackParameterName = {
            |   onNavigateBack()
            |}
        """.trimMargin()
    }

    return """|
        |$screenComponentName(
        |   $componentParameterName = {
        |       ${componentMatched.generateGalleryComponentCallee()}
        |   },
        |   $stateComponentsParameterName = {
        |       $stateComponentParameters
        |   }${onNavigateBackParameter?.let { ",\n\t$it" } ?: ""}
        |)
    """.trimMargin()
}

context(FileImportsHandler)
private fun ComponentMatched.generateGalleryComponentCallee(): String {
    val componentSimpleName = fqName.getShortName()

    val parameters = parameters.map { (param, _) ->
        generateComponentCalleeParameters(param)
    }.joinToString("\n") { it }

    return """
        |$componentSimpleName(
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
    param: ParamWrapper,
    stateComponentWrapper: StateComponentWrapper
): String {
    addImport(stateComponentWrapper.fqName.asString())

    val stateComponentSimpleName = stateComponentWrapper.fqName.getShortName()
    val stateParamName = stateComponentWrapper.stateParameterName
    val onStateParamName = stateComponentWrapper.onStateParameterName

    val stateName = "${param.name.getShortName()}State"

    return """
        |$stateComponentSimpleName(
        |   $stateParamName = $stateName,
        |   $onStateParamName = { newState ->
        |       $stateName = newState
        |   }
        |)
    """.trimMargin()
}


internal interface FileImportsHandler {

    fun addImport(importDirective: String)
    fun addImport(packageName: String, vararg names: String)

    fun addPackageImport(packageName: String)

    fun addImport(import: KSImportDirective)
    fun addImports(imports: List<KSImportDirective>)

    fun getAllImportsDeclaration(): String
}

internal class FileImportsHandlerImpl: FileImportsHandler {
    private sealed interface ImportType {
        data class Specific(val names: MutableSet<String>): ImportType
        data object Star: ImportType
    }

    private val importedPackages: MutableMap<String, ImportType> = mutableMapOf()

    override fun addImport(packageName: String, vararg names: String) {
        // No validation to ensure [names] does not contain DOT_QUALIFIED_EXPRESSION
        if (importedPackages.containsKey(packageName)) {
            when (val importType = importedPackages[packageName]!!) {
                is ImportType.Specific -> {
                    importType.names.addAll(names)
                }
                ImportType.Star -> { /* Ignore as the declaration is already imported */ }
            }
        } else {
            importedPackages[packageName] = ImportType.Specific(names.toMutableSet())
        }
    }

    override fun addImport(importDirective: String) {
        val packageName = importDirective.substringBeforeLast(".")
        val importedDeclaration = importDirective.substringAfterLast(".")
        addImport(packageName, importedDeclaration)
    }

    override fun addPackageImport(packageName: String) {
        // If previous was a Specific import type, it will be converted to a star import
        importedPackages[packageName] = ImportType.Star
    }

    override fun addImport(import: KSImportDirective) {
        if (import.isAllUnder) {
            import.getImportedReference()?.getExpressionAsString()?.let { addPackageImport(it) }
        } else {
            import.getImportedReference()?.getExpressionAsString()?.let { expression ->
                val packageName = expression.substringBeforeLast(".")
                val importedDeclaration = expression.substringAfterLast(".")
                addImport(packageName, importedDeclaration)
            }
        }
    }

    override fun addImports(imports: List<KSImportDirective>) = imports.forEach { import ->
        addImport(import)
    }

    override fun getAllImportsDeclaration(): String = buildString {
        importedPackages.forEach { (packageName, importType) ->
            when (importType) {
                ImportType.Star -> appendLine("import $packageName.*")
                is ImportType.Specific -> {
                   importType.names.forEach { name ->
                       appendLine("import $packageName.$name")
                   }
                }
            }
        }
    }
}
