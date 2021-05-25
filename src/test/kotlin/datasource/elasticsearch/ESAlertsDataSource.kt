package datasource.elasticsearch

import model.Alert
import model.State
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDateTime

internal class ESAlertsDataSourceTest {

    private val files = AlertFiles.collect("./")

    @Test
    fun `deserialize an alert json file`() {
        val alerts = readAllAlerts(files.filter { it.absolutePath.contains("response-time") })

        checkOrder(alerts)
        alerts.first().let {
            assertThat(it.name).isEqualTo("microchaos.high_latency")
            assertThat(it.timestamp).isEqualToIgnoringNanos(
                LocalDateTime.of(2021, 5, 14, 12, 21, 17)
            )
            assertThat(it.state).isSameAs(State.ALERTED)
            assertThat(it.message).contains("requests_seconds is greater than a threshold of 1 (current value is 1.392508928)")
            assertThat(it.subjects).contains("frontend", "/frontend/checkout-payment")
        }
    }

    @Test
    fun `deserialize multiple json files`() {
        val alerts = readAllAlerts(files)

        checkOrder(alerts)
        assertThat(alerts.size).isGreaterThan(10)
        assertThat(alerts.map { it.name }.toSet()).contains(
            "microchaos.high_latency",
            "microchaos.5xx_response",
            "microchaos.high_cpu_usage",
            "microchaos.traffic-spike",
            "microchaos.high_memory_usage",
            "microchaos.application_shutdown"
        )
    }

    @Test
    fun `find alerts by date`() {
        val alerts = readAllAlertsByDateRange(
            files,
            LocalDateTime.of(2021, 5, 16, 16, 39),
            LocalDateTime.of(2021, 5, 16, 16, 46),
        )

        checkOrder(alerts)
        assertThat(alerts.size).isEqualTo(5)
        assertThat(alerts.map { it.name }.toSet()).contains(
            "microchaos.5xx_response",
            "microchaos.high_memory_usage",
        )
    }

    private fun checkOrder(alerts: List<Alert>) {
        (1 until alerts.size).forEach { idx ->
            assertThat(alerts[idx].timestamp).isAfter(alerts[idx - 1].timestamp)
        }
    }

    private fun readAllAlerts(jsonFiles: List<File>): List<Alert> {
        val dataSource = ESAlertsDataSource(jsonFiles.toTypedArray())
        return dataSource.readAllAlerts()
    }

    private fun readAllAlertsByDateRange(
        jsonFiles: List<File>,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Alert> {
        val dataSource = ESAlertsDataSource(jsonFiles.toTypedArray())
        return dataSource.readAlerts(from, to)
    }
}