package datasource.microchaos

import datasource.TopologyDataSource
import model.*
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.InputStream

class MicrochaosConfigTopologyDataSource(private val input: InputStream) : TopologyDataSource {

    override fun readTopology(): Topology {
        val yaml = Yaml();
        val content = yaml.loadAll(input)
        val services = content.map {
            require(it is Map<*, *>) { "Unexpected type for yaml document" }
            this.readService(it)
        }.toHashSet()
        return Topology(services)
    }

    private fun readService(yaml: Map<*, *>): Service {
        val serviceYaml = yaml["service"] as Map<*, *>
        val serviceName = serviceYaml["name"] as String
        val downstreams = this.extractDownstreams(serviceName, serviceYaml)
        val service = Service(
            name = serviceName,
            port = serviceYaml["port"].toString().toInt(),
            downstreams = downstreams,
        )
        return service
    }

    private fun extractDownstreams(service: String, serviceYaml: Map<*, *>): Set<Request> {
        require(serviceYaml["endpoints"] is List<*>) { "Endpoints is not a list" }
        return (serviceYaml["endpoints"] as List<*>)
            .filterIsInstance<Map<*, *>>()
            .flatMap {
                require(it["behavior"] is Map<*, *>) { "Invalid behavior property" }
                val behavior = it["behavior"] as Map<*, *>
                require(behavior["commands"] is List<*>) { "Invalid list of commands" }
                this.downstreamsFromCommands(service, behavior["commands"] as List<*>)
            }.toHashSet()

    }

    private fun downstreamsFromCommands(serviceName: String, commands: List<*>): Set<Request> {
        return commands
            .filterIsInstance<Map<String, Map<String, String>>>()
            .filter { it.containsKey("request") }
            .map {
                val requestFields = it["request"] as Map<String, String>
                val url = Url(requestFields.getValue("target").toString())
                Request(
                    from = serviceName,
                    to = listOf(url.extractServiceName()),
                    synchronization = Synchronization.SYNC,
                    protocol = Protocol.REST_HTTP,
                    endpoint = url.extractEndpoint(),
                )
            }.toHashSet()

    }
}

fun main() {
    val input = File("experimental-env/google-online-boutique/online-boutique.yml").inputStream()
    val topology = MicrochaosConfigTopologyDataSource(input).readTopology()
    println(topology)
}