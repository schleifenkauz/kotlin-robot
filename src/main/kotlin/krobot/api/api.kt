/**
 * @author Nikolaus Knop
 */

package krobot.api

import krobot.impl.KFile
import java.io.*
import java.nio.file.Files
import java.nio.file.Path

@KRobotDsl
class KotlinFile internal constructor(
    val pkg: String?,
    val name: String,
    private val write: (file: KFile) -> Unit
) {
    fun writeTo(appendable: Appendable) {
        val kFile = KFile(appendable)
        write(kFile)
    }
}

fun KotlinFile.writeTo(path: Path) {
    Files.newBufferedWriter(path).use { writeTo(it) }
}

fun KotlinFile.writeTo(file: File) {
    BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use { writeTo(it) }
}

fun kotlinFile(
    pkg: String? = null,
    name: String,
    imports: KImportRobot.() -> Unit = {},
    members: KFileRobot.() -> Unit = {}
): KotlinFile = KotlinFile(pkg, name) { file -> KFileRobot.write(file, pkg, imports, members) }


fun kotlinClass(
    pkg: String?,
    imports: KImportRobot.() -> Unit = {},
    name: String,
    modifiers: KModifiersRobot.() -> Unit = {},
    typeParameters: KTypeParametersRobot.() -> Unit = {},
    primaryConstructor: KPrimaryConstructorRobot.() -> Unit,
    inheritance: KInheritanceRobot.() -> Unit,
    members: KClassRobot.() -> Unit = {}
) = kotlinFile(pkg, "$name.kt", imports) {
    addClass(name, modifiers, typeParameters, primaryConstructor, inheritance, members)
}

fun kotlinInterface(
    pkg: String? = null,
    imports: KImportRobot.() -> Unit = {},
    name: String,
    modifiers: KModifiersRobot.() -> Unit = {},
    typeParameters: KTypeParametersRobot.() -> Unit = {},
    inheritance: KInheritanceRobot.() -> Unit = {},
    members: KInterfaceRobot.() -> Unit = {}
) = kotlinFile(pkg, "$name.kt", imports) {
    addInterface(name, modifiers, typeParameters, inheritance, members)
}

fun kotlinObject(
    pkg: String? = null,
    imports: KImportRobot.() -> Unit = {},
    name: String,
    modifiers: KModifiersRobot.() -> Unit = {},
    inheritance: KInheritanceRobot.() -> Unit = {},
    members: KObjectRobot.() -> Unit = {}
) = kotlinFile(pkg, "$name.kt", imports) {
    addObject(name, modifiers, inheritance, members)
}