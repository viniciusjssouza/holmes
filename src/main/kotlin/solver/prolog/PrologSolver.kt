package solver.prolog

import infra.loggerFor
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.core.parsing.parse
import it.unibo.tuprolog.solve.SolutionFormatter
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.classic.classicWithDefaultBuiltins
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.parse
import model.Alert
import model.Service
import model.Topology
import solver.SolverException
import java.io.File
import java.time.Duration

class PrologSolver(private val timeout: Duration = Duration.ofSeconds(10)) {

    companion object {
        val log = loggerFor<PrologSolver>()
    }
    private var theory: Theory = Theory.empty()

    fun addAlerts(alerts: List<Alert>) {
        val facts = alerts.joinToString("\n") { PrologAlert(it).toFact() }
        log.debug("Alerts facts:\n${facts.prependIndent("\t")}")
        theory += Theory.parse(facts)
    }

    fun addTopology(topology: Topology) {
        val facts = topology.services.flatMap { service ->
            service.downstreams.map{ PrologRequest(it).toFact() }
        }.joinToString("\n")
        log.debug("Topology facts:\n${facts.prependIndent("\t")}")
        theory += Theory.parse(facts)
    }

    fun solve(query: String): Set<String> {
        val solver = getSolver();
        val goal = Struct.parse(query, solver.operators)
        val solutions = solver.solveList(goal, this.timeout.toMillis())
        return solutions.map { sol ->
            sol.format(SolutionFormatter.withOperators(solver.operators))
        }
            .filterNot { sol -> sol == "no." }
            .toHashSet()
    }

    fun loadTheory(file: File): Theory {
        try {
            val t = Theory.parse(file.readText())
            println("# Successfully loaded ${t.size} clauses from $file")
            theory += t
        } catch (e: ParseException) {
            throw SolverException(
                """
                    |# Error while parsing theory file: $file
                    |#     Message: ${e.message}
                    |#     Line   : ${e.line}
                    |#     Column : ${e.column}x
                    |#     Clause : ${e.clauseIndex}
                    """.trimMargin()
            )
        }
        return theory
    }

    private fun getSolver(): Solver {
        return Solver.classicWithDefaultBuiltins(staticKb = this.theory)
    }
}
