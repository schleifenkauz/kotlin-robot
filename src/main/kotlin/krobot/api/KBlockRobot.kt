/**
 * @author Nikolaus Knop
 */

package krobot.api

import krobot.impl.KFile

@KRobotDsl
open class KBlockRobot @PublishedApi internal constructor(private val file: KFile): KRobot(file) {
    fun addStatement(stmt: String) {
        file.ensureNewline()
        file.writeln(stmt)
    }

    fun assign(variable: String, expr: KExpr) {
        with(file) {
            ensureNewline()
            write(variable)
            write(" = ")
            expr.writeTo(file)
        }
    }

    fun assign(variable: String, expr: () -> KExpr) {
        assign(variable, expr())
    }

    fun addReturn(returnValue: KExpr? = null) {
        with(file) {
            ensureNewline()
            write("return")
            if (returnValue != null) {
                write(" ")
                returnValue.writeTo(file)
            }
        }
    }

    fun doBreak() {
        file.writeln("break")
    }

    fun doContinue() {
        file.writeln("continue")
    }

    fun doThrow(exception: KExpr) {
        file.write("throw ")
        exception.writeTo(file)
    }

    fun addIf(cond: KExpr, body: KBlockRobot.() -> Unit): KIfExpr {
        val ifExpr = KIfExpr(cond, body)
        evaluate(ifExpr)
        return ifExpr
    }

    fun addWhile(cond: KExpr, body: KBlockRobot.() -> Unit) {
        with(file) {
            ensureNewline()
            write("while (")
            cond.writeTo(file)
            writeln(") {")
            incIndent()
            body()
            decIndent()
            writeln("}")
        }
    }

    fun addFor(name: String, iterable: KExpr, block: KBlockRobot.() -> Unit) {
        with(file) {
            ensureNewline()
            write("for (")
            write(name)
            write(" in ")
            iterable.writeTo(file)
            writeln(") {")
            incIndent()
            block()
            decIndent()
            ensureNewline()
            writeln("}")
        }
    }

    fun addWhen(cases: KWhenExpr.() -> Unit) {
        evaluate(`when`(cases))
    }

    fun addWhen(subject: KExpr, cases: KSubjectWhenExpr.() -> Unit) {
        evaluate(`when`(subject, cases))
    }

    fun addTry(block: KBlockRobot.() -> Unit, catch: Catch? = null, finally: Finally? = null) {
        evaluate(`try`(block, catch, finally))
    }

    fun callFunction(name: String, typeParameters: KTypeParametersRobot.() -> Unit = {}, vararg args: KExpr) {
        evaluate(call(name, typeParameters, *args))
    }

    fun callFunction(name: String, typeParameters: KTypeParametersRobot.() -> Unit = {}, args: List<KExpr>) {
        evaluate(call(name, typeParameters, args))
    }

    fun callFunction(name: String, vararg args: KExpr) {
        callFunction(name, {}, *args)
    }

    fun addVal(name: String, type: KtType? = null, modifiers: KModifiersRobot.() -> Unit = {}): KLocalVariableRobot {
        with(file) {
            ensureNewline()
            KModifiersRobot.write(file, modifiers)
            write("val ")
            write(name)
            if (type != null) {
                write(": ")
                write(type.toString())
            }
        }
        return KLocalVariableRobot(file)
    }

    fun addVar(name: String, type: KtType? = null, modifiers: KModifiersRobot.() -> Unit = {}): KLocalVariableRobot {
        with(file) {
            ensureNewline()
            KModifiersRobot.write(file, modifiers)
            write("var ")
            write(name)
            if (type != null) {
                write(": ")
                write(type.toString())
            }
        }
        return KLocalVariableRobot(file)
    }

    fun evaluate(expr: KExpr) {
        file.ensureNewline()
        expr.writeTo(file)
        file.ensureNewline()
    }
}
