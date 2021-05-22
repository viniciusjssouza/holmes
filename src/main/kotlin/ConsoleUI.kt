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
}