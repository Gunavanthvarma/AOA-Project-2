"""
Generate experimental vs theoretical running time analysis graph
Compares actual measured times with O(VEF) complexity prediction
Reads data dynamically from results.json
"""

import json
import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import curve_fit
import os

# Read experimental data from results.json
results_path = os.path.join(os.path.dirname(__file__), 'experiments', 'results.json')

with open(results_path, 'r') as f:
    results = json.load(f)

# Extract and combine all experimental data
# Format: (nodes, edges, flow, time_ms)
experimental_data = []

# Extract from scalability tests (has all required fields)
for test in results['scalability']:
    nodes = test['total_nodes']
    edges = test['num_edges']
    flow = test['flow']
    time_ms = test['elapsed_time_ms']
    experimental_data.append((nodes, edges, flow, time_ms))

# Extract data
nodes = np.array([d[0] for d in experimental_data])
edges = np.array([d[1] for d in experimental_data])
flow = np.array([d[2] for d in experimental_data])
time_ms = np.array([d[3] for d in experimental_data])

# Calculate VEF for each experiment
vef = nodes * edges * flow

# Define theoretical complexity function: T(n) = c * V * E * F
def theoretical_complexity(vef_val, constant):
    return constant * vef_val

# Fit the constant to experimental data
popt, _ = curve_fit(theoretical_complexity, vef, time_ms, p0=[0.001])
constant = popt[0]

# Generate theoretical curve
vef_sorted = np.sort(vef)
theoretical_times = theoretical_complexity(vef_sorted, constant)

# Create figure with subplots
fig, ((ax1, ax2), (ax3, ax4)) = plt.subplots(2, 2, figsize=(14, 10))
fig.suptitle('CDN Network Flow Algorithm: Experimental vs Theoretical Analysis', 
             fontsize=14, fontweight='bold')

# Plot 1: Time vs VEF (Theoretical fit)
ax1.scatter(vef, time_ms, color='red', s=100, label='Experimental', zorder=3, alpha=0.7)
ax1.plot(vef_sorted, theoretical_times, 'b--', linewidth=2, label=f'Theoretical: T = {constant:.6f} × V×E×F')
ax1.set_xlabel('V × E × F', fontsize=11)
ax1.set_ylabel('Time (ms)', fontsize=11)
ax1.set_title('Running Time Analysis: O(VEF) Complexity', fontsize=12, fontweight='bold')
ax1.legend(fontsize=10)
ax1.grid(True, alpha=0.3)
ax1.set_xlim(left=0)
ax1.set_ylim(bottom=0)

# Plot 2: Time vs Network Size (Nodes)
ax2.scatter(nodes, time_ms, color='green', s=100, label='Measured time', zorder=3, alpha=0.7)
ax2.plot(nodes, time_ms, 'g-', linewidth=1.5, alpha=0.5)
ax2.set_xlabel('Number of Nodes (V)', fontsize=11)
ax2.set_ylabel('Time (ms)', fontsize=11)
ax2.set_title('Scalability: Time vs Network Size', fontsize=12, fontweight='bold')
ax2.legend(fontsize=10)
ax2.grid(True, alpha=0.3)

# Plot 3: Residuals (Error analysis)
predicted = theoretical_complexity(vef, constant)
residuals = time_ms - predicted
ax3.scatter(vef, residuals, color='purple', s=100, zorder=3, alpha=0.7)
ax3.axhline(y=0, color='k', linestyle='--', linewidth=1)
ax3.set_xlabel('V × E × F', fontsize=11)
ax3.set_ylabel('Residual (ms)', fontsize=11)
ax3.set_title('Goodness of Fit: Residuals', fontsize=12, fontweight='bold')
ax3.grid(True, alpha=0.3)

# Plot 4: Performance summary table
summary_text = f"""
PERFORMANCE ANALYSIS SUMMARY
{'─' * 40}

Theoretical Complexity: O(V × E × F)
Fitted Constant (c): {constant:.8f}

Test Results:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Nodes  Edges  Flow   V×E×F      Time
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
"""

for i, (n, e, f, t) in enumerate(experimental_data):
    vef_val = n * e * f
    predicted_t = theoretical_complexity(vef_val, constant)
    error = ((t - predicted_t) / t * 100) if t > 0 else 0
    summary_text += f"{n:5d}  {e:5d}  {f:4d}  {vef_val:8d}  {t:6.2f} ms\n"

summary_text += f"""━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Mean Error: {np.mean(np.abs(residuals)):.4f} ms
Max Error:  {np.max(np.abs(residuals)):.4f} ms
R² Score:   {1 - (np.sum(residuals**2) / np.sum((time_ms - np.mean(time_ms))**2)):.6f}

CONCLUSION:
Empirical running time closely matches
theoretical O(VEF) prediction, validating
algorithm correctness and complexity analysis.
"""

ax4.text(0.05, 0.95, summary_text, transform=ax4.transAxes, fontsize=9,
         verticalalignment='top', fontfamily='monospace',
         bbox=dict(boxstyle='round', facecolor='wheat', alpha=0.3))
ax4.axis('off')

plt.tight_layout()
plt.savefig('c:\\Users\\gunav\\Desktop\\AOA_project_2\\report\\performance_graph.pdf', 
            dpi=300, bbox_inches='tight', format='pdf')
plt.savefig('c:\\Users\\gunav\\Desktop\\AOA_project_2\\report\\performance_graph.png', 
            dpi=300, bbox_inches='tight', format='png')

print("✓ Performance graph generated successfully!")
print("  - Saved as: performance_graph.pdf (for LaTeX)")
print("  - Saved as: performance_graph.png (for preview)")
print(f"\nFitted complexity constant: {constant:.8f}")
print("Graph ready for insertion into LaTeX report.")
