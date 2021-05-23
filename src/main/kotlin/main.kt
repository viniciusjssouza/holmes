import datasource.elasticsearch.AlertFiles
import datasource.elasticsearch.ESAlertsDataSource
import datasource.microchaos.MicrochaosConfigTopologyDataSource
import infra.loggerFor
import model.Alert
import model.Topology
import solver.SolverException
import solver.prolog.PrologSolver
import java.io.File
import java.io.FileInputStream
import java.time.Instant
import java.time.LocalDateTime

val logger = loggerFor("Main")
const val ONLINE_BOUTIQUE_MICROCHAOS_CONFIG =
    "experimental-env/google-online-boutique/online-boutique.yml"
const val PROLOG_KNOWLEDGE_BASE = "./prolog/holmes.pl"


fun main(args: Array<String>) {
    ConsoleUI.printSplash()
    val alerts = readAlerts()
    val topology = readTopology()
    val solver = PrologSolver()
    logger.debug("Topology loaded:\n${topology.toString().prependIndent("\t")}")
    try {
        solver.addAlerts(alerts)
        solver.addTopology(topology)
        solver.loadTheory(File(PROLOG_KNOWLEDGE_BASE))
        val alertAt = Instant.parse("2021-05-14T12:25:02.370Z").epochSecond
        val query = "internalServerErr(frontend, '/frontend/checkout-payment', $alertAt, CAUSE)."
        logger.debug("Query: $query")
        val solutions = solver.solve(query)
        ConsoleUI.printSolutions(solutions)
    } catch (err: SolverException) {
        println(err.message)
    }
}

fun readAlerts(): List<Alert> {
    val alertFiles = AlertFiles.collect("./")
    val from = LocalDateTime.parse("2021-05-14T00:00:00")
    val to = LocalDateTime.parse("2021-05-14T12:27:00")
    return ESAlertsDataSource(alertFiles.toTypedArray()).readAlerts(from, to)
}

fun readTopology(): Topology {
    val fileInputStream = FileInputStream(ONLINE_BOUTIQUE_MICROCHAOS_CONFIG)
    return MicrochaosConfigTopologyDataSource(fileInputStream).readTopology()
}
