/**
 * @author Nikolaus Knop
 */

package krobot.api

import krobot.impl.*

@KRobotDsl
open class KRobot internal constructor(private val file: KFile) {
    fun comment(vararg lines: String) {
        when {
            lines.isEmpty() -> {}
            lines.size == 1 -> singleLineComment(lines)
            else            -> multilineComment(lines)
        }
    }

    private fun singleLineComment(lines: Array<out String>) {
        file.write("//")
        file.writeln(lines.first())
    }

    private fun multilineComment(lines: Array<out String>) {
        file.ensureNewline()
        file.writeln("/*")
        for (line in lines) {
            file.write("* ")
            file.writeln(line)
        }
    }

    fun newline() {
        raw { newline() }
    }

    fun raw(write: KFile.() -> Unit) {
        file.write()
    }
}