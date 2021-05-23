package solver.prolog

import java.time.Instant

object QueryBuilder {
    fun build(
        fact: String,
        service: String,
        endpoint: String? = null,
        at: String,
    ): String {
        val endpointParam = endpoint?.let { ", '$endpoint'" } ?: ""
        val timestampParam = parseInputDate(at)
        return "$fact($service$endpointParam, $timestampParam, CAUSE)."
    }

    fun parseInputDate(at: String) = Instant.parse(at).epochSecond
}