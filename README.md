# Assignment 4: Smart Campus Scheduling Report

## 1. Data Context and Graph Structure

The analysis was performed on a directed graph of 8 tasks (vertices $n=8$). The problem uses an **edge weight model** where weights represent time or cost dependencies. The source node for pathfinding is **Task 4**. The graph structure is mixed, containing both cyclic and acyclic dependencies, which necessitates the use of Strongly Connected Components (SCC) analysis.

---

## 2. Dependency Analysis: SCC and Topological Sort

### Strongly Connected Components (SCC)

The initial analysis successfully compressed the 8-vertex graph into a **Condensation Directed Acyclic Graph (DAG)** composed of **6 components**.

The key finding is the identification of a single non-trivial SCC:
* **Component 0** contains the vertices $\mathbf{[3, 2, 1]}$, representing the graph's cyclic dependency. This means these three tasks must be treated as a single, inseparable block for scheduling.
* The remaining 5 components (Components 1, 2, 3, 4, 5) are all trivial, containing a single task each. For example, the source node, Task 4, is in **Component 5**.

### Topological Ordering

The topological sort determines a valid, linear execution sequence for these 6 components.

1.  **Component Order:** The sequence of components is $\mathbf{[5, 4, 3, 2, 1, 0]}$ (which is the order of the SCC IDs).
2.  **Derived Task Order:** This translates to the following sequential order for the original 8 tasks: $\mathbf{[4, 5, 6, 7, 0, 3, 2, 1]}$. This sequence ensures all task dependencies are met, with the cyclic block (`3, 2, 1`) scheduled at the end.

---

## 3. Scheduling Analysis: Shortest and Longest Paths

All path calculations are conducted on the Condensation DAG, beginning from **Source Component 5** (which contains Task 4).

### Shortest Paths

The shortest path analysis shows the minimum time required to reach each component from the source.

* The source component $\mathbf{[4]}$ has a distance of **0.00**.
* The path proceeds through $\mathbf{[5]}$ with a distance of **2.00**, and $\mathbf{[6]}$ with a distance of **7.00**, culminating at $\mathbf{[7]}$ with a shortest distance of **8.00**.
* The cyclic component $\mathbf{[3, 2, 1]}$ and Task $\mathbf{[0]}$ are **unreachable** from Task 4, resulting in a distance of $\mathbf{INF}$ (Infinity).

### Longest Paths (Critical Path)

The longest path analysis identifies the **critical path**, which is the sequence of tasks that determines the overall minimum completion time for the entire project.

* The longest distances mirror the shortest distances in this specific dataset: $\mathbf{[4]}$ at **0.00**, $\mathbf{[5]}$ at **2.00**, $\mathbf{[6]}$ at **7.00**, and $\mathbf{[7]}$ at **8.00**.
* The unreachable components $\mathbf{[3, 2, 1]}$ and $\mathbf{[0]}$ show a distance of $\mathbf{NINF}$ (Negative Infinity) when using a sign-inversion method for longest path calculation.

### Critical Path Conclusion

The **Critical Path Length** (maximum longest path) for the current task set, starting from Task 4, is **8.00**. This means 8.00 is the absolute minimum time required to complete the scheduled tasks.

---

## 4. Practical Implications

The analysis provides a robust schedule and time estimate for the "Smart Campus Scheduling" scenario.

* **Completion Time:** The critical path length of 8.00 sets the most important metric for project management.
* **Bottleneck Identification:** The tasks on the path leading to $\mathbf{[7]}$ are critical. Furthermore, the cyclic dependency $\mathbf{[3, 2, 1]}$ is confirmed as isolated, requiring a separate mechanism or resolution before it can be integrated into the main workflow.
* **Scheduling Guarantee:** The derived task order $\mathbf{[4, 5, 6, 7, 0, 3, 2, 1]}$ ensures a dependency-compliant execution sequence.