/**
 *@author Nikolaus Knop
 */

package krobot.api

import krobot.impl.KFile

@KRobotDsl
class KWhenExpr internal constructor(private val build: KWhenExpr.() -> Unit): KExpr() {
    private lateinit var file: KFile

    override fun writeTo(out: KFile) {
        file = out
        out.writeln("when {")
        out.incIndent()
        build()
        out.decIndent()
        out.writeln("}")
    }

    fun case(cond: KExpr, block: KBlockRobot.() -> Unit) {
        cond.writeTo(file)
        file.writeln(" -> {")
        file.incIndent()
        KBlockRobot(file).block()
        file.decIndent()
        file.writeln("}")
    }

    fun case(cond: KExpr, then: KExpr) = case(cond) { evaluate(then) }

    fun otherwise(block: KBlockRobot.() -> Unit) = case(expr("else"), block)

    fun otherwise(then: KExpr) = otherwise { evaluate(then) }
}