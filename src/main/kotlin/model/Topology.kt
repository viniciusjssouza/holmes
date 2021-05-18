package model

import java.lang.StringBuilder

class Topology(val services: Set<Service>) {

    fun serviceByName(name: String): Service = services.first { it.name == name }

    override fun toString(): String {
        return services.joinToString("\n", transform = Service::toString)
    }
}

