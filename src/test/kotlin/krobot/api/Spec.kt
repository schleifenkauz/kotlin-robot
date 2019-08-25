/**
 *@author Nikolaus Knop
 */

package krobot.api

import org.junit.jupiter.api.Test

internal class Spec {
    @Test fun `KRobot should generate the right code`() {
        testData1.writeTo(System.out)
    }

    private companion object {
        val testData1 = kotlinFile(
            "com.sample",
            {
                comment("these are the commons")
                import("com.sample.commons")
            }
        ) {
            comment("Now the class begins")
            addClass(
                "MySampleClass",
                { public() },
                { outvariant("T") },
                { `val`("value").of(type("kotlin.Boolean"), defaultValue = `true`) { private() } },
                {
                    implement(type("List").parameterizedBy {
                        covariant(type("T"))
                    }, delegate = call("emptyList"))
                }
            ) {
                addVar("myVar", type("kotlin.Int")) {
                    public()
                }.initializeWith(int(123)) {
                    get {
                        addReturn(field)
                    }
                    set("value") {
                        evaluate(call("println", stringLiteral("myVar has a new value")))
                        assign("field", getVar("value"))
                    }
                }
                init {
                    evaluate(call("println", stringLiteral("Hello I'm a program written by KRobot")))
                }
                addFunction(
                    name = "myFunction",
                    modifiers = {
                        annotation("JvmName", stringLiteral("myVar2"))
                        private()
                    },
                    parameters = { "x" of type("kotlin.Int") },
                    returnType = type("kotlin.Int"),
                    body = {
                        addVar("y", type("kotlin.Int")) initializedWith getVar("x")
                        assign("y") { getVar("x") + int(1) }
                        addVal("l") by call("lazy", lambda(body = getVar("x") * int(2)))
                        assign("y", int(2))
                        addReturn(getVar("x"))
                    }
                )
                addCompanion(
                    { private() },
                    { implement(
                            type("List").parameterizedBy { covariant(type("kotlin.Int")) },
                            delegate = call("emptyList")
                        )
                    }
                ) {
                    addVal("SIZE") {
                        private()
                        const()
                    } initializeWith int(0)
                }
            }
            addEnumClass(
                "E",
                { public() },
                {
                    implement(type("java.io.Serializable"))
                },
                entries = {
                    add("A")
                    add("B")
                    add("C")
                }
            ) {
                addFunction(
                    "toString",
                    modifiers = { override() },
                    returnType = type("String")
                ) {
                    evaluate(`if`(getVar("this") equalTo (getVar("E") select getVar("A"))) {
                        addReturn(stringLiteral("a"))
                    }.elseIf(getVar("this").equalTo(getVar("E").select(getVar("B")))) {
                            addReturn(stringLiteral("b"))
                    }.elseIf(getVar("this") equalTo (getVar("E") select getVar("C"))) {
                        addReturn(stringLiteral("c"))
                    })
                    doThrow(call("AssertionError"))
                }
            }
        }

        val TEST_DATA = listOf(
            testData1
        )
    }
}