package datasource.elasticsearch

import infra.serialization.DateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class KibanaAlert(
    val alertName: String,
    val alertInstanceId: String,
    @Serializable(with = DateSerializer::class) val alertedAt: LocalDateTime,
    val group: String,
    val reason: String,
    val alertState: String,
) {

}

@Serializable
data class Document(
    val _source: KibanaAlert
) {

}

@Serializable
data class Hits(
    val hits: List<Document>
) {

}

@Serializable
data class SearchResult(
    val hits: Hits,
) {

}