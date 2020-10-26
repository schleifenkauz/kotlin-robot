/**
 * @author Nikolaus Knop
 */

package krobot.api

import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface KtType

fun type(name: Any) = object : KtType {
    private val str = name.toString()

    override fun toString(): String = str
}

@ExperimentalStdlibApi
inline fun <reified T> type() = typeOf<T>().asKtType()

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

    private var isFirst = true

    private fun addProjection(variance: String? = null, type: String) {
        if (isFirst) builder.append("<")
        else builder.append(", ")
        if (variance != null) {
            builder.append(variance)
            builder.append(' ')
        }
        builder.append(type)
        isFirst = false
    }

    fun invariant(type: String) {
        addProjection(null, type)
    }

    fun invariant(type: KtType) {
        addProjection(null, type.toString())
    }

    fun covariant(type: KtType) {
        addProjection("out", type.toString())
    }

    fun covariant(type: String) {
        addProjection("out", type)
    }

    fun contravariant(type: KtType) {
        addProjection("in", type.toString())
    }

    fun contravariant(type: String) {
        addProjection("in", type)
    }

    fun star() {
        addProjection(null, "*")
    }

    @PublishedApi internal fun buildType(): KtType {
        if (!isFirst) builder.append('>')
        return type(builder.toString())
    }
}

fun KtType.parameterizedBy(parameterize: KtTypeParameterization.() -> Unit) =
    KtTypeParameterization(this).apply(parameterize).buildType()

fun KtType.nullable() = type("$this?")

val String.t get() = type(this)