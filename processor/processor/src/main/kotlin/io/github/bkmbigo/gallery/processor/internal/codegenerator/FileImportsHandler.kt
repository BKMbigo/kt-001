package io.github.bkmbigo.gallery.processor.internal.codegenerator

import io.github.bkmbigo.gallery.ksp.symbol.KSImportDirective


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
