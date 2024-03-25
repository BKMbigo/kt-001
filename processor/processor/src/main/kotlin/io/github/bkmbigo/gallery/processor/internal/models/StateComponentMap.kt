package io.github.bkmbigo.gallery.processor.internal.models

import io.github.bkmbigo.gallery.ksp.symbol.*
import io.github.bkmbigo.gallery.processor.internal.environment.ProcessorEnvironment
import io.github.bkmbigo.gallery.processor.internal.models.savers.StateComponentMapSaver
import io.github.bkmbigo.gallery.processor.internal.models.savers.StateComponentWrapperSaver
import io.github.bkmbigo.gallery.processor.internal.models.savers.symbols.SavedKotlinType
import io.github.bkmbigo.gallery.processor.internal.models.savers.symbols.toSavedKotlinType
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.StateComponentWrapper
import io.github.bkmbigo.gallery.processor.internal.utils.*

/*
* This is a highly inefficient map used for demo purposes only. The intended purpose of the map is to store and retrieve @GalleryStateComponent symbols associated with specific types
* */

context(ProcessorEnvironment)
internal class StateComponentMap constructor() {

    /*
    * A type can have several @GalleryStateComponent identified by an identifier
    *  */
    internal var stateComponentMap: MutableMap<SavedKotlinType, StateComponentTypeList> = mutableMapOf()
        private set


    fun addStateComponent(stateComponent: StateComponentWrapper): Boolean {
        val componentType = StateComponentType(stateComponent.type)

        return if (stateComponentMap.containsKey(componentType.toSavedKotlinType())) {
            stateComponentMap[componentType.toSavedKotlinType()]?.addStateComponent(stateComponent) ?: false
        } else {
            val newStateComponentTypeList = StateComponentTypeList(componentType.type)

            if (newStateComponentTypeList.addStateComponent(stateComponent)) {
                stateComponentMap[componentType.toSavedKotlinType()] = newStateComponentTypeList
                true
            } else {
                false
            }
        }
    }

    fun retrieveStateComponent(
        type: KSType
    ): StateComponentWrapper? {
        val stateRetrievalState = recursiveRetrieveStateComponent(
            StateRetrievalState(
                unVisitedTypes = setOf(StateComponentType(type))
            )
        )

        return if (stateRetrievalState.stateRetrieved != null) {
            /*
            * You can cache visitedTypes to reduce number of [KSType.resolve calls]
            * */
            stateRetrievalState.stateRetrieved
        } else {
            /*
            * all visitedTypes are considered unresolvable types. Therefore, they can be stored to prevent similar transversal of the tree
            * */
            logger.error("Failed to find a @GalleryStateComponent for the type ${type.declaration.qualifiedName?.asString()}")
            null
        }
    }

    private tailrec fun recursiveRetrieveStateComponent(
        pastState: StateRetrievalState
    ): StateRetrievalState {
        return if (pastState.unVisitedTypes.isNotEmpty()) {
            val currentType = pastState.unVisitedTypes.first()
            val stack = currentType.superTypes.filterNot { it.isAny || it.isUnit }.toSet() + pastState.unVisitedTypes

            val retrievedStateComponent = retrieveStateComponentForType(currentType)

            if (retrievedStateComponent != null) {
                StateRetrievalState(
                    stateRetrieved = retrievedStateComponent,
                    unVisitedTypes = stack,
                    visitedTypes = pastState.visitedTypes.toMutableList().apply {
                        add(currentType)
                    }
                )
            } else {
                if (stack.isNotEmpty()) {
                    recursiveRetrieveStateComponent(
                        pastState.copy(
                            unVisitedTypes = stack,
                            visitedTypes = pastState.visitedTypes.toMutableList().apply {
                                add(currentType)
                            }
                        )
                    )
                } else {
                    StateRetrievalState(
                        stateRetrieved = null,
                        unVisitedTypes = emptySet(),
                        visitedTypes = pastState.visitedTypes
                    )
                }
            }
        } else {
            StateRetrievalState(
                stateRetrieved = null,
                unVisitedTypes = emptySet(),
                visitedTypes = pastState.visitedTypes
            )
        }
    }

    /** Checks whether there is a @GalleryStateComponent for type. ignores supertypes */
    private fun retrieveStateComponentForType(componentType: StateComponentType, identifier: String? = null, reportErrors: Boolean = false): StateComponentWrapper? {

        // All Kotlin classes are descendants of kotlin.Any (Skips mapping for kotlin.Any)
        if (componentType.isAny || componentType.isUnit) {
            // You can argue that at this stage all errors should be reported :)
            if (reportErrors) {
                logger.error("There are no registered @GalleryStateComponent(s)")
            }
            return null
        }


        return if(stateComponentMap.containsKey(componentType.toSavedKotlinType())) {
            return stateComponentMap[componentType.toSavedKotlinType()]?.retrieveStateComponent(identifier, reportErrors)
        } else {
            if (reportErrors)
                logger.error("There are no registered @GalleryStateComponent(s) for the type ${componentType.type.declaration.qualifiedName?.asString()}")

            null
        }
    }

    /* This type is meant preserve nullability when parsing through supertypes of a current type
    *       For example, if class C:B and class B:A
    *           type C? will have supertype B? and B? will have supertype A?
    *       Also, if class C<T>:B<T> and B<T>:A<T>
    *           type C<Int>:B<Int> and B<Int>:A<Int>
    * */
    internal data class StateComponentType(
        val type: KSType,
        val isNullable: Boolean
    ) {

        /* This constructor is only used in the top-most declaration */
        constructor(type: KSType):this(type, type.isMarkedNullable)

        /*
        * This calls [KSType.resolve] making it expensive. Call only when needed
        * */
        val superTypes
            get() = when(val declaration = type.declaration) {
                is KSClassDeclaration -> {
                    // Discuss how to handle value class
                    declaration.superTypes.map { StateComponentType(it.resolve(), isNullable) }
                }
                is KSTypeAlias -> {
                    val resolvedType = declaration.type.resolve()
                    sequenceOf(StateComponentType(resolvedType, isNullable || resolvedType.isMarkedNullable))
                }
                else -> emptySequence()
            }

        val isFunctionType
            get() = type.isFunctionType

        val isSuspendFunctionType
            get() = type.isSuspendFunctionType

        context(ProcessorEnvironment)
        val isAny
            get() = type.isAny

        context(ProcessorEnvironment)
        val isUnit
            get() = type.isUnit


        fun toSavedKotlinType() = type.toSavedKotlinType()
    }

    private data class StateRetrievalState(
        val stateRetrieved: StateComponentWrapper? = null,
        val unVisitedTypes: Set<StateComponentType> = emptySet(),
        val visitedTypes: List<StateComponentType> = emptyList()
    )

}