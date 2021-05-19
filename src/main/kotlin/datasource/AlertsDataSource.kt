package datasource

import model.Alert

interface AlertsDataSource {
    fun readAlerts(): List<Alert>
}