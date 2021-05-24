import infra.loggerFor

val logger = loggerFor("Main")
const val ONLINE_BOUTIQUE_MICROCHAOS_CONFIG =
    "experimental-env/google-online-boutique/online-boutique.yml"
const val PROLOG_KNOWLEDGE_BASE = "./prolog/knowledge-base.pl"


fun main(args: Array<String>) {
    ConsoleUI.printSplash()
    val studyCase = StudyCase(
        PROLOG_KNOWLEDGE_BASE,
        ONLINE_BOUTIQUE_MICROCHAOS_CONFIG,
        "2021-05-14T00:00:00",
        "2022-05-14T12:27:00"
    )
    runDiagnosisLoop(studyCase, args)
}

fun runDiagnosisLoop(studyCase: StudyCase, args: Array<String>) {
   if (args.isNotEmpty()) {
        val query = ConsoleUI.parseQuery(args.joinToString(" "))
        val solutions = studyCase.solveQuery(query)
        ConsoleUI.printSolutions(solutions)
        return
    }
    while(true) {
        val query = ConsoleUI.readQuery()
        val solutions = studyCase.solveQuery(query)
        ConsoleUI.printSolutions(solutions)
    }
}




