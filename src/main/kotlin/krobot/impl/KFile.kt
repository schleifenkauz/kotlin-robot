/**
 *@author Nikolaus Knop
 */

package krobot.impl

class KFile(private val writer: Appendable) {
    private var indentCount = 0

    private var indent = ""

    private var isNewLine = true

    fun incIndent() {
        ++indentCount
        indent += INDENT
    }

    fun decIndent() {
        check(indentCount > 0) { "Already no indent" }
        --indentCount
        indent = indent.drop(4)
    }

    private fun indent() {
        if (isNewLine) {
            writer.append(indent)
        }
    }

    fun write(csq: CharSequence) {
        indent()
        writer.append(csq)
        isNewLine = false
    }

    fun write(char: Char) {
        indent()
        writer.append(char)
        isNewLine = false
    }

    fun writeln(line: CharSequence = "") {
        write(line)
        newline()
    }

    fun newline() {
        writer.appendln()
        isNewLine = true
    }

    fun ensureNewline() {
        if (!isNewLine) newline()
    }

    companion object {
        private const val INDENT = "    "
    }
}