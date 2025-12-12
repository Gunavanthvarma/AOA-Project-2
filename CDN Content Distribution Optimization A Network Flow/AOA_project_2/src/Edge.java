/**
 * Represents a directed edge in the network flow graph.
 * Each edge stores the original direction and capacity information.
 * Flow values can be positive (forward) or negative (reverse/cancellation).
 */
public class Edge {
    private final int u;           // source node
    private final int v;           // destination node
    private final int capacity;    // maximum forward capacity
    private final int cost;        // cost per unit forward flow
    private int flow;              // current flow (positive=forward, negative=reverse)

    /**
     * Construct an edge from u to v.
     * @param u source node
     * @param v destination node
     * @param capacity maximum forward capacity (must be >= 0)
     * @param cost cost per unit forward flow
     * @throws IllegalArgumentException if capacity < 0
     */
    public Edge(int u, int v, int capacity, int cost) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Edge capacity cannot be negative: " + capacity);
        }
        this.u = u;
        this.v = v;
        this.capacity = capacity;
        this.cost = cost;
        this.flow = 0;
    }

    // Getters
    public int getU() { return u; }
    public int getV() { return v; }
    public int getCapacity() { return capacity; }
    public int getCost() { return cost; }
    public int getFlow() { return flow; }

    /**
     * Get residual capacity for forward direction.
     * Returns how much additional forward flow can be sent.
     */
    public int residualCapacity() {
        return capacity - flow;
    }

    /**
     * Get cost for forward direction.
     */
    public int residualCost() {
        return cost;
    }

    /**
     * Get residual capacity for reverse direction (flow cancellation).
     * Returns how much flow can be reversed.
     */
    public int reverseResidualCapacity() {
        return flow;
    }

    /**
     * Get cost for reverse direction.
     * Negative of forward cost, representing cost to undo flow.
     */
    public int reverseResidualCost() {
        return -cost;
    }

    /**
     * Augment flow in forward direction.
     * @param amount positive amount to add to flow
     */
    public void augmentFlow(int amount) {
        this.flow += amount;
    }

    /**
     * Augment flow in reverse direction (cancellation).
     * @param amount positive amount to subtract from flow
     */
    public void cancelFlow(int amount) {
        this.flow -= amount;
    }

    @Override
    public String toString() {
        return String.format("Edge(%d->%d, cap=%d, cost=%d, flow=%d)",
                u, v, capacity, cost, flow);
    }
}
