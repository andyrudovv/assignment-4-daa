package org.example.graph.dagsp;

import org.example.graph.Graph;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class DAGPathFinder {
    private final Graph dag;
    private final int source;
    private final Stack<Integer> topologicalOrder;

    public DAGPathFinder(Graph dag, int source) {
        this.dag = dag;
        this.source = source;
        this.topologicalOrder = new Stack<>();
        performTopologicalSort();
    }

    /**
     * Performs a DFS-based Topological Sort.
     */
    private void performTopologicalSort() {
        boolean[] visited = new boolean[dag.getV()];
        for (int i = 0; i < dag.getV(); i++) {
            if (!visited[i]) {
                dfs(i, visited);
            }
        }
    }

    private void dfs(int u, boolean[] visited) {
        visited[u] = true;
        for (Graph.Edge edge : dag.getAdj().get(u)) {
            if (!visited[edge.to]) {
                dfs(edge.to, visited);
            }
        }
        topologicalOrder.push(u); // Node is pushed onto the stack after all its dependencies are visited
    }

    /**
     * Finds the shortest path from the source to all other nodes using DP over the topological order.
     */
    public double[] findShortestPaths() {
        double[] dist = new double[dag.getV()];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);

        if (source < dag.getV()) {
            dist[source] = 0;
        }

        Stack<Integer> topoClone = (Stack<Integer>) topologicalOrder.clone();
        while (!topoClone.isEmpty()) {
            int u = topoClone.pop();

            if (dist[u] != Double.POSITIVE_INFINITY) {
                for (Graph.Edge edge : dag.getAdj().get(u)) {
                    int v = edge.to;
                    int weight = edge.weight;
                    // Relaxation for Shortest Path
                    if (dist[u] + weight < dist[v]) {
                        dist[v] = dist[u] + weight;
                    }
                }
            }
        }
        return dist;
    }

    /**
     * Finds the longest path from the source to all other nodes using DP over the topological order.
     * The longest path is the 'Critical Path' in scheduling.
     */
    public double[] findLongestPaths() {
        double[] dist = new double[dag.getV()];
        Arrays.fill(dist, Double.NEGATIVE_INFINITY); // Initialize to -infinity

        if (source < dag.getV()) {
            dist[source] = 0;
        }

        Stack<Integer> topoClone = (Stack<Integer>) topologicalOrder.clone();
        while (!topoClone.isEmpty()) {
            int u = topoClone.pop();

            if (dist[u] != Double.NEGATIVE_INFINITY) {
                for (Graph.Edge edge : dag.getAdj().get(u)) {
                    int v = edge.to;
                    int weight = edge.weight;
                    // Relaxation for Longest Path (Critical Path)
                    if (dist[u] + weight > dist[v]) {
                        dist[v] = dist[u] + weight;
                    }
                }
            }
        }
        return dist;
    }

    public Stack<Integer> getTopologicalOrder() {
        return topologicalOrder;
    }
}