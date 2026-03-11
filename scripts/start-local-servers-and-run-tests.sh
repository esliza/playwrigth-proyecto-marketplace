#!/usr/bin/env bash
set -euo pipefail

FRONTEND_PATH=${1:-"/srv/frontend/vite-project"}
BACKEND_PATH=${2:-"/srv/backend"}
FRONTEND_PORT=${3:-5130}
BACKEND_PORT=${4:-8080}
TIMEOUT=${5:-180}

function wait_for_port() {
  local port=$1; local timeout=$2; local start=$(date +%s)
  while true; do
    if nc -z localhost $port >/dev/null 2>&1; then
      echo "Port $port is open"
      return 0
    fi
    now=$(date +%s)
    if [ $((now - start)) -ge $timeout ]; then
      return 1
    fi
    sleep 2
  done
}

echo "Starting backend if path exists: $BACKEND_PATH"
if [ -d "$BACKEND_PATH" ]; then
  if [ -f "$BACKEND_PATH/package.json" ]; then
    (cd "$BACKEND_PATH" && npm ci && nohup npm run start >/dev/null 2>&1 &)
  elif [ -f "$BACKEND_PATH/pom.xml" ]; then
    (cd "$BACKEND_PATH" && nohup mvn spring-boot:run >/dev/null 2>&1 &)
  else
    echo "No start script found in $BACKEND_PATH"
  fi
else
  echo "Backend path not found: $BACKEND_PATH"
fi

echo "Starting frontend if path exists: $FRONTEND_PATH"
if [ -d "$FRONTEND_PATH" ]; then
  if [ -f "$FRONTEND_PATH/package.json" ]; then
    (cd "$FRONTEND_PATH" && npm ci && nohup npm run dev >/dev/null 2>&1 &)
  else
    echo "No package.json found in $FRONTEND_PATH"
  fi
else
  echo "Frontend path not found: $FRONTEND_PATH"
fi

echo "Waiting for services..."
wait_for_port $BACKEND_PORT $TIMEOUT || echo "Backend not ready within timeout"
wait_for_port $FRONTEND_PORT $TIMEOUT || echo "Frontend not ready within timeout"

BASE_URL="http://localhost:$FRONTEND_PORT"
echo "Running tests against $BASE_URL"
mvn -DbaseUrl=$BASE_URL -Dplaywright.headless=true -Dplaywright.enableRecording=false test
