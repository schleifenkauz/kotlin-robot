/**
 *@author Nikolaus Knop
 */

package krobot.api

import krobot.impl.KFile

@KRobotDsl
class KLocalVariableRobot @PublishedApi internal constructor(private val file: KFile)  {
    infix fun by(delegate: KExpr) {
        file.write(" by ")
        delegate.writeTo(file)
    }

    infix fun initializedWith(v: KExpr) {
        file.write(" = ")
        v.writeTo(file)
    }
}