package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.graph.Graph;
import org.example.graph.dagsp.DAGPathFinder;
import org.example.graph.scc.CondensationGraph;
import org.example.graph.scc.TarjanSCC;
import org.example.utils.TaskData;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        String DATA_FILE = "tasks.json";

        try {
            // 1. Read Graph Data
            ObjectMapper mapper = new ObjectMapper();
            TaskData data = loadTaskData(DATA_FILE);
            Graph originalGraph = buildGraph(data);

            System.out.println("--- 1. Strongly Connected Components (SCC) ---");

            // 2. Find SCCs (Tarjan's Algorithm)
            TarjanSCC sccFinder = new TarjanSCC(originalGraph);
            List<List<Integer>> sccs = sccFinder.getSccs();

            System.out.printf("Total Vertices (n): %d\n", data.n);
            System.out.printf("Number of SCCs: %d\n", sccs.size());
            System.out.println("SCCs found (list of vertices):");
            for (int i = 0; i < sccs.size(); i++) {
                System.out.printf("  Component %d (Size: %d): %s\n", i, sccs.get(i).size(), sccs.get(i));
            }

            System.out.println("\n--- 2. Topological Sort (on Condensation DAG) ---");

            // 3. Build Condensation Graph
            CondensationGraph condensation = new CondensationGraph(originalGraph, sccFinder);
            Graph condensationDAG = condensation.getGraph();
            int sourceComp = sccFinder.getComponentId()[data.source];

            // 4. Compute Topological Order
            DAGPathFinder pathFinder = new DAGPathFinder(condensationDAG, sourceComp);
            Stack<Integer> topoStack = pathFinder.getTopologicalOrder();

            // Extract topological order from the stack (it's in reverse order initially)
            List<Integer> topoOrder = Arrays.asList(new Integer[topoStack.size()]);
            for (int i = 0; i < topoOrder.size(); i++) {
                topoOrder.set(topoOrder.size() - 1 - i, topoStack.get(i));
            }

            System.out.println("Topological Order of Components (SCC ID): " + topoOrder);

            // Derive order of original tasks (vertices)
            List<Integer> taskOrder = topoOrder.stream()
                    .flatMap(sccId -> sccs.get(sccId).stream())
                    .toList();
            System.out.println("Derived Order of Original Tasks (Vertices): " + taskOrder);


            System.out.println("\n--- 3. Shortest/Longest Paths (on Condensation DAG) ---");

            // 5. Compute Paths
            // The source node is the SCC containing the original source node
            System.out.printf("Source Task: %d -> Source Component ID: %d\n", data.source, sourceComp);

            // Shortest Paths
            double[] shortestDists = pathFinder.findShortestPaths();
            System.out.println("\nShortest Paths from Source Component (ID " + sourceComp + "):");
            printDistances(shortestDists, condensation.getSccs());

            // Longest Paths (Critical Path)
            double[] longestDists = pathFinder.findLongestPaths();
            double criticalPathLength = Arrays.stream(longestDists).max().orElse(0.0);

            System.out.println("\nLongest Paths (Critical Path) from Source Component (ID " + sourceComp + "):");
            printDistances(longestDists, condensation.getSccs());
            System.out.printf("\nCRITICAL PATH LENGTH (MAX Longest Path): %.2f\n", criticalPathLength);


        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static TaskData loadTaskData(String fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Use the ClassLoader to get the resource stream from the classpath
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(fileName)) {

            if (inputStream == null) {
                // Throw an exception if the file isn't found in the resources path
                throw new java.io.FileNotFoundException(
                        "Resource not found: " + fileName +
                                ". Ensure it is placed directly under src/main/resources."
                );
            }

            // Read the JSON from the stream
            return mapper.readValue(inputStream, TaskData.class);
        }
    }

    private static Graph buildGraph(TaskData data) {
        Graph graph = new Graph(data.n);
        for (TaskData.Edge edge : data.edges) {
            graph.addEdge(edge.u, edge.v, edge.w);
        }
        return graph;
    }

    private static void printDistances(double[] dists, List<List<Integer>> sccs) {
        for (int i = 0; i < dists.length; i++) {
            if (dists[i] == Double.POSITIVE_INFINITY) {
                System.out.printf("  Component %d (%s): INF\n", i, sccs.get(i));
            } else if (dists[i] == Double.NEGATIVE_INFINITY) {
                System.out.printf("  Component %d (%s): NINF\n", i, sccs.get(i));
            } else {
                System.out.printf("  Component %d (%s): %.2f\n", i, sccs.get(i), dists[i]);
            }
        }
    }
}