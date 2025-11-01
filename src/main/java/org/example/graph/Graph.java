package org.example.graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final int V; // Number of vertices
    private final List<List<Edge>> adj;

    public static class Edge {
        public int to;
        public int weight;

        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    public Graph(int V) {
        this.V = V;
        adj = new ArrayList<>(V);
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(v, w));
    }

    public int getV() {
        return V;
    }

    public List<List<Edge>> getAdj() {
        return adj;
    }
}