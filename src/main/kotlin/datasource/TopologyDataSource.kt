package datasource

import model.Topology

interface TopologyDataSource {
    fun readTopology(): Topology
}