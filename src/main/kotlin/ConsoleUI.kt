import solver.prolog.QueryBuilder
import java.lang.IllegalArgumentException
import java.time.Instant
import kotlin.system.exitProcess

object ConsoleUI {

    fun printSplash() {
        println(
            ConsoleUI::class.java.getResourceAsStream("/asciiart.txt")
                .bufferedReader()
                .readText()
        )
    }

    fun printSolutions(solve: List<String>) {
        println("\n############## ROOT CAUSES FOUND #################\n")
        println(solve.joinToString("\n------------------------------------\n"))
    }

    fun parseQuery(query: String): String {
        val regex = """[\w_-]+\([\w_-]+\s*,?\s*.*?,\s*([\w\:\-\.]+)""".toRegex()
        val match = regex.find(query)
        val queryDate = match?.groupValues?.get(1)
            ?: throw IllegalArgumentException("Invalid input date: '$query'")
        val dateToSeconds = QueryBuilder.parseInputDate(queryDate).toString()
        return query.replace(queryDate, dateToSeconds)
    }

    fun readQuery(): String {
        println("Tell me about your case...\n")
        val fact = askInput("What happened?", "internalServerErr", true)
        val service = askInput("Which service?", "frontend", true)
        val endpoint = askInput("Which endpoint?", null, false)
        val at = askInput("When?", null, true)!!
        return QueryBuilder.build(
            fact!!,
            service!!,
            endpoint,
            at
        )
    }

    private tailrec fun askInput(prompt: String, defaultValue: String?, required: Boolean): String? {
        print("$prompt${defaultValue?.let { "[$defaultValue]" } ?: ""} ")
        val line = readLine() ?: exitProcess(0)
        if (line.isNullOrBlank() && defaultValue.isNullOrBlank() && required) {
            return askInput(prompt, defaultValue, required)
        }
        return if (line.isNullOrBlank()) defaultValue else line
    }
}