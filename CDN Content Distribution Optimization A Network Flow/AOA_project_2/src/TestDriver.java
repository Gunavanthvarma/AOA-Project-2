/**
 * Simple test driver for the CDN Network Flow implementation
 */
public class TestDriver {
    public static void main(String[] args) {
        System.out.println("=== CDN Network Flow - Simple Test ===\n");

        // Test 1: Small network
        System.out.println("Test 1: Small Network");
        System.out.println("Nodes: 0=source, 1-2=intermediate, 3-4=intermediate, 5 nodes total");

        CDNNetworkFlow net = new CDNNetworkFlow(5);
        net.addEdge(0, 1, 10, 1);
        net.addEdge(0, 2, 10, 2);
        net.addEdge(1, 3, 5, 2);
        net.addEdge(1, 4, 10, 1);
        net.addEdge(2, 3, 10, 1);
        net.addEdge(2, 4, 10, 3);
        net.addEdge(3, 4, 10, 1);

        FlowResult result = net.minCostMaxFlow(0, 4, 20);
        System.out.println("Result: " + result);
        System.out.println();

        // Test 2: CDN-style network
        System.out.println("Test 2: CDN-style Network");
        System.out.println("Structure: super-source -> origin -> caches -> edges -> super-sink");

        CDNNetworkFlow cdnNet = new CDNNetworkFlow(8);

        // Super-source to origin
        cdnNet.addEdge(0, 1, 100, 0);

        // Origin to caches
        cdnNet.addEdge(1, 2, 50, 5);
        cdnNet.addEdge(1, 3, 50, 3);

        // Caches to edges
        cdnNet.addEdge(2, 4, 30, 2);
        cdnNet.addEdge(2, 5, 30, 3);
        cdnNet.addEdge(3, 5, 30, 1);
        cdnNet.addEdge(3, 6, 30, 4);

        // Edges to super-sink
        cdnNet.addEdge(4, 7, 20, 0);
        cdnNet.addEdge(5, 7, 30, 0);
        cdnNet.addEdge(6, 7, 20, 0);

        FlowResult cdnResult = cdnNet.minCostMaxFlow(0, 7, 70);
        System.out.println("Result: " + cdnResult);
        System.out.println();

        // Test 3: Infeasible case
        System.out.println("Test 3: Infeasible Network (insufficient capacity)");
        CDNNetworkFlow infeasibleNet = new CDNNetworkFlow(3);
        infeasibleNet.addEdge(0, 1, 5, 1);
        infeasibleNet.addEdge(1, 2, 3, 1);

        FlowResult infeasibleResult = infeasibleNet.minCostMaxFlow(0, 2, 10);
        System.out.println("Result: " + infeasibleResult);
        System.out.println("Expected: Feasible=false, Flow=3 (limited by bottleneck)");
        System.out.println();

        System.out.println("=== All tests completed ===");
    }
}
