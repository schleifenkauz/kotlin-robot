/**
 *@author Nikolaus Knop
 */

package krobot.api

import krobot.impl.KFile
import krobot.impl.joinTo

@KRobotDsl
class KModifiersRobot private constructor(private val file: KFile) {
    fun add(modifier: String) {
        with(file) {
            write(modifier)
            write(" ")
        }
    }

    fun annotation(name: String, args: List<KExpr>) {
        with(file) {
            write("@")
            write(name)
            args.joinTo(this)
            write(" ")
        }
    }

    fun annotation(name: String, vararg args: KExpr) {
        annotation(name, args.asList())
    }

    fun public() = add("public")

    fun internal() = add("internal")

    fun protected() = add("protected")

    fun private() = add("private")

    fun expect() = add("expect")

    fun actual() = add("actual")

    fun final() = add("final")

    fun open() = add("open")

    fun abstract() = add("abstract")

    fun sealed() = add("sealed")

    fun const() = add("const")

    fun external() = add("external")

    fun override() = add("override")

    fun lateinit() = add("lateinit")

    fun tailrec() = add("tailrec")

    fun vararg() = add("vararg")

    fun suspend() = add("suspend")

    fun inner() = add("inner")

    fun enum() = add("enum")

    fun annotation() = add("annotation")

    fun companion() = add("companion")

    fun inline() = add("inline")

    fun infix() = add("infix")

    fun operator() = add("operator")

    fun data() = add("data")

    fun noinline() = add("noinline")

    fun crossinline() = add("crossinline")


    @PublishedApi internal companion object {
        fun write(file: KFile, modifiers: KModifiersRobot.() -> Unit) {
            KModifiersRobot(file).modifiers()
        }
    }
}