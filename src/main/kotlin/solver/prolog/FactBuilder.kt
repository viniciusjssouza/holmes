package solver.prolog

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

val DEFAULT_WINDOW_SIZE = Duration.ofMinutes(3).toSeconds().toInt() // 3 minutes

private data class TimeWindow(val currentTime: Long, val windowSize: Int = DEFAULT_WINDOW_SIZE) {

    val timeVariable = "T"

    fun conditionClause() =
        " :- between(${currentTime - windowSize.toLong()}, " +
            "${currentTime + windowSize.toLong()}, " +
            "$timeVariable)"
}

class FactBuilder(private val name: String) {

    private var timeWindow: TimeWindow? = null
    private var explanation = name
    private var parameters = emptyList<String>()

    fun withExplanation(explanation: String): FactBuilder {
        this.explanation = explanation
        return this
    }

    fun withParameters(vararg params: String): FactBuilder {
        this.parameters = params.toList()
        return this
    }

    fun withTimeWindow(timestamp: LocalDateTime, windowSize: Int): FactBuilder {
        this.timeWindow = TimeWindow(timestamp.toEpochSecond(ZoneOffset.UTC), windowSize)
        return this
    }

    fun withTimeWindow(timestamp: LocalDateTime): FactBuilder {
        this.timeWindow = TimeWindow(timestamp.toEpochSecond(ZoneOffset.UTC))
        return this
    }

    fun build(): String {
        val paramsList = parameters.joinToString(separator = ", ")
        val timeVar = timeWindow?.timeVariable?.let { ", $it" } ?: ""
        val timeWindowCondition = timeWindow?.conditionClause() ?: ""
        return "$name($paramsList $timeVar ,'$explanation')$timeWindowCondition."
    }
}