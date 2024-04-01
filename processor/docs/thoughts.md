# Custom Compose Gallery Component

* Project: Compose Component Gallery
* Author: Brian Mbigo
* Portfolio: [Github](https://www.github.com/BKMbigo), [LinkedIn](https://www.linkedin.com/in/BKMbigo)
* Date: 27 March 2024

The proposal below takes a somewhat speculative approach, read the technical limitations for a description on what may become a hindrance during the project duration.

This is a proposal, feel free to comment and suggest changes at the Github project site

## Table Of Contents
* [Brief Summary](#brief-summary)
* [Design proposal](#design-proposal)
* [Technical Limitations](#technical-limitations)
* [Limitations of this approach](#limitations-of-this-approach)

## Brief Summary
My proposal is to introduce a gradle-plugin with the capability of generating a gallery application in all Compose-UI multiplatform supported targets (i.e Android, Desktop, Js, WebAssembly, ios).

In compose, there is the concept of a preview, for instance, a button in Material3 is defined as:
```kotlin
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) { /* @Composable code */ }
```
Its corresponding preview would look be:
```kotlin
@Preview
@Composable
private fun ButtonPreview() {
    MaterialTheme {
        Button(
            onClick = { /*no-op*/ }
        ) {
            Text("Click Me!!")
        }
    }
}
```
The above preview is static, as the user has to explicitly set the text to be displayed, color of button, shape and other properties. My proposal would involve exposing properties in the function call, for instance:
```kotlin
@GalleryComponent
@Preview
@Composable
fun ButtonComponent(
    enabled: Boolean = true,
    text: String = "",
    color: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null
) {
    Button(
        onClick = { /*no-op*/ },
        enabled = enabled,
        color = color,
        elevation = elevation,
        border = border
    ) {
        Text(text = text)
    }
}
```
The above component will produce a Button Preview, along with "[controls](#gallerystatecomponent)" to control the properties

## Design Proposal
This part contains the design specifications for the Component Gallery
* [General Design Principles](#general-design-principles)
* [Annotations](#annotations)
  * [GalleryComponent](#gallerystatecomponentt)
  * [GalleryStateComponent](#gallerystatecomponentt)
    * [GalleryStateRow](#gallerystaterowt)
    * [GalleryStatePage](#gallerystatepaget)
  * [GalleryComponentParameter](#gallerycomponentparameter)
  * [GalleryComponentTheme](#gallerycomponenttheme)
* [Processor](#processor)
  * [Types](#types)
    * [Generic Types](#generic-types)
    * [Typealias](#typealias)
    * [Value class](#value-class)
  * [Generation of GalleryStatePage](#generation-of-gallerystatepage)
    * [Enum Classes](#enum-classes)
    * [Data Classes](#data-classes)
  * Expected Output
  * Incremental Processing
* [Gradle Plugin](#gradle-plugin)
  * [Gradle Extension](#gradle-extension) 
  * [Running the application on different platforms]()
  * [Handling multi-module projects]()
* [Component Libraries](#component-libraries)

### General Design Principles
Generally, the design of the Component Gallery is guided by the following principles:
* Easy to integrate and use for end-users
* Clear APIs
* Design-agnostic (Not tied to a specific design system)
* Easily extensible

### Annotations
In general, there will be two broad types of annotations: Client and Design Annotations. Design annotations will be added later and are intended to be used in creating component libraries (To be discussed later).

#### @GalleryComponent
This is the annotation used to annotate a Component to be added to the gallery. It has the following guidelines:
* ###### Can only be applied to `@Composable` functions
    Components are required to be `@Composable` functions
* ###### Cannot be applied to private functions
    Private functions cannot be called from a different file
* ###### Cannot have functional parameters
    We cannot provide bindings for functional parameters. Also, what UI component would be used for a function?
* ###### Cannot have parameters with the types: Any, Any?, Unit, Nothing
    This is because it is difficult to correctly update a state with the above types
* ###### All parameters are required to have default expressions
    The default expressions are considered as the default value for state produced in the `GalleryComponent`

A correct annotation would look like:
```kotlin
/**
 * These is a Material3 button element
 * */
@GalleryComponent
@Composable
internal fun ButtonComponent(
    enabled: Boolean = true,
    text: String = "",
    color: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null
) {
    Button(
        onClick = { /*no-op*/ },
        enabled = enabled,
        color = color,
        elevation = elevation,
        border = border
    ) {
        Text(text = text)
    }
}
```

#### @GalleryStateComponent<T>
These are the 'controls' used to update state declared in `@GalleryComponent`. In these proposal, there are divided into two types [@GalleryStateRow](#gallerystaterowt) and [@GalleryStatePage](#gallerystatepaget).

```kotlin
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class GalleryStateComponent<T>(
    val identifier: String = ""
)
```
The annotation has a type argument `T` which denotes the type the `@GalleryStateComponent` is bound to. The parameter `identifier` is used to distinguish the `@GalleryStateComponent` when there are several `@GalleryStateComponent` for a specific type. Leaving the parameter empty, denotes that the parameter is the default for the type `T`

These annotations have the following guidelines:
* ###### Can only be applied to `@Composable` functions
    State Component functions have to be @Composable functions
* ###### Cannot be applied to private functions
    Private functions cannot be called from a different file
* ###### Type parameters cannot be private
    Private types cannot be accessed from a different file
* ###### Must have a single state parameter
    A state parameter is a parameter that takes the type T defined in the annotation. This is the parameter that the [@GalleryComponent](#gallerycomponent) will pass the current state. If there are more than one parameter taking T, then the processor cannot determine what parameter to pass state to
* ###### Must have a single onState parameter
    An onState parameter is a parameter that takes (T) -> Unit, where T is the type defined in the annotation. This is the callback that is called when state is changed.
* ###### All remaining parameters are required to have default expressions
    This is to enable the processor to call the function without any further arguments

A user can add an optional parameter `paramName` which passes the parameter name specified in [@GalleryComponent](#gallerycomponent)

A correctly annotated function would look like:
```kotlin
/**
 * This function will be used to change any @GalleryComponent Int parameter
 * */
@GalleryStateComponent<Int>
@Composable
fun IntStateComponent(
    state: Int,
    onState: (Int) -> Unit,
    paramName: String,
    modifier: Modifier = Modifier // Note that the processor cannot pass a custom Modifier. It will leave the argument empty
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = paramName
        )
      
        OutlinedTextField(
            state = TextFieldValue(state),
            onState = { textFieldValue ->
                onState(textFieldValue.text.toInt()) // This is just an example
            },
            label = paramName
        )
    }
}
```

##### @GalleryStateRow<T>
Unlike a [@GalleryStatePage](#gallerystatepaget), several rows are stacked on a column. These makes [@GalleryStateComponent](#gallerystatecomponentt)(s) marked with these annotations, to have limited vertical space.

##### @GalleryStatePage<T>
This annotation is used by [@GalleryStateComponent](#gallerystatecomponentt)(s) that require greater vertical space, for example, a color wheel, or an enum class.

#### @GalleryComponentParameter
This annotation is used by users to provide additional context on the `@GalleryStateComponent` to be used for a parameter. For example, a user might want a multi-line text field for a parameter, in this case, the user will pass an identifier to specify the particular `@GalleryStateComponent` they would like to use.

This annotation can only be applied to `@GalleryComponent` function parameters. The processor does not search for symbols with these annotations, but rather, uses it to gain more information when processing a `@GalleryComponent` symbol.

An example with the annotation would be:
```kotlin
@GalleryComponent
@Composable
fun JobDescriptionComponent(
    organizationName: String,
    jobDescription: @GalleryComponentParameter(identifier = "multi-line", paramName = "Job Description") String // Will use a different @GalleryStateComponent
) {
   JobDescription(
       organizationName,
       jobDescription
   ) 
}
```

#### @GalleryComponentTheme
This annotation is used to denote a 'theme'(a CompositionLocalProvider) for all components. An example is the `MaterialTheme` used in compose-material3 projects.

The annotation has the following guidelines:
* ###### Can only be applied to `@Composable` functions
  ComponentTheme are required to be `@Composable` functions
* ###### Cannot be applied to private functions
  Private functions cannot be called from a different file
* ###### Must have one `@Composable` functional parameter(`content`)
  This functional parameter (referred to as `content` parameter), is used to call other `@Composable` functions. The parameter has to have the type (() -> Unit)
* ###### Cannot have parameters with the types: Any, Any?, Unit, Nothing
  This is because it is difficult to correctly update a state with the above types
* ###### All parameters are required to have default expressions (except for the `content` parameter)
  The default expressions are considered as the default value for state passed to CompositionLocals

An example with the annotation would be:
```kotlin
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

@GalleryComponentTheme
@Composable
fun MaterialThemeComponent(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    lightColors: ColorScheme = lightColorScheme(),
    darkColors: ColorScheme = darkColorScheme(),
    content: @Composable () -> Unit
) {
    
    val colorScheme = if (isDarkTheme) {
        darkColors
    } else {
        lightColors
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    ) 
}
```

This annotation is very similar to `@GalleryComponent`

### Processor
The processor has the following roles:
  * Register new `@GalleryStateComponent` symbols
  * Register new `@GalleryComponent` and `@GalleryComponentTheme` symbols
    * Find and associate parameters with the corresponding `@GalleryStateComponent`
    * Generate `@GalleryStatePage` for specified types
  * Generate Component Screens
  * Generate the navigation for the application
    * Generate android manifest

#### Types
> This part is fully suggestive and is based on my limited knowledge

##### Nullability and SuperTypes
Nullable types are considered different from their non-null counterparts.  
Supertypes are resolved on a depth-first manner
```kotlin
interface A
interface B:A
interface C:B
interface Z
interface Y:Z
class D: C,Y

// When searching for `@GalleryStateComponent` for type C, the processor will search for C --> B --> A
// When searching for `@GalleryStateComponent` for type C?, the processor will search for C? --> B? --> A?
// When searching for `@GalleryStateComponent` for type D, the processor will search for D --> C --> B --> A --> Y --> Z
// When searching for `@GalleryStateComponent` for type D?, the processor will search for D? --> C? --> B? --> A? --> Y? --> Z?
```

##### Generic Types
When finding `@GalleryStateComponent`(s), the processor only considers the fully qualified name and nullability of the type. Type arguments are processed at a later stage. This is because a type reference can have multiple 

This would translate to the following resolution of types
```kotlin
interface A<T>
interface B<T>: A<T>
class C<T>: B<T>

// When searching for `@GalleryStateComponent` for type C<Int>, the processor will search for C --> B --> A
```

##### Typealias
Typealias will act as an intermediate type and thus can be used to resolve different `@GalleryStateComponent`(s) for a certain type

```kotlin
typealias Age = Int

// When searching for `@GalleryStateComponent` for type Age, the processor will at first search for Age before proceeding to Int

// TODO: typealias with generic types
```

##### Value class
I propose value class to also act as intermediate types, similar to typealias

```kotlin
@JvmInline
value class Age(val age: Int)

// When searching for `@GalleryStateComponent` for type Age, the processor will first search for components linked to the  value class Age before proceeding to Int
```

#### Generation of `@GalleryStatePage`
There are specific scenarios where the processor can automatically generate a `@GalleryStatePage` (This is where design annotations will help...)

##### Enum Classes
I consider enum classes as important to the project. This is because, a user might use an enum class to simplify complex state, for instance:
```kotlin
data class Person(
    val age: Int,
    val name: String,
    val department: String,
    val isFemale: Boolean,
    val phoneNumber: String,
    val emailAddress: String
)

@Composable
fun EmployeeDetails(
    person: Person
) {  }

// An enum class with fake persons. This enum will allow the user to quickly switch between the two persons without the need to enter all details
enum class FakePerson(val person: Person) {
    Mary(Person(age = 24, name = "Mary", department = "HR", isFemale = true, phoneNumber = "+25482323232", emailAddress = "x@company.com")),
    John(Person(age = 34, name = "John", department = "Finance", isFemale = false, phoneNumber = "+25482323232", emailAddress = "y@company.com")),
}

// The user can create a Component as follows:
@GalleryComponent
@Composable
fun EmployeeDetailsComponent(
    person: FakePerson = FakePerson.John
) {
    EmployeeDetails(
        person = person.person  // Pass the person specified in FakePerson
    )
}
```

##### Data Classes
> I consider this a little unclear/confusing. This is because it may be unclear what parameters have corresponding `@GalleryStateComponent`(s)

When enabled, the processor will check property declarations (val/var) in a data class and generate a page for the data class. However for this to occur, all property types are required to have resolvable `@GalleryStateComponent`(s)

For instance:
```kotlin
data class Person(
    val age: Int,
    val name: String
)

// The user will not need to define a '@GalleryStateComponent' for the type Person
@GalleryComponent
@Composable
fun PersonComponent(
    person: Person = Person(age = 24, name = "Micheal")
) { /* Composable code */ }
```

### Gradle Plugin
#### Gradle Extension
Options the user can set in gradle files. Will be applied when more rigid knowledge is obtained.
```kotlin
gallery {
    android {
        enabled = true
      
        processorOptions {
            // Options tied to the processor
        }
    }
    desktop {
        enabled = true

        processorOptions {
          // Options tied to the processor
        }
    }
    js {
        enabled = true

        processorOptions {
          // Options tied to the processor
        }
    }
    wasmJs {
        enabled = true

        processorOptions {
          // Options tied to the processor
        }
    }
}
```

### Component Libraries
These are libraries that will contain default `@GalleryStateComponent`(s) for a specific design system

<details>
  <summary>All Component Libraries are suggested to have `@GalleryStateComponent`(s) for the following types</summary>
<h6>Primitive Types</h6>
<ul>
  <li>Short</li>
  <li>Short?</li>
  <li>Int</li>
  <li>Int?</li>
  <li>Float</li>
  <li>Float?</li>
  <li>Long</li>
  <li>Long?</li>
  <li>Double</li>
  <li>Double?</li>
  <li>Char</li>
  <li>Char?</li>
  <li>String</li>
  <li>String?</li>
  <li>Boolean</li>
  <li>Boolean?</li>
</ul>

</details>

#### Material Component Library
The library will contain two material-based component libraries (one for Material2 and one for Material3).


<details>
  <summary>These libraries will contain <code>@GalleryStateComponent</code>(s) for the following types:</summary>
<h6>Compose UI Types</h6>
<ul>
  <li>androidx.compose.ui.graphics.Color</li>
  <li>androidx.compose.ui.graphics.Shape</li>
  <li>androidx.compose.ui.graphics.Brush</li> // This will not be straightforward
  <li>androidx.compose.ui.unit.Dp</li>
  <li>androidx.compose.ui.unit.Sp</li>
</ul>
<h6>Compose Material Types</h6>
<ul>
  <li>androidx.compose.material.ColorScheme</li>
  <li>androidx.compose.material.Typography</li>
  <li>androidx.compose.material.</li>
</ul>
<h6>Compose Material3 Types</h6>
<ul>
  <li>androidx.compose.material3.ColorScheme</li>
  <li>androidx.compose.material3.Typography</li>
</ul>

</details>

## Technical Limitations
For this approach to work, it is vital for the processor to access the default expression passed to the parameter. [Kotlin Symbol Processing](https://kotlinlang.org/docs/ksp-quickstart.html) does not support getting the default expression ([KSP can only check if there is a default value](https://github.com/google/ksp/blob/3993454a425c2d27c7a9ea019c91c78d8f008a36/api/src/main/kotlin/com/google/devtools/ksp/symbol/KSValueParameter.kt#L61)). With KSP1 (KSP version based on Kotlin K1 compiler), the processor can be modified to get the default expression. With KSP2 (K2-based KSP), it is harder as the symbol-processor is based on [Kotlin Analysis Api](), which also does not expose the default expression.

## Limitations of this approach
