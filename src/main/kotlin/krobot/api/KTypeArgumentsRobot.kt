/**
 *@author Nikolaus Knop
 */

package krobot.api

import krobot.impl.KFile

@KRobotDsl
class KTypeArgumentsRobot private constructor(private val file: KFile) {
    private var isFirst = true

    fun add(type: KtType) {
        with(file) {
            if (isFirst) write("<")
            else write(", ")
            write(type.toString())
        }
        isFirst = false
    }

    fun add(type: String) {
        add(type(type))
    }

    internal fun finish() {
        file.write(">")
    }

    @PublishedApi internal companion object {
        fun write(file: KFile, typeArguments: KTypeArgumentsRobot.() -> Unit) {
            val robot = KTypeArgumentsRobot(file)
            robot.typeArguments()
            if (!robot.isFirst) {
                robot.finish()
            }
        }
    }
}