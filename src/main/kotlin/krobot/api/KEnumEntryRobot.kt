/**
 *@author Nikolaus Knop
 */

package krobot.api

import krobot.impl.KFile

@KRobotDsl
class KEnumEntryRobot @PublishedApi internal constructor(private val file: KFile) {
    private var isFirst = true

    fun add(name: String) {
        if (!isFirst) {
            file.writeln(",")
        }
        file.write(name)
        isFirst = false
    }

    fun add(name: String, body: KClassRobot.() -> Unit) {
        add(name)
        file.writeln(" {")
        file.incIndent()
        KClassRobot(file).body()
        file.decIndent()
        file.write("}")
    }

    fun String.invoke(body: KClassRobot.() -> Unit) {
        add(this, body)
    }

    @PublishedApi internal companion object {
        fun write(file: KFile, entries: KEnumEntryRobot.() -> Unit) {
            file.ensureNewline()
            KEnumEntryRobot(file).entries()
            file.writeln(";")
        }
    }
}
