package krobot.api

import krobot.api.KClassRobot.Companion.body
import krobot.api.KClassRobot.Companion.nameAndTypeParameters
import krobot.impl.KFile

@KRobotDsl
class KInterfaceRobot @PublishedApi internal constructor(file: KFile) : KDeclarationContainerRobot(file) {
    @PublishedApi internal companion object {
        fun write(
            file: KFile,
            name: String,
            modifiers: KModifiersRobot.() -> Unit,
            typeParameters: KTypeParametersRobot.() -> Unit,
            inheritance: KInheritanceRobot.() -> Unit,
            members: KInterfaceRobot.() -> Unit
        ) {
            with(KInterfaceRobot(file)) {
                nameAndTypeParameters(file, modifiers, name, typeParameters, "interface")
                KInheritanceRobot(file).inheritance()
                body(file, members)
            }
        }
    }
}