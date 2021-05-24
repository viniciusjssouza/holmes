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
import java.lang.Exception
import java.time.Instant
import java.time.LocalDateTime
import kotlin.system.exitProcess

val logger = loggerFor("Main")
const val ONLINE_BOUTIQUE_MICROCHAOS_CONFIG =
    "experimental-env/google-online-boutique/online-boutique.yml"
const val PROLOG_KNOWLEDGE_BASE = "./prolog/knowledge-base.pl"


fun main(args: Array<String>) {
    ConsoleUI.printSplash()

    val solver = loadKnowledgeBase()
    runDiagnosisLoop(solver, args)
}

fun runDiagnosisLoop(solver: PrologSolver, args: Array<String>) {
    val alerts = readAlerts()
    solver.addAlerts(alerts)
    if (args.isNotEmpty()) {
        val query = ConsoleUI.parseQuery(args.joinToString(" "))
        solveQuery(solver, query)
        return
    }
    while(true) {
        val query = ConsoleUI.readQuery()
        solveQuery(solver, query)
    }
}

fun solveQuery(solver: PrologSolver, query: String) {
    try {
        logger.debug("Query: $query")
        val solutions = solver.solve(query)
        ConsoleUI.printSolutions(solutions)
    } catch (err: Exception) {
        logger.error("Error while solving query '$query'", err)
        exitProcess(1)
    }
}

fun loadKnowledgeBase(): PrologSolver {
    val topology = readTopology()
    val solver = PrologSolver()
    logger.debug("Topology loaded:\n${topology.toString().prependIndent("\t")}")
    try {
        solver.addTopology(topology)
        solver.loadTheory(File(PROLOG_KNOWLEDGE_BASE))
        return solver;
    } catch (err: SolverException) {
        logger.error("Error while loading theories", err)
        exitProcess(1)
    }
}

fun readAlerts(): List<Alert> {
    val alertFiles = AlertFiles.collect("./")
    val from = LocalDateTime.parse("2021-05-14T00:00:00")
    val to = LocalDateTime.parse("2022-05-14T12:27:00")
    return ESAlertsDataSource(alertFiles.toTypedArray()).readAlerts(from, to)
}

fun readTopology(): Topology {
    val fileInputStream = FileInputStream(ONLINE_BOUTIQUE_MICROCHAOS_CONFIG)
    return MicrochaosConfigTopologyDataSource(fileInputStream).readTopology()
}
