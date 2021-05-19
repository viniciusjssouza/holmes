package datasource.elasticsearch

import model.Alert
import org.junit.jupiter.api.Test
import java.io.File

internal class ESAlertsDataSourceTest {

    @Test
    fun `deserialize an alert json file`() {
        val alerts = readFromResponseTimeJson()
        println(alerts)
    }

    private fun readFromResponseTimeJson(): List<Alert> {
        val jsonFile = File("experimental-env/google-online-boutique/1-response-time-cart/alerts.json")
        val dataSource = ESAlertsDataSource(arrayOf(jsonFile))
        return dataSource.readAlerts()
    }
}