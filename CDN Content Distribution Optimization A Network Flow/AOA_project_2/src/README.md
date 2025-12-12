# CDN Network Flow Problem - Java Implementation

## Overview
This project implements a solution to the minimum-cost content distribution problem in multi-layer CDNs using network flow algorithms.

## Quick Start

1. **Install Dependencies**
   ```bash
   # Java (check if installed)
   java -version
   
   # Python packages (for graph generation)
   pip3 install numpy matplotlib scipy
   ```

2. **Compile**
   ```bash
   cd src
   javac *.java
   ```

3. **Run Tests**
   ```bash
   java TestDriver
   ```

4. **Run Experiments**
   ```bash
   java ExperimentalValidator
   ```

5. **Generate Performance Graph**
   ```bash
   cd ..
   python3 generate_performance_graph.py
   ```

## Project Structure

```
AOA_project_2/
├── src/
│   ├── Edge.java                 # Edge representation
│   ├── FlowResult.java          # Result object for flow computation
│   ├── CDNNetworkFlow.java      # Main min-cost max-flow algorithm
│   ├── ExperimentalValidator.java # Experimental validation suite
│   ├── TestDriver.java          # Simple test driver
│   └── README.md                # This file
│
├── report/
│   ├── main.tex                 # LaTeX report document
│   ├── performance_graph.pdf    # Generated performance graph (PDF)
│   └── performance_graph.png    # Generated performance graph (PNG)
│
├── experiments/
│   └── results.json             # Experimental results
│
└── generate_performance_graph.py # Python script to generate performance graphs
```

## Dependencies

### Java Dependencies
- **Java Development Kit (JDK)**: Version 8 or higher
  - Check version: `java -version` and `javac -version`
  - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)

### Python Dependencies (for graph generation)
- **Python**: Version 3.6 or higher
  - Check version: `python3 --version`
- **Required Python packages**:
  - `numpy` - Numerical computing
  - `matplotlib` - Plotting library
  - `scipy` - Scientific computing (for curve fitting)

## Installation

### Install Java
```bash
# On macOS (using Homebrew)
brew install openjdk

# On Ubuntu/Debian
sudo apt-get update
sudo apt-get install openjdk-11-jdk

# On Windows
# Download and install from Oracle or use Chocolatey:
choco install openjdk
```

### Install Python Dependencies
```bash
# Install using pip
pip3 install numpy matplotlib scipy

# Or using conda
conda install numpy matplotlib scipy
```

## Compilation

### Compile All Java Files
```bash
cd src
javac *.java
```

This will create `.class` files for all Java source files in the `src` directory.

### Compile Individual Files (if needed)
```bash
cd src
javac Edge.java
javac FlowResult.java
javac CDNNetworkFlow.java
javac ExperimentalValidator.java
javac TestDriver.java
```

**Note:** Files must be compiled in dependency order, or simply compile all at once with `javac *.java`.

## Execution

### Run Simple Tests
```bash
cd src
java TestDriver
```

This runs three basic tests:
1. Small network with 5 nodes
2. CDN-style network with 8 nodes
3. Infeasible network (insufficient capacity)

**Expected Output:**
```
=== CDN Network Flow - Simple Test ===

Test 1: Small Network
...
Result: Flow: 20, Cost: 60, Success: true, Time: Xms

Test 2: CDN-style Network
...
Result: Flow: 70, Cost: 400, Success: true, Time: Xms

Test 3: Infeasible Network (insufficient capacity)
Result: Flow: 3, Cost: 6, Success: false, Time: Xms
Expected: Feasible=false, Flow=3 (limited by bottleneck)

=== All tests completed ===
```

### Run Full Experimental Validation

The `ExperimentalValidator` performs comprehensive testing across multiple network sizes and generates experimental data.

```bash
cd src
java ExperimentalValidator
```

**What it does:**
1. **Small-scale tests** (1 origin, 2 caches, 3 edges) - 5 iterations
2. **Medium-scale tests** (2 origins, 5 caches, 10 edges) - 5 iterations
3. **Large-scale tests** (5 origins, 15 caches, 50 edges) - 3 iterations
4. **Scalability tests** (varying network sizes from scale 1 to 5)

**Output:**
- Console output with timing results and summary
- Results saved to `../experiments/results.json`

**Example Output:**
```
============================================================
CDN Network Flow Algorithm - Experimental Validation
============================================================

[1/4] Running small scale test (1 origin, 2 caches, 3 edges)...
   Average time: 0.1500 ms

[2/4] Running medium scale test (2 origins, 5 caches, 10 edges)...
   Average time: 0.8500 ms

[3/4] Running large scale test (5 origins, 15 caches, 50 edges)...
   Average time: 12.4500 ms

[4/4] Running scalability test (varying network sizes)...
   Scale 1: 34 nodes, 0.0000 ms
   Scale 2: 50 nodes, 1.0000 ms
   ...

============================================================
SUMMARY
============================================================
Small Scale (avg): 0.1500 ms
Medium Scale (avg): 0.8500 ms
Large Scale (avg): 12.4500 ms

Scalability Analysis:
Nodes          Time (ms)    Flow    Cost
34             0.0000       733     3609
50             1.0000       1290    5303
...

Results saved to ../experiments/results.json
```

## Generate Performance Graph

The performance graph compares experimental running times with the theoretical O(VEF) complexity prediction.

### Prerequisites
1. Run `ExperimentalValidator` first to generate `experiments/results.json`
2. Install Python dependencies (see Dependencies section above)

### Generate Graph
```bash
# From project root directory
python3 generate_performance_graph.py
```

**What it does:**
- Reads experimental data from `experiments/results.json`
- Fits theoretical O(VEF) curve to experimental data
- Generates a 4-panel analysis graph:
  1. Time vs V×E×F (theoretical fit)
  2. Scalability: Time vs Network Size
  3. Residual analysis (goodness of fit)
  4. Performance summary table

**Output Files:**
- `report/performance_graph.pdf` - For LaTeX inclusion
- `report/performance_graph.png` - For preview

**Example Output:**
```
✓ Performance graph generated successfully!
  - Saved as: performance_graph.pdf (for LaTeX)
  - Saved as: performance_graph.png (for preview)

Fitted complexity constant: 0.00000950
Graph ready for insertion into LaTeX report.
```

### Troubleshooting Graph Generation

**Error: ModuleNotFoundError: No module named 'numpy'**
```bash
pip3 install numpy matplotlib scipy
```

**Error: FileNotFoundError: experiments/results.json**
- Make sure you've run `ExperimentalValidator` first
- Check that `experiments/results.json` exists

**Error: Permission denied**
- Ensure you have write permissions in the `report/` directory

## Algorithm Details

### Successive Shortest Paths

The implementation uses the **Successive Shortest Paths** algorithm for minimum cost maximum flow:

1. **Initialization**: Start with zero flow
2. **Iteration**: While flow < demand:
   - Find shortest path in residual graph using SPFA
   - Determine bottleneck capacity
   - Augment flow along path
   - Update residual capacities
3. **Termination**: Return total flow and cost

### Time Complexity

- **Per iteration**: O(VE) for SPFA (average case)
- **Number of iterations**: O(F) where F is total flow
- **Overall**: O(VEF) = O(V² × W) where W is max capacity

For typical CDN networks with bounded capacity, this is polynomial in input size.

### Network Construction

The CDN problem is modeled as a flow network:

```
Nodes:
- Super-source (s)
- Origin servers {o₁, o₂, ..., oₘ}
- Intermediate caches {c₁, c₂, ..., cₙ}
- Edge servers {e₁, e₂, ..., eₚ}
- Super-sink (t)

Edges:
- s → oᵢ: capacity = total demand, cost = 0
- oᵢ → cⱼ: capacity and cost from network
- cᵢ → cⱼ: capacity and cost from network
- cᵢ → eⱼ: capacity and cost from network
- eⱼ → t: capacity = demand[eⱼ], cost = 0
```

## API Usage

### Basic Usage

```java
// Create network with 5 nodes
CDNNetworkFlow network = new CDNNetworkFlow(5);

// Add edges: (source, dest, capacity, cost)
network.addEdge(0, 1, 10, 1);
network.addEdge(1, 2, 5, 2);
network.addEdge(2, 3, 10, 1);

// Solve: min-cost max-flow from node 0 to node 3 with demand 20
FlowResult result = network.minCostMaxFlow(0, 3, 20);

// Check result
System.out.println("Flow: " + result.totalFlow);
System.out.println("Cost: " + result.totalCost);
System.out.println("Success: " + result.success);
System.out.println("Time: " + result.elapsedTimeMs + "ms");
```

### CDN Network Construction

```java
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

// Solve
FlowResult result = cdnNet.minCostMaxFlow(0, 7, 70);
```

## Classes

### Edge
- Represents a directed edge in the network
- Stores capacity, cost, and current flow
- Methods:
  - `residualCapacity()`: Available capacity for forward flow
  - `residualCost()`: Cost for forward flow
  - `reverseResidualCapacity()`: Available capacity for flow cancellation
  - `reverseResidualCost()`: Cost for flow cancellation

### FlowResult
- Result container for min-cost max-flow computation
- Fields:
  - `totalFlow`: Total flow sent
  - `totalCost`: Total cost incurred
  - `success`: Whether demand was fully satisfied
  - `elapsedTimeMs`: Computation time in milliseconds

### CDNNetworkFlow
- Main algorithm implementation
- Methods:
  - `addEdge(u, v, capacity, cost)`: Add edge to network
  - `minCostMaxFlow(source, sink, demand)`: Solve min-cost max-flow
  - `getEdgeCount()`: Get number of edges
  - `printFlows()`: Print all non-zero flows

### ExperimentalValidator
- Experimental testing suite
- Tests networks of varying sizes
- Collects timing and quality metrics
- Saves results to JSON

## Experimental Results

### Small Scale (8 nodes)
- Average time: ~0.15 ms
- Tests: 5 iterations

### Medium Scale (18 nodes)
- Average time: ~0.85 ms
- Tests: 5 iterations

### Large Scale (72 nodes)
- Average time: ~12.45 ms
- Tests: 3 iterations

### Scalability
Tested with scales 1-5:
- Scale 1: 8 nodes
- Scale 2: 18 nodes
- Scale 3: 38 nodes
- Scale 4: 68 nodes
- Scale 5: 77 nodes

Runtime grows polynomially as O(n²) for typical CDN topologies.

## Report

Full mathematical derivation, proof of correctness, and experimental analysis in:
- `report/main.tex` - LaTeX source
- Compile with: `pdflatex main.tex`

## Additional Tools

### LaTeX Report Compilation
To compile the LaTeX report:
```bash
cd report
pdflatex main.tex
pdflatex main.tex  # Run twice for references
```

**Requirements:**
- LaTeX distribution: TexLive (Linux/macOS) or MiKTeX (Windows)
- Required packages: `acmart`, `algorithm`, `algorithmic`, `amsmath`, `graphicx`, `listings`

## Implementation Notes

1. **SPFA Algorithm**: Uses Shortest Path Faster Algorithm for shortest path finding
   - Better average case than Bellman-Ford
   - Uses queue for vertex management

2. **Residual Graph**: Implicitly maintained through edge flows
   - Forward edges: capacity - flow
   - Backward edges (cancellation): flow

3. **Random Seed**: Experimental validator uses fixed seed (42) for reproducibility

4. **Integer Arithmetic**: Uses int for flows/capacities, long for total cost to prevent overflow

## Correctness

The algorithm is correct because:
1. Successive shortest paths guarantee optimal cost
2. SPFA finds shortest paths in graphs with non-negative edge costs
3. Residual graph correctly represents available capacity
4. Flow conservation is maintained at all intermediate nodes

## License

Educational use - Algorithm Analysis and Design Course
