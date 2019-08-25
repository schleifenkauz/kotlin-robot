/**
 *@author Nikolaus Knop
 */

package krobot.api

import krobot.impl.KFile

@KRobotDsl
class KPropertyRobot @PublishedApi internal constructor(private val file: KFile, private val mutable: Boolean) {
    private var initialized = false
    private var delegated = false
    private var getter = false
    private var setter = false

    infix fun initializeWith(expr: KExpr) {
        check(!(initialized || delegated || getter || setter))
        file.write(" = ")
        expr.writeTo(file)
    }

    infix fun by(delegate: KExpr) {
        check(!(initialized || delegated || getter || setter))
        file.write(" by ")
        delegate.writeTo(file)
    }

    var get: KExpr
        get() = throw UnsupportedOperationException()
        set(value) {
            check(!(delegated || getter))
            file.ensureNewline()
            file.incIndent()
            file.write("get() = ")
            value.writeTo(file)
            file.decIndent()
        }

    fun get(body: KPropertyBlockRobot.() -> Unit) {
        check(!(delegated || getter))
        file.ensureNewline()
        file.incIndent()
        file.writeln("get() {")
        file.incIndent()
        KPropertyBlockRobot(file).body()
        file.ensureNewline()
        file.decIndent()
        file.writeln("}")
        file.decIndent()
    }

    fun set(valueParameterName: String = "value", block: KPropertyBlockRobot.() -> Unit) {
        check(mutable) { "Cannot create setter for mutable variable" }
        check(!(delegated || setter))
        file.ensureNewline()
        file.incIndent()
        file.writeln("set($valueParameterName) {")
        file.incIndent()
        KPropertyBlockRobot(file).block()
        file.decIndent()
        file.ensureNewline()
        file.writeln("}")
        file.decIndent()
    }
}