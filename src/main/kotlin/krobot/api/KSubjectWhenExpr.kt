/**
 *@author Nikolaus Knop
 */

package krobot.api

import krobot.api.KSubjectWhenExpr.Case.*
import krobot.impl.KFile
import kotlin.reflect.KClass

@KRobotDsl
class KSubjectWhenExpr internal constructor(
    private val subject: KExpr,
    private val cases: KSubjectWhenExpr.() -> Unit
) : KExpr() {
    lateinit var file: KFile; private set

    override fun writeTo(out: KFile) {
        file = out
        file.write("when (")
        subject.writeTo(file)
        file.writeln(") {")
        file.incIndent()
        cases()
        file.decIndent()
        file.writeln("}")
    }

    inline fun case(case: Case, block: KBlockRobot.() -> Unit) {
        file.write(case.toString())
        file.writeln(" -> { ")
        file.incIndent()
        KBlockRobot(file).block()
        file.decIndent()
        file.writeln("}")
    }

    inline fun case(expr: KExpr, block: KBlockRobot.() -> Unit) {
        case(equalTo(expr), block)
    }

    fun case(case: Case, then: KExpr) {
        file.write(case.toString())
        file.write(" -> ")
        then.writeTo(file)
        file.ensureNewline()
    }

    fun case(expr: KExpr, then: KExpr) {
        case(equalTo(expr), then)
    }

    inline infix fun Case.then(block: KBlockRobot.() -> Unit) {
        case(this, block)
    }

    inline infix fun KExpr.then(block: KBlockRobot.() -> Unit) {
        case(this, block)
    }

    infix fun Case.then(expr: KExpr) {
        case(this, expr)
    }

    infix fun KExpr.then(expr: KExpr) {
        case(this, expr)
    }

    fun otherwise(block: KBlockRobot.() -> Unit) = case(Else, block)

    fun otherwise(then: KExpr) = otherwise { evaluate(then) }

    fun equalTo(expr: KExpr): Case = EqualTo(expr)

    fun containedIn(expr: KExpr): Case = In(expr)

    fun notIn(expr: KExpr): Case = NotIn(expr)

    fun instanceOf(cls: String): Case = InstanceOf(cls)

    fun instanceOf(cls: KClass<*>): Case = InstanceOf(cls.qualifiedName!!)

    fun notInstanceOf(cls: String): Case = NotInstanceOf(cls)

    fun notInstanceOf(cls: KClass<*>): Case = NotInstanceOf(cls.qualifiedName!!)

    val otherwise: Case = Else

    sealed class Case {
        abstract override fun toString(): String

        infix fun or(other: Case): Case = Or(this, other)

        internal data class EqualTo(private val expr: KExpr) : Case() {
            override fun toString(): String = "$expr"
        }

        internal data class In(private val expr: KExpr) : Case() {
            override fun toString(): String = "in $expr"
        }

        internal data class NotIn(private val expr: KExpr) : Case() {
            override fun toString(): String = "!in $expr"
        }

        internal data class InstanceOf(private val type: String) : Case() {
            override fun toString(): String = "is $type"
        }

        internal data class NotInstanceOf(private val type: String) : Case() {
            override fun toString(): String = "!is $type"
        }

        internal data class Or(private val first: Case, private val second: Case) : Case() {
            override fun toString(): String = "$first, $second"
        }

        internal object Else : Case() {
            override fun toString(): String = "else"
        }
    }
}