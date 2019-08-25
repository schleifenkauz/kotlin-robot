/**
 * @author Nikolaus Knop
 */

package krobot.api

import krobot.impl.KFile
import krobot.impl.Naming

@KRobotDsl
class KFileRobot private constructor(
    file: KFile
) : KDeclarationContainerRobot(file) {
    @PublishedApi internal fun setPackage(pkg: String?) {
        if (pkg != null) {
            file.write("package ")
            file.writeln(pkg)
        }
    }

    fun addTypeAlias(
        name: String,
        modifiers: KModifiersRobot.() -> Unit = {},
        typeParameters: KTypeParametersRobot.() -> Unit = {},
        type: KtType
    ) {
        Naming.checkIdentifier("Type alias name", name)
        file.ensureNewline()
        KModifiersRobot.write(file, modifiers)
        file.write("typealias ")
        file.write(name)
        KTypeParametersRobot.write(file, typeParameters)
        file.write(" = ")
        file.write(type.toString())
    }

    @PublishedApi internal companion object {

        fun write(
            file: KFile,
            pkg: String?,
            imports: KImportRobot.() -> Unit,
            members: KFileRobot.() -> Unit
        ) {
            with(KFileRobot(file)) {
                setPackage(pkg)
                KImportRobot(file).imports()
                members()
            }
        }
    }
}