package io.github.bkmbigo.gallery.processor.internal.models

import io.github.bkmbigo.gallery.ksp.symbol.KSType
import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.StateComponentWrapper

/*
* Stores a full list of @GalleryStateComponent(s) that can be used to implement state logic for a type
* */
context(ProcessorEnvironment)
internal class StateComponentTypeList(
    val type: KSType
) {
    /*
    * This is the default @GalleryStateComponent that is used for the [KSType]. There can only be one default per type
    * */
    private var defaultStateComponent: StateComponentWrapper? = null

    /**
     * This stores the non-default @GalleryStateComponent
     * */
    private val nonDefaultStateComponent: MutableMap<String, StateComponentWrapper> = mutableMapOf()


    constructor(
        defaultStateComponent: StateComponentWrapper
    ) : this(defaultStateComponent.type) {
        this@StateComponentTypeList.defaultStateComponent = defaultStateComponent
    }

    /**
     * Adds a @GalleryStateComponent
     *
     * @return true if successfully added
     * */
    fun addStateComponent(stateComponentWrapper: StateComponentWrapper): Boolean {
        if (stateComponentWrapper.isDefault) {
            return if (defaultStateComponent != null) {
                logger.error("The type $type already has a default @GalleryStateComponent at ${defaultStateComponent!!.fqName}")
                false
            } else {
                defaultStateComponent = stateComponentWrapper
                true
            }
        } else {
            return false
        }
    }

    /**
     * Retrieves a @GalleryStateComponent for this type
     * */
    fun retrieveStateComponent(
        identifier: String? = null,
        reportErrors: Boolean = false
    ): StateComponentWrapper? =
        if (identifier == null) {
            if (defaultStateComponent == null) {
                if (reportErrors)
                    logger.error("Cannot find a default @GalleryStateComponent for the type ${type.declaration.qualifiedName?.asString()}")
                null
            } else {
                defaultStateComponent
            }
        } else {
            if (nonDefaultStateComponent.containsKey(identifier)) {
                nonDefaultStateComponent[identifier]
            } else {
                if (reportErrors)
                    logger.error("Cannot find a non-default @GalleryStateComponent for type ${type.declaration.qualifiedName?.asString()} with identifier $identifier")
                null
            }
        }

}
