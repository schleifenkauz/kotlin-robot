package krobot.api

import krobot.impl.KFile

@KRobotDsl
class KFunctionRobot @PublishedApi internal constructor(file: KFile) : KBlockRobot(file) {
    @PublishedApi internal companion object {
        fun writeNoBody(
            file: KFile,
            name: String,
            modifiers: KModifiersRobot.() -> Unit,
            typeParameters: KTypeParametersRobot.() -> Unit,
            parameters: KParametersRobot.() -> Unit,
            returnType: KtType?,
            receiver: KtType?
        ) {
            file.ensureNewline()
            KModifiersRobot.write(file, modifiers)
            file.write("fun ")
            KTypeParametersRobot.write(file, typeParameters)
            if (receiver != null) {
                file.write(receiver.toString())
                file.write('.')
            }
            file.write(name)
            KParametersRobot.write(file, parameters)
            if (returnType != null) {
                file.write(": ")
                file.write(returnType.toString())
            }
        }

        fun write(
            file: KFile,
            name: String,
            typeParameters: KTypeParametersRobot.() -> Unit,
            modifiers: KModifiersRobot.() -> Unit,
            parameters: KParametersRobot.() -> Unit,
            returnType: KtType,
            body: KFunctionRobot.() -> Unit,
            receiver: KtType?
        ) {
            file.ensureNewline()
            writeNoBody(file, name, modifiers, typeParameters, parameters, returnType, receiver)
            file.write(" {")
            file.incIndent()
            KFunctionRobot(file).body()
            file.decIndent()
            file.ensureNewline()
            file.writeln("}")
        }
    }
}
