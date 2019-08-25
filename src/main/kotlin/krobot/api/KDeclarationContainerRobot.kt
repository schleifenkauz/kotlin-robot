/**
 * @author Nikolaus Knop
 */

package krobot.api

import krobot.api.KClassRobot.Companion.body
import krobot.impl.KFile

@KRobotDsl
abstract class KDeclarationContainerRobot @PublishedApi internal constructor(
    @PublishedApi internal val file: KFile
) : KRobot(file) {
    private fun addProperty(
        propertyType: String,
        mutable: Boolean,
        modifiers: KModifiersRobot.() -> Unit,
        name: String,
        type: KtType? = null,
        body: KPropertyRobot.() -> Unit
    ) {
        file.ensureNewline()
        KModifiersRobot.write(file, modifiers)
        file.write(propertyType)
        file.write(" ")
        file.write(name)
        if (type != null) {
            file.write(": ")
            file.write(type.toString())
        }
        KPropertyRobot(file, mutable).body()
        file.ensureNewline()
    }

    fun addVar(
        name: String,
        type: KtType? = null,
        modifiers: KModifiersRobot.() -> Unit = {},
        body: KPropertyRobot.() -> Unit
    ) {
        addProperty("var", true, modifiers, name, type, body)
    }

    fun addVal(
        name: String,
        type: KtType? = null,
        modifiers: KModifiersRobot.() -> Unit = {},
        body: KPropertyRobot.() -> Unit
    ) {
        addProperty("val", false, modifiers, name, type, body)
    }

    fun addFunction(
        name: String,
        modifiers: KModifiersRobot.() -> Unit = {},
        receiver: KtType? = null,
        typeParameters: KTypeParametersRobot.() -> Unit = {},
        parameters: KParametersRobot.() -> Unit = {},
        returnType: KtType = type("kotlin.Unit"),
        body: KFunctionRobot.() -> Unit = {}
    ) {
        KFunctionRobot.write(file, name, typeParameters, modifiers, parameters, returnType, body, receiver)
    }

    fun addSingleExprFunction(
        name: String,
        modifiers: KModifiersRobot.() -> Unit = {},
        receiver: KtType? = null,
        typeParameters: KTypeParametersRobot.() -> Unit = {},
        parameters: KParametersRobot.() -> Unit = {},
        returnType: KtType? = null,
        singleExpression: () -> KExpr
    ) {
        KFunctionRobot.writeNoBody(file, name, modifiers, typeParameters, parameters, returnType, receiver)
        file.write(" = ")
        singleExpression().writeTo(file)
        file.writeln()
    }

    fun addAbstractFunction(
        name: String,
        modifiers: KModifiersRobot.() -> Unit = {},
        typeParameters: KTypeParametersRobot.() -> Unit = {},
        receiver: KtType? = null,
        parameters: KParametersRobot.() -> Unit = {},
        returnType: KtType
    ) {
        KFunctionRobot.writeNoBody(file, name, {
            modifiers()
            if (this@KDeclarationContainerRobot !is KInterfaceRobot) {
                abstract()
            }
        }, typeParameters, parameters, returnType, receiver)
        file.writeln()
    }

    fun addClass(
        name: String,
        modifiers: KModifiersRobot.() -> Unit = {},
        typeParameters: KTypeParametersRobot.() -> Unit = {},
        primaryConstructor: KPrimaryConstructorRobot.() -> Unit = {},
        inheritance: KInheritanceRobot.() -> Unit = {},
        members: KClassRobot.() -> Unit = {}
    ) {
        KClassRobot.write(file, name, modifiers, typeParameters, primaryConstructor, inheritance, members)
    }

    fun addInterface(
        name: String,
        modifiers: KModifiersRobot.() -> Unit = {},
        typeParameters: KTypeParametersRobot.() -> Unit = {},
        inheritance: KInheritanceRobot.() -> Unit = {},
        members: KInterfaceRobot.() -> Unit = {}
    ) {
        KInterfaceRobot.write(file, name, modifiers, typeParameters, inheritance, members)
    }

    fun addObject(
        name: String,
        modifiers: KModifiersRobot.() -> Unit = {},
        inheritance: KInheritanceRobot.() -> Unit = {},
        members: KObjectRobot.() -> Unit = {}
    ) {
        KObjectRobot.write(file, name, modifiers, inheritance, members)
    }

    fun addAnnotationClass(name: String, parameters: KParametersRobot.() -> Unit) {
        with(file) {
            ensureNewline()
            file.write("annotation class ")
            file.write(name)
            KParametersRobot.write(file, parameters)
        }
    }

    fun addEnumClass(
        name: String,
        modifiers: KModifiersRobot.() -> Unit,
        inheritance: KInheritanceRobot.() -> Unit,
        entries: KEnumEntryRobot.() -> Unit,
        body: KClassRobot.() -> Unit
    ) {
        addClass(
            name,
            modifiers = { modifiers(); enum() },
            typeParameters = {},
            primaryConstructor = {},
            inheritance = inheritance,
            members = {
                KEnumEntryRobot.write(file, entries)
                body()
            }
        )
    }

    fun addCompanion(
        modifiers: KModifiersRobot.() -> Unit = {},
        inheritance: KInheritanceRobot.() -> Unit = {},
        members: KObjectRobot.() -> Unit
    ) {
        KModifiersRobot.write(file, modifiers)
        file.write("companion object")
        KInheritanceRobot(file).inheritance()
        with(KObjectRobot(file)) {
            body(file, members)
        }
    }
}
