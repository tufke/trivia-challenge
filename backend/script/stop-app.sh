#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "Stopping trivia-challenge-service application..."

# Stop Spring Boot runs started via Maven.
if pgrep -f "spring-boot:run" > /dev/null; then
  echo "Found running Maven Spring Boot process, stopping..."
  pkill -f "spring-boot:run" || true
  echo "Application stopped successfully"
else
  echo "No running Spring Boot process found"
fi

# Stop only Java processes that look like this app (avoid matching shell scripts by path).
APP_PIDS="$(pgrep -f "java .*trivia-challenge-service" || true)"
if [[ -n "$APP_PIDS" ]]; then
  echo "Found running Java app process, cleaning up..."
  # shellcheck disable=SC2086
  kill $APP_PIDS || true
fi

echo "Done"
