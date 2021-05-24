package solver.prolog

import model.Alert
import model.State

typealias AlertMapper = () -> String

class PrologAlert(private val alert: Alert) {
    private val alertsToRules = mapOf<String, AlertMapper>(
        "microchaos.5xx_response" to ::internalServerErrMapper,
        "microchaos.high_latency" to ::highLatencyMapper,
        "microchaos.high_cpu_usage" to ::highCpuUsageMapper,
        "microchaos.high_memory_usage" to ::highMemoryUsageMapper,
        "microchaos.traffic-spike" to ::trafficSpikeMapper,
        "microchaos.application_shutdown" to ::processTerminatedMapper,
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

    private fun processTerminatedMapper() =
        FactBuilder("processTerminated")
            .withExplanation(this.toString())
            .withParameters(serviceNameParam())
            .withTimeWindow(alert.timestamp)
            .build()

    private fun trafficSpikeMapper() =
        FactBuilder("trafficSpike")
            .withExplanation(this.toString())
            .withParameters(serviceNameParam())
            .withTimeWindow(alert.timestamp)
            .build()

    private fun highCpuUsageMapper() =
        FactBuilder("highCpuUsage")
            .withExplanation(this.toString())
            .withParameters(serviceNameParam())
            .withTimeWindow(alert.timestamp)
            .build()

    private fun highMemoryUsageMapper() =
        FactBuilder("highMemoryUsage")
            .withExplanation(this.toString())
            .withParameters(serviceNameParam())
            .withTimeWindow(alert.timestamp)
            .build()

    private fun highLatencyMapper() =
        FactBuilder("highLatencyInternal")
            .withExplanation(this.toString())
            .withParameters(serviceNameParam(), endpointParam())
            .withTimeWindow(alert.timestamp)
            .build()

    private fun internalServerErrMapper() =
        FactBuilder("internalServerErrAlert")
            .withExplanation(this.toString())
            .withParameters(serviceNameParam(), endpointParam())
            .withTimeWindow(alert.timestamp)
            .build()

    private fun serviceNameParam() = alert.subjects[0]

    private fun endpointParam() = "'${alert.subjects[1]}'"

    override fun toString(): String {
        val endpointInfo = alert.subjects.getOrNull(1)?.let {
            ", endpoint \"${alert.subjects[1]}\""
        } ?: ""
        return "${alert.name}, on service \"${serviceNameParam()}\"$endpointInfo at ${alert.timestamp}"
    }

}