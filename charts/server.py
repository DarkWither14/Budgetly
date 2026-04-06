"""
Budgetly Chart Server
---------------------
A small Flask server that generates matplotlib charts and returns them as PNG images.
The Java backend (or frontend directly) POSTs data to this server to get chart images.

Run with:
    pip install -r charts/requirements.txt
    python charts/server.py

Listens on http://localhost:5050
"""

import io
import json
import matplotlib
matplotlib.use('Agg')  # non-interactive backend — no display needed
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker
from flask import Flask, request, send_file, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # allow requests from the browser frontend


# ---------------------------------------------------------------------------
# POST /trend
# Body: {
#   "labels":  ["Jan 2026", "Feb 2026", ...],
#   "values":  [520.00, 380.50, ...],
#   "color":   "#ef4444",
#   "title":   "Monthly Spending"
# }
# Returns: PNG image
# ---------------------------------------------------------------------------
@app.route('/trend', methods=['POST'])
def trend():
    data = request.get_json(force=True)
    labels = data.get('labels', [])
    values = data.get('values', [])
    color  = data.get('color', '#3b82f6')
    title  = data.get('title', 'Trend')

    fig, ax = plt.subplots(figsize=(11, 4))

    if labels and values:
        x = range(len(labels))
        bars = ax.bar(x, values, color=color, alpha=0.80, edgecolor=color, linewidth=0.8, width=0.55)

        # value labels on each bar
        for bar, val in zip(bars, values):
            if val > 0:
                ax.text(
                    bar.get_x() + bar.get_width() / 2,
                    bar.get_height() + max(values) * 0.01,
                    f'${val:,.0f}',
                    ha='center', va='bottom', fontsize=8, color='#374151'
                )

        ax.set_xticks(list(x))
        ax.set_xticklabels(labels, fontsize=9, color='#6b7280', rotation=20, ha='right')
    else:
        ax.text(0.5, 0.5, 'No data for this period',
                ha='center', va='center', fontsize=12, color='#9ca3af',
                transform=ax.transAxes)

    # y-axis dollar formatting
    ax.yaxis.set_major_formatter(mticker.FuncFormatter(lambda v, _: f'${v:,.0f}'))
    ax.tick_params(axis='y', labelsize=9, labelcolor='#6b7280')

    ax.set_title(title, fontsize=13, fontweight='bold', color='#111827', pad=12)
    ax.set_facecolor('#f9fafb')
    fig.patch.set_facecolor('#ffffff')
    ax.spines['top'].set_visible(False)
    ax.spines['right'].set_visible(False)
    ax.spines['left'].set_color('#e5e7eb')
    ax.spines['bottom'].set_color('#e5e7eb')
    ax.grid(axis='y', color='#e5e7eb', linewidth=0.8)
    ax.set_axisbelow(True)

    buf = io.BytesIO()
    plt.savefig(buf, format='png', bbox_inches='tight', dpi=150)
    buf.seek(0)
    plt.close(fig)

    return send_file(buf, mimetype='image/png')


# ---------------------------------------------------------------------------
# GET /health
# Simple health check so the frontend can detect if the server is running.
# ---------------------------------------------------------------------------
@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'ok'})


if __name__ == '__main__':
    print("Budgetly Chart Server running on http://localhost:5050")
    app.run(host='0.0.0.0', port=5050, debug=False)
