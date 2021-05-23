package model

enum class Synchronization {
    ASYNC,
    SYNC
}

enum class Protocol {
    REST_HTTP,
    MESSAGE_ORIENTED,
    GRPC
}

data class Request(
    val from: String,
    val to: List<String>,
    val synchronization: Synchronization,
    val protocol: Protocol,
    val endpoint: String,
) {
    override fun toString(): String {
        return "--> $to($endpoint)"
    }
}