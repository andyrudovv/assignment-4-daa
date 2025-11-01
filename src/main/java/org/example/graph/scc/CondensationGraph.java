package org.example.graph.scc;

import org.example.graph.Graph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CondensationGraph {
    private final Graph condensationGraph;
    private final List<List<Integer>> sccs;
    private final int[] componentId;

    public CondensationGraph(Graph originalGraph, TarjanSCC sccFinder) {
        this.sccs = sccFinder.getSccs();
        this.componentId = sccFinder.getComponentId();
        int numComponents = sccs.size();
        this.condensationGraph = new Graph(numComponents);

        // Map to store the min weight for an edge between two components (u_comp -> v_comp)
        Map<Long, Integer> componentEdges = new HashMap<>();

        for (int u = 0; u < originalGraph.getV(); u++) {
            int uComp = componentId[u];
            for (Graph.Edge edge : originalGraph.getAdj().get(u)) {
                int v = edge.to;
                int vComp = componentId[v];
                int weight = edge.weight;

                // An edge from u to v means an edge from SCC(u) to SCC(v)
                if (uComp != vComp) {
                    // Create a unique key for the edge (fromComp, toComp)
                    long key = ((long) uComp << 32) | vComp;

                    // Condensation edge weight: Use the minimum weight of all
                    // original cross-SCC edges for the shortest path problem.
                    componentEdges.put(key, Math.min(componentEdges.getOrDefault(key, Integer.MAX_VALUE), weight));
                }
            }
        }

        // Add edges to the condensation graph
        for (Map.Entry<Long, Integer> entry : componentEdges.entrySet()) {
            long key = entry.getKey();
            int weight = entry.getValue();
            int uComp = (int) (key >> 32);
            int vComp = (int) key;
            condensationGraph.addEdge(uComp, vComp, weight);
        }
    }

    public Graph getGraph() {
        return condensationGraph;
    }

    public List<List<Integer>> getSccs() {
        return sccs;
    }

    public int[] getComponentId() {
        return componentId;
    }
}