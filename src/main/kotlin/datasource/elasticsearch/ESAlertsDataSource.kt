package datasource.elasticsearch

import datasource.AlertsDataSource
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.Alert
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime

class ESAlertsDataSource(private val files: Array<File>) : AlertsDataSource {
    override fun readAlerts(from: LocalDateTime): List<Alert> {
        return this.readAllAlerts().filter {
            it.timestamp >= from
        }
    }

    override fun readAlerts(from: LocalDateTime, to: LocalDateTime): List<Alert> {
        return this.readAllAlerts().filter {
            it.timestamp in from..to
        }
    }

    fun readAllAlerts(): List<Alert> = this.files
        .flatMap(::alertsFromJsonFile)
        .sortedBy { it.timestamp }

    private fun alertsFromJsonFile(file: File): List<Alert> {
        val jsonContent = Files.readString(file.toPath())!!
        val decoder = Json { ignoreUnknownKeys = true }
        val searchResult = decoder.decodeFromString<SearchResult>(jsonContent)
        return searchResult.hits.hits.map(::documentToAlert)
    }

    private fun documentToAlert(document: Document): Alert {
        return document._source.let {
            Alert(
                timestamp = it.alertedAt,
                name = it.alertName,
                subjects = it.alertInstanceId.split(",").map(String::trim),
                state = Mapper.mapAlertState(it.alertState),
                message = it.reason
            )
        }
    }
}