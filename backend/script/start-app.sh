#!/usr/bin/env bash
set -euo pipefail

# this script does NOT use '-v' on 'docker compose down', so Docker volumes/data are kept.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$ROOT_DIR"

echo "=========================================="
echo "Stopping article-ingestion-service"
echo "=========================================="

bash "$SCRIPT_DIR/stop-app.sh"
sleep 2

echo
echo "=========================================="
echo "Starting trivia-challenge-service"
echo "=========================================="
echo
echo "Remote Java Debugger is enabled on port 5005"
echo
echo "To attach debugger in IntelliJ:"
echo "  1. Run -> Edit Configurations..."
echo "  2. + -> Remote JVM Debug"
echo "  3. Set Host: localhost, Port: 5005"
echo "  4. Click 'Debug' to connect"
echo

exec mvn spring-boot:run \
  "-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" \
  "-Dspring-boot.run.arguments=--spring.profiles.active=local"
