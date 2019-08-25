/**
 * @author Nikolaus Knop
 */

package krobot.api

import kotlin.reflect.KType

interface KtType

fun type(name: String) = object : KtType {
    override fun toString(): String = name
}

fun KType.asKtType() = object : KtType {
    override fun toString(): String = this@asKtType.toString()
}

fun functionType(receiverType: KtType? = null, vararg argumentTypes: KtType, returnType: KtType) = type(buildString {
    if (receiverType != null) {
        append(receiverType.toString())
        append('.')
    }
    append('(')
    argumentTypes.joinTo(this)
    append(')')
    append(" -> ")
    append(returnType)
})

@KRobotDsl
class KtTypeParameterization @PublishedApi internal constructor(unparameterized: KtType) {
    private val builder = StringBuilder(unparameterized.toString())

    init {
        builder.append('<')
    }

    private var isFirst = true

    private fun addProjection(variance: String? = null, type: String) {
        if (!isFirst) builder.append(", ")
        if (variance != null) {
            builder.append(variance)
            builder.append(' ')
        }
        builder.append(type)
        isFirst = false
    }

    fun covariant(type: String) {
        addProjection(null, type)
    }

    fun covariant(type: KtType) {
        addProjection(null, type.toString())
    }

    fun outvariant(type: KtType) {
        addProjection("out", type.toString())
    }

    fun outvariant(type: String) {
        addProjection("out", type)
    }

    fun invariant(type: KtType) {
        addProjection("in", type.toString())
    }

    fun invariant(type: String) {
        addProjection("in", type)
    }

    fun star() {
        addProjection(null, "*")
    }

    @PublishedApi internal fun buildType(): KtType {
        builder.append('>')
        return type(builder.toString())
    }
}

fun KtType.parameterizedBy(parameterize: KtTypeParameterization.() -> Unit) =
    KtTypeParameterization(this).apply(parameterize).buildType()

fun KtType.nullable() = type("$this?")

val String.t get() = type(this)