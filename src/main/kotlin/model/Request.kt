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
    val from: Service,
    val to: List<Service>,
    val synchronization: Synchronization,
    val protocol: Protocol
) {
}