/**
 *@author Nikolaus Knop
 */

package krobot.api

import krobot.impl.KFile

@KRobotDsl
open class KParametersRobot @PublishedApi internal constructor(private val file: KFile) {
    private var isFirst = true

    infix fun String.of(type: KtType) {
        if (!isFirst) file.write(", ")
        file.write(this)
        file.write(": ")
        file.write(type.toString())
        isFirst = false
    }

    fun String.of(type: KtType, defaultValue: KExpr?) {
        of(type)
        if (defaultValue != null) {
            file.write(" = ")
            defaultValue.writeTo(file)
        }
    }

    fun String.of(type: String, defaultValue: KExpr?) {
        of(type(type), defaultValue)
    }

    infix fun String.of(type: String) {
        of(type(type))
    }

    fun String.of(type: KtType, defaultValue: KExpr? = null, modifiers: KModifiersRobot.() -> Unit) {
        KModifiersRobot.write(file, modifiers)
        of(type, defaultValue)
    }

    @PublishedApi internal companion object {
        fun write(file: KFile, parameters: KParametersRobot.() -> Unit) {
            file.write("(")
            KParametersRobot(file).parameters()
            file.write(")")
        }
    }
}