/**
 * @author Nikolaus Knop
 */

package krobot.api

import krobot.impl.KFile
import krobot.impl.joinTo
import kotlin.reflect.KClass

abstract class KExpr internal constructor() {
    abstract fun writeTo(out: KFile)

    override fun toString(): String {
        val sb = StringBuilder()
        val file = KFile(sb)
        writeTo(file)
        return sb.toString()
    }
}

internal fun expr(string: String) = object : KExpr() {
    override fun writeTo(out: KFile) {
        out.write(string)
    }
}

internal fun expr(buildString: KFile.() -> Unit): KExpr = object : KExpr() {
    override fun writeTo(out: KFile) {
        buildString(out)
    }
}

fun getVar(name: String): KExpr = expr(name)

fun int(value: Int): KExpr = expr(value.toString())

fun long(value: Long) = expr {
    write(value.toString())
    write("L")
}

fun float(value: Float) = expr {
    write(value.toString())
    write("L")
}

fun double(value: Double): KExpr = expr(value.toString())

val `true` = expr("true")

val `false` = expr("false")

fun stringLiteral(value: String): KExpr = expr {
    write('"')
    write(value)
    write('"')
}

fun operatorApplication(left: KExpr, op: String, right: KExpr) = expr {
    left.writeTo(this)
    write(' ')
    write(op)
    write(' ')
    right.writeTo(this)
}

infix fun KExpr.select(other: KExpr) = expr {
    writeTo(this)
    write('.')
    other.writeTo(this)
}

fun KExpr.call(function: String, vararg args: KExpr) = select(krobot.api.call(function, *args))

fun KExpr.call(function: String, args: List<KExpr>) = select(krobot.api.call(function, args))

infix fun KExpr.call(function: String): KExpr = select(krobot.api.call(function))

infix fun KExpr.select(property: String) = select(getVar(property))

val String.e get() = expr(this)

fun call(name: String, typeParameters: KTypeArgumentsRobot.() -> Unit, args: List<KExpr>) =
    expr {
        write(name)
        KTypeArgumentsRobot.write(this, typeParameters)
        if (args.any() && args.last() is Lambda) {
            val firstParameters = args.dropLast(1)
            if (firstParameters.any()) {
                firstParameters.asIterable().joinTo(this)
            }
            write(" ")
            val lambda = args.last()
            lambda.writeTo(this)
        } else {
            args.asIterable().joinTo(this)
        }
    }

fun call(name: String, typeParameters: KTypeArgumentsRobot.() -> Unit, vararg args: KExpr) =
    call(name, typeParameters, args.asList())

fun call(name: String, vararg args: KExpr) = call(name, {}, *args)

fun call(name: String, args: List<KExpr>) = call(name, {}, args)

fun `if`(cond: KExpr, then: KBlockRobot.() -> Unit) = KIfExpr(cond, then)

fun `if`(cond: KExpr, then: KExpr) = KIfExpr(cond, { evaluate(then) })

fun `when`(cases: KWhenExpr.() -> Unit) = KWhenExpr(cases)

fun `when`(subject: KExpr, cases: KSubjectWhenExpr.() -> Unit): KExpr = KSubjectWhenExpr(subject, cases)

fun anonymousObject(inheritance: KInheritanceRobot.() -> Unit, body: KObjectRobot.() -> Unit) = expr {
    write("object")
    KInheritanceRobot(this).inheritance()
    writeln("{")
    incIndent()
    KObjectRobot(this).body()
    decIndent()
    writeln("}")
}

fun `try`(block: KBlockRobot.() -> Unit, catch: Catch? = null, finally: Finally? = null) = expr {
    writeln("try {")
    incIndent()
    KBlockRobot(this).block()
    decIndent()
    writeln("}")
    if (catch != null) {
        val (name, type, catchBlock) = catch
        write("catch(")
        write(name)
        write(": ")
        write(type.toString())
        writeln(") {")
        incIndent()
        KBlockRobot(this).catchBlock()
        decIndent()
        writeln("}")
    }
    if (finally != null) {
        writeln("finally {")
        incIndent()
        KBlockRobot(this).block()
        decIndent()
        writeln("}")
    }
}

data class Finally internal constructor(internal val block: KBlockRobot.() -> Unit)

fun finally(block: KBlockRobot.() -> Unit) = Finally(block)

data class Catch internal constructor(
    internal val name: String,
    internal val type: KtType,
    internal val block: KBlockRobot.() -> Unit
)

fun catch(name: String, type: KtType, block: KBlockRobot.() -> Unit) = Catch(name, type, block)

private class Lambda(private val parameters: List<String>, private val expr: KExpr) : KExpr() {
    override fun writeTo(out: KFile) {
        out.write("{ ")
        if (parameters.any()) {
            parameters.asIterable().joinTo(out)
            out.write(" -> ")
        }
        expr.writeTo(out)
        out.write(" }")
    }
}

fun lambda(vararg parameters: String, body: KExpr): KExpr = Lambda(parameters.asList(), body)

fun lambda(vararg parameters: String, body: KBlockRobot.() -> Unit): KExpr = lambda(*parameters, body = block(body))

operator fun KExpr.plus(other: KExpr) = operatorApplication(this, "+", other)

operator fun KExpr.minus(other: KExpr) = operatorApplication(this, "-", other)

operator fun KExpr.times(other: KExpr) = operatorApplication(this, "*", other)

operator fun KExpr.div(other: KExpr) = operatorApplication(this, "/", other)

operator fun KExpr.rem(other: KExpr) = operatorApplication(this, "%", other)

operator fun KExpr.rangeTo(other: KExpr) = operatorApplication(this, "..", other)

operator fun KExpr.unaryMinus() = expr { write("-"); writeTo(this) }

operator fun KExpr.unaryPlus() = expr { write("-"); writeTo(this) }

operator fun KExpr.get(other: KExpr) = expr { writeTo(this); write('['); other.writeTo(this); write(']') }

operator fun KExpr.set(index: KExpr, other: KExpr) = expr {
    writeTo(this)
    write('[')
    index.writeTo(this)
    write(']')
    write(" = ")
    other.writeTo(this)
}

operator fun KExpr.inc() = expr { writeTo(this); write("++") }

operator fun KExpr.dec() = expr { writeTo(this); write("--") }

infix fun KExpr.lessThan(other: KExpr) = operatorApplication(this, "<", other)

infix fun KExpr.greaterThan(other: KExpr) = operatorApplication(this, ">", other)

infix fun KExpr.lessThanOrEquals(other: KExpr) = operatorApplication(this, "<=", other)

infix fun KExpr.greaterThanOrEquals(other: KExpr) = operatorApplication(this, ">=", other)

operator fun KExpr.not() = expr { write('!'); writeTo(this) }

infix fun KExpr.equalTo(other: KExpr) = operatorApplication(this, "==", other)

infix fun KExpr.instanceOf(type: KtType) = operatorApplication(this, "is", expr(type.toString()))

infix fun KExpr.cast(type: KtType) = operatorApplication(this, "as", expr(type.toString()))

fun block(body: KBlockRobot.() -> Unit): KExpr = expr { KBlockRobot(this).body() }

operator fun String.invoke(vararg args: KExpr) = call(this, *args)

inline fun <reified T : Any> clazz() = T::class.clazz

val <T : Any> KClass<T>.clazz get() = qualifiedName!!.clazz

val String.clazz get() = ("${this}::class").e

val `this` = "this".e

val String.q get() = expr("\"$this\"")

