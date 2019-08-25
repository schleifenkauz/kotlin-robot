package krobot.api

import krobot.impl.KFile

@KRobotDsl
class KPrimaryConstructorRobot internal constructor(file: KFile) : KParametersRobot(file) {
    fun `val`(name: String) = "val $name"

    fun `var`(name: String) = "var $name"
}
