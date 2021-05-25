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

    @Test
    fun `case3-TrafficSpike`() {
        val query = ConsoleUI.parseQuery(
        "highLatency(frontend, '/frontend/checkout-payment', 2021-05-14T13:33:54.007Z, CAUSE)."
        )
        val solutions = studyCase.solveQuery(query)
        println(solutions)
        assertThat(solutions.filter { sol ->
            sol.contains("CAUSE = 'microchaos.traffic-spike, on service \"frontend\"")
        }).isNotEmpty
    }

    @Test
    fun `case4-HighMemoryUsage`() {
        val query = ConsoleUI.parseQuery(
        "internalServerErr(checkout, '/payments', 2021-05-16T16:40:44.061Z, CAUSE)."
        )
        val solutions = studyCase.solveQuery(query)
        println(solutions)
        assertThat(solutions.filter { sol ->
            sol.contains("CAUSE = 'microchaos.high_memory_usage, on service \"currency-service\"")
        }).isNotEmpty
    }

    @Test
    fun `case5-NetworkFailure`() {
        val query = ConsoleUI.parseQuery(
            "highLatency(frontend, '/frontend/checkout-payment', 2021-05-16T20:42:06.979Z, CAUSE)."
        )
        val solutions = studyCase.solveQuery(query)
        println(solutions)
        assertThat(solutions.filter { sol ->
            sol.contains("CAUSE = 'microchaos.network_failure, on service \"payment-service\"")
        }).isNotEmpty
    }

    @Test
    fun `case6-ApplicationShutdown`() {
        val query = ConsoleUI.parseQuery(
            "highLatency(frontend, '/frontend/checkout-payment', 2021-05-16T22:18:54.056Z, CAUSE)."
        )
        val solutions = studyCase.solveQuery(query)
        println(solutions)
        assertThat(solutions.filter { sol ->
            sol.contains(" CAUSE = 'microchaos.application_shutdown, on service \"payment-service\"")
        }).isNotEmpty
    }
}