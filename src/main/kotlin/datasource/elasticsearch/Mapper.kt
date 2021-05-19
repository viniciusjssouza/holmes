package datasource.elasticsearch

import model.State
import java.lang.IllegalArgumentException

object Mapper {
    fun mapAlertState(alertState: String): State {
        return when (alertState) {
            "" -> State.RECOVERED
            "ALERT" -> State.RECOVERED
            else -> throw IllegalArgumentException("Unexpected alert state: '$alertState'")
        }
    }
}