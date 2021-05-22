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
import java.time.LocalDateTime

val logger = loggerFor("Main")
const val ONLINE_BOUTIQUE_MICROCHAOS_CONFIG = "experimental-env/google-online-boutique/online-boutique.yml"
const val PROLOG_KNOWLEDGE_BASE = "./prolog/holmes.pl"


fun main(args: Array<String>) {
    ConsoleUI.printSplash()
    val solver = PrologSolver()
    try {
        solver.loadTheory(File(PROLOG_KNOWLEDGE_BASE))
        solver.addFact()
        ConsoleUI.printSolutions(solver.solve("family(X, tiao)"))
    } catch (err: SolverException) {
        println(err.message)
    }
//
//    val alerts = readAlerts(LocalDateTime.parse("2021-05-14T00:00:00"))
//    val topology = readTopology()
//
//    println(alerts)
//    println(topology)
}

fun readAlerts(from: LocalDateTime): List<Alert> {
    val alertFiles = AlertFiles.collect("./")
    return ESAlertsDataSource(alertFiles.toTypedArray()).readAlerts(from)
}

fun readTopology(): Topology {
    val fileInputStream = FileInputStream(ONLINE_BOUTIQUE_MICROCHAOS_CONFIG)
    return MicrochaosConfigTopologyDataSource(fileInputStream).readTopology()
}
