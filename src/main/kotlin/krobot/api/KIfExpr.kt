/**
 *@author Nikolaus Knop
 */

package krobot.api

import krobot.impl.KFile

@KRobotDsl
class KIfExpr @PublishedApi internal constructor(
    private val cond: KExpr,
    private val then: KBlockRobot.() -> Unit,
    private val extra: KFile.() -> Unit = {}
) : KExpr() {
    override fun writeTo(out: KFile) {
        with(out) {
            extra()
            write("if (")
            cond.writeTo(this)
            write(") {")
            incIndent()
            KBlockRobot(this).then()
            decIndent()
            ensureNewline()
            writeln("}")
        }
    }

    private var used = false

    infix fun `else`(block: KBlockRobot.() -> Unit): KExpr {
        if (used) throw IllegalStateException()
        used = true
        return expr {
            this@KIfExpr.writeTo(this)
            writeln("else {")
            incIndent()
            KBlockRobot(this).block()
            decIndent()
            ensureNewline()
            writeln("}")
        }
    }

    fun `else`(then: KExpr) = `else` { evaluate(then) }

    fun elseIf(cond: KExpr, block: KBlockRobot.() -> Unit): KIfExpr {
        if (used) throw IllegalStateException()
        used = true
        return KIfExpr(cond, block) {
            this@KIfExpr.writeTo(this)
            write("else ")
        }
    }

    fun elseIf(cond: KExpr, then: KExpr) = elseIf(cond) { evaluate(then) }
}