package datasource.microchaos

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class UrlTest {

    @Test
    fun `extractServiceNameSuccessfully`() {
        assertThat(Url("http://product-catalog:8081/products").extractServiceName())
            .isEqualTo("product-catalog")
    }

    @Test
    fun `extractEndpoint`() {
        assertThat(Url("http://cart:8087/user/products").extractEndpoint())
            .isEqualTo("/user/products")
    }
}