#!/bin/bash

# Install Python dependencies if needed
pip install -r charts/requirements.txt -q

# Start the chart server in the background
echo "Starting chart server on port 5050..."
python3 charts/server.py &
CHART_PID=$!

# Start the frontend server in the background
echo "Starting frontend server on port 3000..."
cd frontend && python3 -m http.server 3000 &
FRONTEND_PID=$!

echo ""
echo "Budgetly is running!"
echo "  Frontend: http://localhost:3000"
echo "  Charts:   http://localhost:5050"
echo ""
echo "Press Ctrl+C to stop."

# Wait and clean up both servers on exit
trap "kill $CHART_PID $FRONTEND_PID 2>/dev/null" EXIT
wait
