/**
 * @author Nikolaus Knop
 */

package krobot.impl

import krobot.api.KExpr

@JvmName("joinExpressionsTo")
internal fun Iterable<KExpr>.joinTo(out: KFile, prefix: String = "(", postfix: String = ")") {
    out.write(prefix)
    val it = iterator()
    if (it.hasNext()) it.next().writeTo(out)
    while (it.hasNext()) {
        out.write(", ")
        it.next().writeTo(out)
    }
    out.write(postfix)
}

internal fun Iterable<String>.joinTo(out: KFile, prefix: String = "(", postfix: String = ")") {
    out.write(prefix)
    val it = iterator()
    if (it.hasNext()) out.write(it.next())
    while (it.hasNext()) {
        out.write(", ")
        out.write(it.next())
    }
    out.write(postfix)
}