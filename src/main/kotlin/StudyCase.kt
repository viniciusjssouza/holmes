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
import java.time.LocalDateTime
import kotlin.system.exitProcess

class StudyCase(
    private val knowledgeBaseFileLocation: String,
    private val microchaosFileLocation: String,
    private val startIsoDate: String,
    private val endIsoDate: String) {

    companion object {
        val logger = loggerFor<StudyCase>()
    }

    private val solver: PrologSolver

    init {
        solver = loadKnowledgeBase()
        val alerts = readAlerts()
        solver.addAlerts(alerts)
    }

    fun solveQuery(query: String): Set<String> {
        try {
            logger.debug("Query: $query")
            return solver.solve(query)
        } catch (err: Exception) {
            logger.error("Error while solving query '$query'", err)
            exitProcess(1)
        }
    }

    fun readAlerts(): List<Alert> {
        val alertFiles = AlertFiles.collect("./")
        val from = LocalDateTime.parse(this.startIsoDate)
        val to = LocalDateTime.parse(this.endIsoDate)
        return ESAlertsDataSource(alertFiles.toTypedArray()).readAlerts(from, to)
    }

    private fun loadKnowledgeBase(): PrologSolver {
        val topology = readTopology()
        val solver = PrologSolver()
        logger.debug("Topology loaded:\n${topology.toString().prependIndent("\t")}")
        try {
            solver.addTopology(topology)
            solver.loadTheory(File(knowledgeBaseFileLocation))
            return solver;
        } catch (err: SolverException) {
            logger.error("Error while loading theories", err)
            exitProcess(1)
        }
    }


    private fun readTopology(): Topology {
        val fileInputStream = FileInputStream(this.microchaosFileLocation)
        return MicrochaosConfigTopologyDataSource(fileInputStream).readTopology()
    }

}