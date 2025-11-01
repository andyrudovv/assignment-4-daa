package org.example.graph.scc;

import org.example.graph.Graph;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TarjanSCC {
    private final Graph graph;
    private int time;
    private final int[] disc; // Discovery time
    private final int[] low;  // Low-link value
    private final boolean[] onStack;
    private final Stack<Integer> stack;
    private final List<List<Integer>> sccs;
    private final int[] componentId; // Maps original node to its SCC ID

    public TarjanSCC(Graph graph) {
        this.graph = graph;
        int V = graph.getV();
        this.time = 0;
        this.disc = new int[V];
        this.low = new int[V];
        this.onStack = new boolean[V];
        this.stack = new Stack<>();
        this.sccs = new ArrayList<>();
        this.componentId = new int[V];

        // Initialize arrays with -1 (unvisited)
        for (int i = 0; i < V; i++) {
            disc[i] = -1;
            componentId[i] = -1;
        }

        // Run DFS from all unvisited nodes
        for (int i = 0; i < V; i++) {
            if (disc[i] == -1) {
                strongconnect(i);
            }
        }
    }

    private void strongconnect(int u) {
        disc[u] = low[u] = time++;
        stack.push(u);
        onStack[u] = true;

        for (Graph.Edge edge : graph.getAdj().get(u)) {
            int v = edge.to;
            if (disc[v] == -1) {
                // Tree edge: recurse
                strongconnect(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) {
                // Back edge: v is an ancestor on the stack
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        // If u is a root of an SCC
        if (low[u] == disc[u]) {
            List<Integer> scc = new ArrayList<>();
            int w;
            int sccIndex = sccs.size();
            do {
                w = stack.pop();
                onStack[w] = false;
                componentId[w] = sccIndex; // Assign ID
                scc.add(w);
            } while (w != u);
            sccs.add(scc);
        }
    }

    public List<List<Integer>> getSccs() {
        return sccs;
    }

    public int[] getComponentId() {
        return componentId;
    }
}