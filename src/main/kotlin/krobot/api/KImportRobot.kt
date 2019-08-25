/**
 *@author Nikolaus Knop
 */

package krobot.api

import krobot.impl.KFile
import krobot.impl.Naming
import kotlin.reflect.KClass

@KRobotDsl
class KImportRobot internal constructor(private val file: KFile) : KRobot(file) {
    private val alreadyImported = mutableSetOf<String>()

    fun import(pkg: String) {
        if (pkg in alreadyImported) return
        Naming.checkPackageName(pkg)
        file.ensureNewline()
        file.write("import ")
        file.writeln(pkg)
        alreadyImported.add(pkg)
    }

    inline fun <reified Class> import() {
        import(Class::class)
    }

    fun import(cls: KClass<*>) {
        import(cls.qualifiedName ?: error("$cls has no qualified name"))
    }

    fun import(cls: Class<*>) {
        import(cls.name ?: error("$cls has no qualified name"))
    }
}