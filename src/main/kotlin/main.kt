import datasource.elasticsearch.AlertFiles
import datasource.elasticsearch.ESAlertsDataSource
import datasource.microchaos.MicrochaosConfigTopologyDataSource
import infra.loggerFor
import model.Alert
import model.Topology
import java.io.FileInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val logger = loggerFor("Main")
const val ONLINE_BOUTIQUE_MICROCHAOS_CONFIG = "experimental-env/google-online-boutique/online-boutique.yml"


fun main(args: Array<String>) {
    logger.info("Starting Holmes")

    val alerts = readAlerts(LocalDateTime.parse("2021-05-14T00:00:00"))
    val topology = readTopology()

    println(alerts)
    println(topology)
}

fun readAlerts(from: LocalDateTime): List<Alert> {
    val alertFiles = AlertFiles.collect("./")
    return ESAlertsDataSource(alertFiles.toTypedArray()).readAlerts(from)
}

fun readTopology(): Topology {
    val fileInputStream = FileInputStream(ONLINE_BOUTIQUE_MICROCHAOS_CONFIG)
    return MicrochaosConfigTopologyDataSource(fileInputStream).readTopology()
}
