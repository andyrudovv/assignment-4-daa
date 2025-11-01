package org.example.utils;

import java.util.List;

public class TaskData {
    public static class Edge {
        public int u; // Source vertex
        public int v; // Destination vertex
        public int w; // Weight
    }

    public boolean directed;
    public int n; // Number of vertices
    public List<Edge> edges;
    public int source; // Start node for path finding
    public String weight_model;
}