package krobot.api

import krobot.api.KClassRobot.Companion.nameAndTypeParameters
import krobot.api.KClassRobot.Companion.body
import krobot.impl.KFile

@KRobotDsl
class KObjectRobot @PublishedApi internal constructor(file: KFile) : KDeclarationContainerRobot(file) {
    fun init(block: KBlockRobot.() -> Unit) {
        with(file) {
            ensureNewline()
            write("init {")
            incIndent()
            KBlockRobot(file).block()
            decIndent()
            writeln("}")
        }
    }

    @PublishedApi internal companion object {
        fun write(
            file: KFile,
            name: String,
            modifiers: KModifiersRobot.() -> Unit,
            inheritance: KInheritanceRobot.() -> Unit,
            members: KObjectRobot.() -> Unit
        ) {
            with(KObjectRobot(file)) {
                nameAndTypeParameters(file, modifiers, name, {}, "object")
                KInheritanceRobot(file).inheritance()
                body(file, members)
            }
        }
    }
}