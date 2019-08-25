package krobot.impl

internal object Naming {
    internal fun checkIdentifier(typeOfIdentifier: String, identifier: String) {
        val start = identifier.first()
        require(start.isJavaIdentifierStart()) {
            val problem = "$typeOfIdentifier '$identifier' is not a valid identifier, "
            val reason = "because it starts with $start"
            problem + reason
        }
        val rest = identifier.drop(1)
        require(rest.all { it.isJavaIdentifierPart() }) {
            val problem = "$typeOfIdentifier '$identifier' is not a valid identifier, "
            val reason = "because it contains characters that cannot be part of a identifier"
            problem + reason
        }
    }

    fun checkPackageName(name: String) {
        for (subName in name.split('.')) {
            if (subName == "*") continue
            checkIdentifier("Package sub name", subName)
        }
        if ('_' in name) {
            Logging.warn("Package name $name contains an underscore which discouraged")
        }
    }
}