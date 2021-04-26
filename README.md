# Kotlin-Robot - DSL based kotlin code generation
Kotlin-Robot helps you with generating new Kotlin files programmatically, for example in annotation processors.

## Using Kotlin-Robot
### Gradle
```groovy
dependencies {
    implementation 'com.github.nkb03:kotlin-robot:1.0'
}
```
## Features
### Idiomatic
Because of its fluent DSL, code generation can look similar to regular kotlin code. 
```kotlin
kotlinFile(pkg = "org.sample") {
    comment("@author Nikolaus Knop")
    addVar("magic", "Int".t, { private() }) {
        initializeWith(int(3))
        get {
            assign("field", field + "1".e)
            addReturn(field)
        }
        set {
            doThrow("AssertionError"())
        }
    }
    addFunction("f", { private(); inline() }, receiver = "kotlin.Int".t) {
        addWhen(`this`) {
            int(0) then "println"("Boom".q)
            containedIn(int(1)..int(4)) then "println"("Bang".q)
            otherwise then "println"("Nope".q)
        }
    }
}.writeTo(Paths.get("example.kt"))
```
This writes the following Kotlin code to `example.kt`

```kotlin
package org.sample

//@author Nikolaus Knop
private var magic: Int = 3
    get() {
        field += 1
        return field
    }
    set(value) {
        throw AssertionError()
    }
private inline fun kotlin.Int.f(): kotlin.Unit {
    when (this) {
        0 -> println("Boom")
        in 1..4 -> println("Bang")
        else -> println("Nope")
    }
}
```

### Flexible
Nearly all language features including comments are supported by the DSL, 
but sometimes it's easier to write raw text circumventing the DSL. Kotlin robot makes this as easy as possible.

```kotlin
kotlinFile("sample.test") {
  comment("//Raw text")
  raw {
    incIndent()
    writeln("SYNTAX_ERROR")
    decIndent()
  }
}
```
### Fast
Code is generated entirely on the fly, without storing an AST. This means the DSL directly translates to code
only operating on output streams. Wherever possible functions with function parameters are marked inline, which makes code generation
with Kotlin Robot even faster. 
## Disclaimer
Because code is written to the output stream directly all possibly complex operations inside the DSL are reevaluated every time you call
`writeTo` on a `KotlinFile`. Therefore, you should always write the code to an intermediate buffer, before writing the same `KotlinFile`
to multiple different OS-level files.
```kotlin
val f = kotlinFile("do.not.do.this") {
  repeat(1_000_000) {
    //Complex operation
  }
}
repeat(1_000_000) { i -> 
  f.writeTo("file$i.kt") //Every single time reevaluates the lambda passed to kotlinFile
}
```
A better approach would be:
```
val f = kotlinFile("do.not.do.this") {
  repeat(1_000_000) {
    //Complex operation
  }
}
val b = StringBuilder()
f.writeTo(b)
repeat(1_000_000) { i -> 
  val w = Files.newBufferedWriter(Paths.get("file$i.kt"))
  w.append(b)
}
```
## Known Weaknesses
- Bad test coverage  
  There is only one Unit Test located in `src/test/kotlin/krobot/api/Spec.kt` that tests basic usage of most features.
  Eventual contributors are encouraged to write some more tests.
  Reporting bugs also helps very much.


## Contributing
To contribute to Kotlin Robot you are advised to use IntelliJ.
Simply clone the project and open it in the IDE. To run the tests you will need the JUnit plugin.

## Author
Nikolaus Knop (niko.knop003@gmail.com)

## License
See [LICENSE.MD](LICENSE.md)
