package model

import java.time.LocalDateTime

data class Alert(
    val timestamp: LocalDateTime,
    val name: String,
    val subjects: List<String>,
    val state: State,
    val message: String?
) {
}