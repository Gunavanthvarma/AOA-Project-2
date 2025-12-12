import java.util.*;
import java.io.*;

/**
 * Experimental validation for CDN Network Flow Problem
 * Tests running time and validates the algorithm against various inputs
 */
public class ExperimentalValidator {
    private Map<String, List<Map<String, Object>>> results;

    public ExperimentalValidator() {
        results = new HashMap<>();
        results.put("small_scale", new ArrayList<>());
        results.put("medium_scale", new ArrayList<>());
        results.put("large_scale", new ArrayList<>());
        results.put("scalability", new ArrayList<>());
    }

    /**
     * Test on a small network: 1 origin, 2 caches, 3 edges
     */
    public Map<String, Object> testSmallNetwork() {
        CDNNetworkFlow net = new CDNNetworkFlow(8);

        net.addEdge(0, 1, 100, 0);  // super-source to origin

        // Origin to caches
        net.addEdge(1, 2, 50, 5);   // origin to cache1
        net.addEdge(1, 3, 50, 3);   // origin to cache2

        // Caches to edges
        net.addEdge(2, 4, 30, 2);   // cache1 to edge1
        net.addEdge(2, 5, 30, 3);   // cache1 to edge2
        net.addEdge(3, 5, 30, 1);   // cache2 to edge2
        net.addEdge(3, 6, 30, 4);   // cache2 to edge3

        // Edges to sink
        net.addEdge(4, 7, 20, 0);   // edge1 to sink
        net.addEdge(5, 7, 30, 0);   // edge2 to sink
        net.addEdge(6, 7, 20, 0);   // edge3 to sink

        FlowResult result = net.minCostMaxFlow(0, 7, 70);

        Map<String, Object> data = new HashMap<>();
        data.put("elapsed_ms", (double) result.elapsedTimeMs);
        data.put("flow", result.totalFlow);
        data.put("cost", result.totalCost);
        data.put("total_nodes", 8);
        data.put("num_edges", 10);

        return data;
    }

    /**
     * Test on a medium network: 2 origins, 5 caches, 10 edges
     */
    public Map<String, Object> testMediumNetwork() {
        int numOrigins = 2;
        int numCaches = 5;
        int numEdges = 10;

        CDNNetworkFlow net = new CDNNetworkFlow(2 + numOrigins + numCaches + numEdges);

        int source = 0;
        int sink = 2 + numOrigins + numCaches + numEdges - 1;

        int originStart = 1;
        int cacheStart = numOrigins + 1;
        int edgeStart = numOrigins + numCaches + 1;

        Random rand = new Random(42);  // Fixed seed for reproducibility

        // Calculate total demand
        int totalDemand = 0;
        int[] demands = new int[numEdges];
        for (int i = 0; i < numEdges; i++) {
            demands[i] = 20 + rand.nextInt(30);
            totalDemand += demands[i];
        }

        // Super-source to origins
        for (int i = 0; i < numOrigins; i++) {
            net.addEdge(source, originStart + i, totalDemand, 0);
        }

        // Origins to caches
        for (int i = 0; i < numOrigins; i++) {
            for (int j = 0; j < numCaches; j++) {
                int origin = originStart + i;
                int cache = cacheStart + j;
                int capacity = 30 + rand.nextInt(50);
                int cost = 1 + rand.nextInt(9);
                net.addEdge(origin, cache, capacity, cost);
            }
        }

        // Caches to edges
        for (int i = 0; i < numCaches; i++) {
            for (int j = 0; j < numEdges; j++) {
                if (rand.nextDouble() < 0.4) {  // 40% connectivity
                    int cache = cacheStart + i;
                    int edge = edgeStart + j;
                    int capacity = 20 + rand.nextInt(30);
                    int cost = 1 + rand.nextInt(4);
                    net.addEdge(cache, edge, capacity, cost);
                }
            }
        }

        // Edges to sink
        for (int j = 0; j < numEdges; j++) {
            int edge = edgeStart + j;
            net.addEdge(edge, sink, demands[j], 0);
        }

        FlowResult result = net.minCostMaxFlow(source, sink, totalDemand);

        Map<String, Object> data = new HashMap<>();
        data.put("elapsed_ms", (double) result.elapsedTimeMs);
        data.put("flow", result.totalFlow);
        data.put("cost", result.totalCost);

        return data;
    }

    /**
     * Test on a larger network
     */
    public Map<String, Object> testLargeNetwork(int numOrigins, int numCaches, int numEdges) {
        CDNNetworkFlow net = new CDNNetworkFlow(2 + numOrigins + numCaches + numEdges);

        int source = 0;
        int sink = 2 + numOrigins + numCaches + numEdges - 1;

        int originStart = 1;
        int cacheStart = numOrigins + 1;
        int edgeStart = numOrigins + numCaches + 1;

        Random rand = new Random(42);

        // Calculate total demand
        int totalDemand = 0;
        int[] demands = new int[numEdges];
        for (int i = 0; i < numEdges; i++) {
            demands[i] = 30 + rand.nextInt(30);
            totalDemand += demands[i];
        }

        // Super-source to origins
        for (int i = 0; i < numOrigins; i++) {
            net.addEdge(source, originStart + i, totalDemand, 0);
        }

        // Origins to caches (sparse connectivity)
        for (int i = 0; i < numOrigins; i++) {
            for (int j = 0; j < numCaches; j++) {
                if (rand.nextDouble() < 0.5) {  // 50% connectivity
                    int origin = originStart + i;
                    int cache = cacheStart + j;
                    int capacity = 50 + rand.nextInt(100);
                    int cost = 1 + rand.nextInt(7);
                    net.addEdge(origin, cache, capacity, cost);
                }
            }
        }

        // Caches to edges (sparse connectivity)
        for (int i = 0; i < numCaches; i++) {
            for (int j = 0; j < numEdges; j++) {
                if (rand.nextDouble() < 0.3) {  // 30% connectivity
                    int cache = cacheStart + i;
                    int edge = edgeStart + j;
                    int capacity = 20 + rand.nextInt(60);
                    int cost = 1 + rand.nextInt(4);
                    net.addEdge(cache, edge, capacity, cost);
                }
            }
        }

        // Edges to sink
        for (int j = 0; j < numEdges; j++) {
            int edge = edgeStart + j;
            net.addEdge(edge, sink, demands[j], 0);
        }

        FlowResult result = net.minCostMaxFlow(source, sink, totalDemand);

        Map<String, Object> data = new HashMap<>();
        data.put("elapsed_ms", (double) result.elapsedTimeMs);
        data.put("flow", result.totalFlow);
        data.put("cost", result.totalCost);
        data.put("total_nodes", 2 + numOrigins + numCaches + numEdges);
        data.put("num_edges", 1 + numOrigins + (numCaches * 2) + (numCaches * numEdges) + numEdges);
        return data;
    }

    /**
     * Run scalability test with increasing network sizes
     */
    public List<Map<String, Object>> scalabilityTest() {
        List<Map<String, Object>> testResults = new ArrayList<>();

        for (int scale = 1; scale <= 5; scale++) {
            int numOrigins = 1 + scale;
            int numCaches = 5 + 5 * scale;
            int numEdges = 10 + 10 * scale;

            Map<String, Object> result = testLargeNetwork(numOrigins, numCaches, numEdges);

            int totalNodes = 2 + numOrigins + numCaches + numEdges;

            Map<String, Object> data = new HashMap<>();
            data.put("scale", scale);
            data.put("num_origins", numOrigins);
            data.put("num_caches", numCaches);
            data.put("num_edges", numEdges);
            data.put("total_nodes", totalNodes);
            data.put("elapsed_time_ms", result.get("elapsed_ms"));
            data.put("flow", result.get("flow"));
            data.put("cost", result.get("cost"));

            testResults.add(data);
        }

        return testResults;
    }

    /**
     * Run all experiments and collect results
     */
    public void runAllExperiments() {
        System.out.println("============================================================");
        System.out.println("CDN Network Flow Algorithm - Experimental Validation");
        System.out.println("============================================================");

        // Small scale test
        System.out.println("\n[1/4] Running small scale test (1 origin, 2 caches, 3 edges)...");
        for (int i = 0; i < 5; i++) {
            Map<String, Object> data = testSmallNetwork();
            results.get("small_scale").add(data);
        }
        double avgSmall = results.get("small_scale").stream()
                .mapToDouble(r -> (Double) r.get("elapsed_ms"))
                .average()
                .orElse(0);
        System.out.printf("   Average time: %.4f ms%n", avgSmall);

        // Medium scale test
        System.out.println("\n[2/4] Running medium scale test (2 origins, 5 caches, 10 edges)...");
        for (int i = 0; i < 5; i++) {
            Map<String, Object> data = testMediumNetwork();
            results.get("medium_scale").add(data);
        }
        double avgMedium = results.get("medium_scale").stream()
                .mapToDouble(r -> (Double) r.get("elapsed_ms"))
                .average()
                .orElse(0);
        System.out.printf("   Average time: %.4f ms%n", avgMedium);

        // Large scale test
        System.out.println("\n[3/4] Running large scale test (5 origins, 15 caches, 50 edges)...");
        for (int i = 0; i < 3; i++) {
            Map<String, Object> data = testLargeNetwork(5, 15, 50);
            results.get("large_scale").add(data);
        }
        double avgLarge = results.get("large_scale").stream()
                .mapToDouble(r -> (Double) r.get("elapsed_ms"))
                .average()
                .orElse(0);
        System.out.printf("   Average time: %.4f ms%n", avgLarge);

        // Scalability test
        System.out.println("\n[4/4] Running scalability test (varying network sizes)...");
        List<Map<String, Object>> scalabilityResults = scalabilityTest();
        results.put("scalability", scalabilityResults);

        for (Map<String, Object> r : scalabilityResults) {
            System.out.printf("   Scale %d: %d nodes, %.4f ms%n",
                    r.get("scale"), r.get("total_nodes"),
                    r.get("elapsed_time_ms"));
        }
    }

    /**
     * Print summary of results
     */
    public void printSummary() {
        String separator = "============================================================";
        System.out.println("\n" + separator);
        System.out.println("SUMMARY");
        System.out.println(separator);

        double avgSmall = results.get("small_scale").stream()
                .mapToDouble(r -> (Double) r.get("elapsed_ms"))
                .average()
                .orElse(0);
        System.out.printf("Small Scale (avg): %.4f ms%n", avgSmall);

        double avgMedium = results.get("medium_scale").stream()
                .mapToDouble(r -> (Double) r.get("elapsed_ms"))
                .average()
                .orElse(0);
        System.out.printf("Medium Scale (avg): %.4f ms%n", avgMedium);

        double avgLarge = results.get("large_scale").stream()
                .mapToDouble(r -> (Double) r.get("elapsed_ms"))
                .average()
                .orElse(0);
        System.out.printf("Large Scale (avg): %.4f ms%n", avgLarge);

        System.out.println("\nScalability Analysis:");
        System.out.println("Nodes\t\tTime (ms)\tFlow\tCost");
        for (Map<String, Object> r : results.get("scalability")) {
            System.out.printf("%d\t\t%.4f\t\t%d\t%d%n",
                    r.get("total_nodes"), r.get("elapsed_time_ms"),
                    r.get("flow"), r.get("cost"));
        }
    }

    /**
     * Save results to JSON file
     */
    public void saveResults(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("{");
            writer.println("  \"small_scale\": [");
            writeResultsList(writer, results.get("small_scale"), true);
            writer.println("  ],");
            writer.println("  \"medium_scale\": [");
            writeResultsList(writer, results.get("medium_scale"), true);
            writer.println("  ],");
            writer.println("  \"large_scale\": [");
            writeResultsList(writer, results.get("large_scale"), true);
            writer.println("  ],");
            writer.println("  \"scalability\": [");
            writeResultsList(writer, results.get("scalability"), false);
            writer.println("  ]");
            writer.println("}");
            System.out.println("\nResults saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving results: " + e.getMessage());
        }
    }

    private void writeResultsList(PrintWriter writer, List<Map<String, Object>> list, boolean simple) {
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> item = list.get(i);
            writer.print("    {");

            if (simple) {
                writer.printf("\"elapsed_ms\": %.4f, \"flow\": %d, \"cost\": %d, \"total_nodes\": %d, \"num_edges\": %d",
                        item.get("elapsed_ms"), item.get("flow"), item.get("cost"),
                        item.get("total_nodes"), item.get("num_edges"));
            } else {
                writer.printf("\"scale\": %d, \"num_origins\": %d, \"num_caches\": %d, " +
                        "\"num_edges\": %d, \"total_nodes\": %d, \"elapsed_time_ms\": %.4f, " +
                        "\"flow\": %d, \"cost\": %d",
                        item.get("scale"), item.get("num_origins"), item.get("num_caches"),
                        item.get("num_edges"), item.get("total_nodes"),
                        item.get("elapsed_time_ms"), item.get("flow"), item.get("cost"));
            }

            writer.println("}");
            if (i < list.size() - 1) {
                writer.println("    ,");
            } else {
                writer.println();
            }
        }
    }

    public static void main(String[] args) {
        ExperimentalValidator validator = new ExperimentalValidator();
        validator.runAllExperiments();
        validator.printSummary();
        validator.saveResults("../experiments/results.json");
    }
}
