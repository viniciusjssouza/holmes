import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class OnlineBoutiqueIT {

    private val studyCase = StudyCase(
        PROLOG_KNOWLEDGE_BASE,
        ONLINE_BOUTIQUE_MICROCHAOS_CONFIG,
        "2021-05-14T00:00:00",
        "2022-05-14T12:27:00"
    )

    @Test
    fun `case1-HighLatencyOnCartService`() {
        val query = ConsoleUI.parseQuery(
        "internalServerErr(frontend, '/frontend/checkout-payment', 2021-05-14T12:25:02.370Z, CAUSE)."
        )
        val solutions = studyCase.solveQuery(query)
        println(solutions)
        assertThat(solutions.filter { sol ->
            sol.contains("CAUSE = 'microchaos.high_latency, on service \"cart\"")
        }).isNotEmpty
    }

    @Test
    fun `case2-HighCpuUsageOnCurrencyService`() {
        val query = ConsoleUI.parseQuery(
            "highLatency(frontend, '/frontend/checkout-payment', 2021-05-14T13:06:26.285Z, CAUSE)."
        )
        val solutions = studyCase.solveQuery(query)
        println(solutions)
        assertThat(solutions.filter { sol ->
            sol.contains("CAUSE = 'microchaos.high_cpu_usage, on service \"currency-service\"")
        }).isNotEmpty
    }
}