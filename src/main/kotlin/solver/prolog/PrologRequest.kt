package solver.prolog

import model.Request

class PrologRequest(private val req: Request) {

    fun toFact() =
        "httpReq(${this.serviceNameParam()}, ${this.destinationParam()}, ${this.endpointParam()})."

    private fun destinationParam() = req.to.first()
    private fun serviceNameParam() = req.from
    private fun endpointParam() = "'${req.endpoint}'"
}