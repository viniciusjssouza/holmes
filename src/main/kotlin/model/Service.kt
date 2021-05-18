package model

class Service(val name: String, val port: Int, val downstreams: Set<String> = emptySet()) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Service

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "$name(port=$port)\n".plus(downstreams.joinToString("\n") {
            "  ---> $it"
        })
    }

}