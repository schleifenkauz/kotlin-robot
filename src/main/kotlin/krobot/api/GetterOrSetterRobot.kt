/**
 *@author Nikolaus Knop
 */

package krobot.api

import krobot.impl.KFile

@KRobotDsl
class GetterOrSetterRobot @PublishedApi internal constructor(private val file: KFile) {
    var get: KExpr
        get() = throw UnsupportedOperationException()
        set(value) {
            file.write("get() = ")
            value.writeTo(file)
        }

    fun get(body: KPropertyBlockRobot.() -> Unit) {
        file.ensureNewline()
        file.writeln("get() {")
        file.incIndent()
        KPropertyBlockRobot(file).body()
        file.ensureNewline()
        file.decIndent()
        file.writeln("}")
    }

    fun set(valueParameterName: String = "value", block: KPropertyBlockRobot.() -> Unit) {
        file.ensureNewline()
        file.writeln("set($valueParameterName) {")
        file.incIndent()
        KPropertyBlockRobot(file).block()
        file.decIndent()
        file.ensureNewline()
        file.writeln("}")
    }
}