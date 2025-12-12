# Experimental Validation Requirements Checklist

## What's Required for Maximum Marks (10 bonus points)

The assignment requires:
1. ✅ **Implementation** - Complete algorithm implementation
2. ✅ **Runtime Analysis** - Experimental verification of O(n log n) complexity
3. ✅ **Graphs/Plots** - Visual evidence showing the match between theoretical and empirical runtime
4. ✅ **Solution Quality** - Comparison with optimal solutions (for small instances)

## Expected Output

When you run `python bandwidth_allocation.py`, you should see:

### 1. Example Instance
- Small test case demonstrating the algorithm works

### 2. Runtime Analysis Section
```
============================================================
RUNTIME ANALYSIS
============================================================
n=  100: Average time = X.XXXXXX seconds (std: X.XXXXXX)
n=  500: Average time = X.XXXXXX seconds (std: X.XXXXXX)
n= 1000: Average time = X.XXXXXX seconds (std: X.XXXXXX)
n= 2000: Average time = X.XXXXXX seconds (std: X.XXXXXX)
n= 5000: Average time = X.XXXXXX seconds (std: X.XXXXXX)
n=10000: Average time = X.XXXXXX seconds (std: X.XXXXXX)
============================================================
```
- **All 6 n values** must be tested (100, 500, 1000, 2000, 5000, 10000)
- Each runs 10 trials and reports average
- Generates `runtime_analysis.png` showing log-log plot

### 3. Solution Quality Comparison Section
```
============================================================
SOLUTION QUALITY COMPARISON
============================================================
n      Optimal       Greedy       Ratio (%)
------------------------------------------------------------
20     XXXX.XX       XXXX.XX      XX.XX%
50     XXXX.XX       XXXX.XX      XX.XX%
100    XXXX.XX       XXXX.XX      XX.XX%
============================================================
```
- Compares greedy vs optimal (DP) for small instances
- Generates `solution_quality.png`
- **Prints LaTeX table format** you can copy to main.tex

## Files Generated

1. `runtime_analysis.png` - Log-log plot showing O(n log n) match
2. `solution_quality.png` - Bar/line plot showing greedy performance

## What Was Missing in Your Original Output

Your output only showed:
- n=2000, 5000, 10000 (missing 100, 500, 1000)
- No solution quality comparison
- No LaTeX table format

The improved code now:
- ✅ Tests ALL n values and prints them clearly
- ✅ Runs solution quality comparison
- ✅ Provides LaTeX table format for easy copy-paste
- ✅ Better formatted output with clear sections

## Next Steps

1. Run the improved code: `python bandwidth_allocation.py`
2. Verify all n values are tested
3. Copy the LaTeX table format into `main.tex` (if values differ)
4. Ensure both PNG files are generated
5. Upload to Overleaf with the PNG files

## Notes

- If n=100, 500, 1000 show very small times (< 0.0001s), that's normal - they're just very fast
- The DP solution for solution quality only works for small n (20, 50, 100) due to exponential complexity
- The runtime analysis uses the fractional greedy algorithm
- The solution quality uses the binary (0/1) greedy algorithm

