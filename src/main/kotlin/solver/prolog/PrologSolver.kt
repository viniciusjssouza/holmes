package solver.prolog

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.core.parsing.parse
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.SolutionFormatter
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.classic.classicWithDefaultBuiltins
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.parse
import solver.SolverException
import java.io.File
import java.time.Duration

class PrologSolver(private val timeout: Duration = Duration.ofSeconds(10)) {

    private var theory: Theory = Theory.empty()

    fun addFact() {
        theory += Theory.parse("siblings(joao, mariaa).\n" +
                "father(tiao, joao).")
    }

    fun solve(query: String): List<String> {
        val solver = getSolver();
        val goal = Struct.parse(query, solver.operators)
        val solutions = solver.solveList(goal, this.timeout.toMillis())
        return solutions.map { sol ->
            sol.format(SolutionFormatter.withOperators(solver.operators))
        }
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
