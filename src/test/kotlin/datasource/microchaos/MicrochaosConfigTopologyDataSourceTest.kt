package datasource.microchaos

import model.Topology
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

internal class MicrochaosConfigTopologyDataSourceTest {

    @Test
    fun `read all services from a file`() {
        val topology = onlineBoutiqueTopology()

        assertThat(topology.services.map { it.name }).containsAll(
            listOf(
                "frontend",
                "cart",
                "checkout",
                "shipping-service",
                "payment-service",
                "currency-service",
                "product-catalog",
                "email-service",
                "recommendation-service"
            )
        )
        assertThat(topology.serviceByName("frontend").port).isEqualTo(8080)
        assertThat(topology.serviceByName("recommendation-service").port).isEqualTo(8088)
    }

    @Test
    fun `read all downstream of a service`() {
        val topology = onlineBoutiqueTopology()
        val checkoutSvc = topology.serviceByName("checkout")

        assertThat(checkoutSvc.downstreams.map { it.to.first() }).containsAll(
            listOf(
                "currency-service",
                "payment-service",
                "shipping-service",
                "email-service",
            )
        )
    }

    private fun onlineBoutiqueTopology(): Topology {
        val datasource = MicrochaosConfigTopologyDataSource(
            File("experimental-env/google-online-boutique/online-boutique.yml")
                .inputStream()
        )
        return datasource.readTopology()
    }
}