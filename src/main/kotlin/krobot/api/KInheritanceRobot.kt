/**
 *@author Nikolaus Knop
 */

package krobot.api

import krobot.impl.KFile
import krobot.impl.joinTo

@KRobotDsl
class KInheritanceRobot @PublishedApi internal constructor(private val file: KFile) {
    private var isFirst = true

    private fun addSeparator() {
        if (isFirst) file.write(" : ")
        else file.write(", ")
    }

    fun implement(superType: KtType, delegate: KExpr? = null) {
        addSeparator()
        with(file) {
            write(superType.toString())
            if (delegate != null) {
                write(" by ")
                delegate.writeTo(file)
            }
        }
        isFirst = false
    }

    fun extend(superType: KtType, vararg constructorArgs: KExpr) {
        addSeparator()
        with(file) {
            write(superType.toString())
            constructorArgs.asIterable().joinTo(file)
        }
        isFirst = false
    }
}