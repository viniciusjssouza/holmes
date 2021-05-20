package datasource

import model.Alert
import java.time.LocalDateTime

interface AlertsDataSource {
    fun readAlerts(from: LocalDateTime): List<Alert>
    fun readAlerts(from: LocalDateTime, to: LocalDateTime): List<Alert>
}