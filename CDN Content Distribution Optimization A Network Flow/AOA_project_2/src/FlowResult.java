import java.util.*;

/**
 * Result object for min-cost max-flow computation
 */
public class FlowResult {
    public int totalFlow;
    public long totalCost;
    public boolean success;
    public long elapsedTimeMs;

    public FlowResult(int totalFlow, long totalCost, boolean success, long elapsedTimeMs) {
        this.totalFlow = totalFlow;
        this.totalCost = totalCost;
        this.success = success;
        this.elapsedTimeMs = elapsedTimeMs;
    }

    @Override
    public String toString() {
        return String.format("Flow: %d, Cost: %d, Success: %b, Time: %dms",
                totalFlow, totalCost, success, elapsedTimeMs);
    }
}
