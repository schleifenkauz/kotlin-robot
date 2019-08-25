/**
 *@author Nikolaus Knop
 */

package krobot.api

import krobot.impl.KFile

@KRobotDsl
class KPropertyBlockRobot @PublishedApi internal constructor(file: KFile): KBlockRobot(file) {
    val field get() = getVar("field")
}