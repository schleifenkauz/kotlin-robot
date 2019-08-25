package krobot.api

import krobot.impl.KFile
import krobot.impl.joinTo

@KRobotDsl
class KClassRobot @PublishedApi internal constructor(
    file: KFile
) : KDeclarationContainerRobot(file) {
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

    fun addConstructor(
        parameters: KParametersRobot.() -> Unit,
        vararg primaryConstructorArgs: KExpr,
        body: KBlockRobot.() -> Unit = {}
    ) {
        with(file) {
            ensureNewline()
            write("constructor")
            KParametersRobot.write(file, parameters)
            write(": this")
            primaryConstructorArgs.asIterable().joinTo(file)
            write(" {")
            incIndent()
            KBlockRobot(file).body()
            decIndent()
            writeln("}")
        }
    }

    @PublishedApi internal companion object {
        fun write(
            file: KFile,
            name: String,
            modifiers: KModifiersRobot.() -> Unit,
            typeParameters: KTypeParametersRobot.() -> Unit,
            primaryConstructor: KPrimaryConstructorRobot.() -> Unit,
            inheritance: KInheritanceRobot.() -> Unit,
            members: KClassRobot.() -> Unit
        ) {
            with(KClassRobot(file)) {
                nameAndTypeParameters(file, modifiers, name, typeParameters, "class")
                writePrimaryConstructor(file, primaryConstructor)
                KInheritanceRobot(file).inheritance()
                body(file, members)
            }
        }

        fun nameAndTypeParameters(
            file: KFile,
            modifiers: KModifiersRobot.() -> Unit,
            name: String,
            typeParameters: KTypeParametersRobot.() -> Unit,
            type: String
        ) {
            file.ensureNewline()
            KModifiersRobot.write(file, modifiers)
            file.write(type)
            file.write(" ")
            file.write(name)
            KTypeParametersRobot.write(file, typeParameters)
        }

        fun <BodyRobot : KDeclarationContainerRobot> BodyRobot.body(
            file: KFile,
            members: BodyRobot.() -> Unit
        ) {
            file.writeln(" {")
            file.incIndent()
            members()
            file.ensureNewline()
            file.decIndent()
            file.writeln("}")
        }

        private fun writePrimaryConstructor(
            file: KFile,
            primaryConstructor: KPrimaryConstructorRobot.() -> Unit
        ) {
            file.write("(")
            KPrimaryConstructorRobot(file).primaryConstructor()
            file.write(")")
        }
    }
}