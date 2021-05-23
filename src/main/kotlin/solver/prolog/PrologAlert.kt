package solver.prolog

import model.Alert
import model.State

typealias AlertMapper = () -> String

class PrologAlert(private val alert: Alert) {
    private val alertsToRules = mapOf<String, AlertMapper>(
        "microchaos.5xx_response" to ::internalServerErrMapper,
        "microchaos.high_latency" to ::highLatencyMapper,
//        "microchaos.traffic-spike" to "trafficSpike",
//        "microchaos.high_cpu_usage" to "highCpuUsage",
//        "microchaos.high_memory_usage" to "highMemoryUsage",
//        "microchaos.application_shutdown" to "terminatedProcess",
    )

    fun toFact(): String {
        return alertsToRules[alert.name]?.let { mapper ->
            if (alert.state == State.ALERTED) {
                mapper()
            } else {
                ""
            }
        } ?: throw IllegalStateException("Unsupported alert: '${alert.name}'")
    }

    private fun highLatencyMapper() =
        FactBuilder("highLatencyInternal")
            .withExplanation(alert.message ?: "highLatencyInternal")
            .withParameters(serviceNameParam(), endpointParam())
            .withTimeWindow(alert.timestamp)
            .build()

    private fun internalServerErrMapper() =
        FactBuilder("internalServerErrAlert")
            .withExplanation(alert.message ?: "internalServerErrAlert")
            .withParameters(serviceNameParam(), endpointParam())
            .withTimeWindow(alert.timestamp)
            .build()

    private fun serviceNameParam() = alert.subjects[0]

    private fun endpointParam() = "'${alert.subjects[1]}'"
}