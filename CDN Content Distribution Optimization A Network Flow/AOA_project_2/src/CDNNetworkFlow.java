import java.util.*;

/**
 * Network Flow Implementation for CDN Content Distribution Problem
 * Implements min-cost max-flow algorithm using successive shortest paths
 */
public class CDNNetworkFlow {
    private int numNodes;
    private List<Edge> edges;
    private Map<Integer, List<Integer>> graph;           // adjacency list with edge indices
    private Map<Integer, List<Integer>> reverseGraph;    // reverse adjacency list
    private static final int INF = Integer.MAX_VALUE / 2;

    /**
     * Initialize the network flow with given number of nodes
     */
    public CDNNetworkFlow(int numNodes) {
        this.numNodes = numNodes;
        this.edges = new ArrayList<>();
        this.graph = new HashMap<>();
        this.reverseGraph = new HashMap<>();

        for (int i = 0; i < numNodes; i++) {
            graph.put(i, new ArrayList<>());
            reverseGraph.put(i, new ArrayList<>());
        }
    }

    /**
     * Add an edge to the network.
     * Creates both forward and implicit reverse edges for MCF flow cancellation.
     * @param u source node (must be 0 <= u < numNodes)
     * @param v destination node (must be 0 <= v < numNodes, u != v)
     * @param capacity maximum flow through edge (must be >= 0)
     * @param cost cost per unit flow
     * @throws IllegalArgumentException if parameters are invalid
     */
    public void addEdge(int u, int v, int capacity, int cost) {
        if (u < 0 || u >= numNodes || v < 0 || v >= numNodes) {
            throw new IllegalArgumentException(
                String.format("Invalid nodes: u=%d, v=%d, numNodes=%d", u, v, numNodes));
        }
        if (u == v) {
            throw new IllegalArgumentException("Self-loops not supported: u=v=" + u);
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative: " + capacity);
        }
        
        Edge edge = new Edge(u, v, capacity, cost);
        int edgeIdx = edges.size();
        edges.add(edge);
        graph.get(u).add(edgeIdx);
        reverseGraph.get(v).add(edgeIdx);
    }

    private int[] findShortestPathSPFA(int source, int sink, int flowLimit) {
        int[] dist = new int[numNodes];
        int[] parentEdgeIdx = new int[numNodes];
        boolean[] isReverse = new boolean[numNodes];  // track if parent edge is reverse
        boolean[] inQueue = new boolean[numNodes];
        Queue<Integer> queue = new LinkedList<>();

        Arrays.fill(dist, INF);
        Arrays.fill(parentEdgeIdx, -1);

        dist[source] = 0;
        queue.add(source);
        inQueue[source] = true;

        // SPFA main loop
        int iterations = 0;
        final int MAX_ITERATIONS = numNodes * numNodes * 10;  // Safety limit
        
        while (!queue.isEmpty() && iterations < MAX_ITERATIONS) {
            iterations++;
            int u = queue.poll();
            inQueue[u] = false;

            // Check forward edges from u
            for (int edgeIdx : graph.get(u)) {
                Edge edge = edges.get(edgeIdx);
                int residualCap = edge.residualCapacity();
                if (residualCap > 0) {
                    int newDist = dist[u] + edge.residualCost();
                    if (newDist < dist[edge.getV()]) {
                        dist[edge.getV()] = newDist;
                        parentEdgeIdx[edge.getV()] = edgeIdx;
                        isReverse[edge.getV()] = false;
                        if (!inQueue[edge.getV()]) {
                            queue.add(edge.getV());
                            inQueue[edge.getV()] = true;
                        }
                    }
                }
            }

            // Check reverse edges from u (edges where u is destination, can send backward)
            for (int edgeIdx : reverseGraph.get(u)) {
                Edge edge = edges.get(edgeIdx);
                int reverseCapacity = edge.reverseResidualCapacity();
                if (reverseCapacity > 0) {
                    int newDist = dist[u] + edge.reverseResidualCost();
                    if (newDist < dist[edge.getU()]) {
                        dist[edge.getU()] = newDist;
                        parentEdgeIdx[edge.getU()] = edgeIdx;
                        isReverse[edge.getU()] = true;
                        if (!inQueue[edge.getU()]) {
                            queue.add(edge.getU());
                            inQueue[edge.getU()] = true;
                        }
                    }
                }
            }
        }
        
        if (iterations >= MAX_ITERATIONS) {
            System.err.println("WARNING: SPFA hit iteration limit!");
        }

        if (dist[sink] == INF) {
            return new int[]{0, 0};
        }

        // Reconstruct path and find bottleneck capacity
        List<Integer> pathEdges = new ArrayList<>();
        List<Boolean> pathIsReverse = new ArrayList<>();
        int current = sink;
        int minCapacity = flowLimit;
        int reconstructionSteps = 0;
        final int MAX_RECONSTRUCTION = numNodes + 1;  // Safety check

        while (current != source && reconstructionSteps < MAX_RECONSTRUCTION) {
            reconstructionSteps++;
            int edgeIdx = parentEdgeIdx[current];
            boolean reverse = isReverse[current];
            Edge edge = edges.get(edgeIdx);
            
            if (reverse) {
                // Reverse edge: the edge is stored as (u, v) but we're using it as (v, u)
                // So we came FROM edge.getV() TO edge.getU()
                // Therefore, we move to edge.getV()
                minCapacity = Math.min(minCapacity, edge.reverseResidualCapacity());
                current = edge.getV();
            } else {
                // Forward edge: we used it as (u, v)
                // So we came FROM edge.getU() TO edge.getV()
                // Therefore, we move to edge.getU()
                minCapacity = Math.min(minCapacity, edge.residualCapacity());
                current = edge.getU();
            }
            
            pathEdges.add(edgeIdx);
            pathIsReverse.add(reverse);
        }
        
        if (reconstructionSteps >= MAX_RECONSTRUCTION) {
            System.err.println("WARNING: Path reconstruction exceeded max steps!");
            return new int[]{0, 0};
        }

        Collections.reverse(pathEdges);
        Collections.reverse(pathIsReverse);

        // Augment flow along the path
        for (int i = 0; i < pathEdges.size(); i++) {
            int edgeIdx = pathEdges.get(i);
            Edge edge = edges.get(edgeIdx);
            boolean reverse = pathIsReverse.get(i);
            
            if (reverse) {
                edge.cancelFlow(minCapacity);
            } else {
                edge.augmentFlow(minCapacity);
            }
        }

        return new int[]{minCapacity, dist[sink]};
    }

    /**
     * Compute minimum cost maximum flow from source to sink.
     * Uses the Successive Shortest Paths algorithm with SPFA.
     * @param source source node
     * @param sink sink node
     * @param demand total flow to send
     * @return FlowResult containing flow, cost, success status, and timing information
     * @throws IllegalArgumentException if source or sink are invalid
     */
    public FlowResult minCostMaxFlow(int source, int sink, int demand) {
        if (source < 0 || source >= numNodes || sink < 0 || sink >= numNodes) {
            throw new IllegalArgumentException(
                String.format("Invalid source/sink: source=%d, sink=%d, numNodes=%d", 
                    source, sink, numNodes));
        }
        if (source == sink) {
            throw new IllegalArgumentException("Source and sink cannot be the same");
        }
        if (demand < 0) {
            throw new IllegalArgumentException("Demand cannot be negative: " + demand);
        }
        
        long startTime = System.currentTimeMillis();
        int totalFlow = 0;
        long totalCost = 0;

        while (totalFlow < demand) {
            int[] result = findShortestPathSPFA(source, sink, demand - totalFlow);
            int flowAmount = result[0];
            int pathCost = result[1];

            if (flowAmount == 0) {
                // Cannot send more flow
                long elapsedTime = System.currentTimeMillis() - startTime;
                return new FlowResult(totalFlow, totalCost, false, elapsedTime);
            }

            totalFlow += flowAmount;
            totalCost += (long) flowAmount * pathCost;
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        return new FlowResult(totalFlow, totalCost, true, elapsedTime);
    }

    /**
     * Get the number of edges in the network
     */
    public int getEdgeCount() {
        return edges.size();
    }

    /**
     * Get an edge by index
     */
    public Edge getEdge(int idx) {
        return edges.get(idx);
    }

    /**
     * Print all edges and their flows
     */
    public void printFlows() {
        System.out.println("Network edges and flows:");
        for (Edge edge : edges) {
            if (edge.getFlow() > 0) {
                System.out.printf("  %s%n", edge);
            }
        }
    }
}
