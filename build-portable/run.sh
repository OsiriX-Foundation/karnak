#!/usr/bin/env bash
# Script to run dcm4che storescp listener and then start Karnak
# Usage: ./run.sh [OPTIONS]
#   --ae-title <title>       AE title for storescp (default: KARNAK-LOCAL)
#   --port <port>            Port for storescp (default: 11115)
#   --dicom-dir <path>       DICOM storage directory (default: ./dicom)
#   --filepath <pattern>     File path pattern (default: {00100010}/{00080060}/{0020000E}/{00080018}.dcm)
#   --force                  Force stop existing storescp instances
#   --help                   Show this help message

set -euo pipefail

log() { echo "[run.sh] $*"; }
die() { echo "ERROR: $*" >&2; exit 1; }

# Default values
AE_TITLE="KARNAK-LOCAL"
PORT="11115"
DICOM_DIR="./dicom"
FILEPATH_PATTERN='{00100010}/{00080060}/{0020000E}/{00080018}.dcm'
FORCE_STOP=false

# Parse arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    --ae-title)
      AE_TITLE="$2"
      shift 2
      ;;
    --port)
      PORT="$2"
      shift 2
      ;;
    --dicom-dir)
      DICOM_DIR="$2"
      shift 2
      ;;
    --filepath)
      FILEPATH_PATTERN="$2"
      shift 2
      ;;
    --force)
      FORCE_STOP=true
      shift
      ;;
    --help)
      grep "^#" "$0" | grep -E "Usage:|--" | sed 's/^# *//'
      exit 0
      ;;
    *)
      die "Unknown option: $1. Use --help for usage information."
      ;;
  esac
done

# Check for existing storescp processes
check_existing_storescp() {
  local pids
  if [[ "$(uname)" == "Darwin" ]]; then
    # macOS compatibility
    pids=$(pgrep -f "storescp" | xargs ps -p 2>/dev/null | grep ":${PORT}" | awk '{print $1}' | grep -v PID || true)
  else
    # Linux
    pids=$(pgrep -f "storescp.*:${PORT}" || true)
  fi

  if [[ -n "$pids" ]]; then
    log "Found existing storescp process(es) on port ${PORT}:"
    if [[ "$(uname)" == "Darwin" ]]; then
      # macOS: ps outputs header by default, skip it
      ps -p $pids -o pid,etime,command | tail -n +2 | while read -r line; do
        echo "  $line"
      done
    else
      # Linux: use --no-headers
      ps -p $pids -o pid,etime,cmd --no-headers | while read -r line; do
        echo "  $line"
      done
    fi

    if [[ "$FORCE_STOP" == true ]]; then
      log "Stopping existing storescp process(es)..."
      kill $pids 2>/dev/null || true
      sleep 2

      # Verify they're stopped
      local remaining=$(pgrep -f "storescp.*:${PORT}" || true)
      if [[ -n "$remaining" ]]; then
        log "Force killing remaining process(es)..."
        kill -9 $remaining 2>/dev/null || true
        sleep 1
      fi
      log "Existing storescp process(es) stopped"
    else
      echo ""
      read -p "Do you want to stop the existing storescp process(es)? (y/n): " -n 1 -r
      echo ""

      if [[ $REPLY =~ ^[Yy]$ ]]; then
        log "Stopping existing storescp process(es)..."
        kill $pids 2>/dev/null || true
        sleep 2

        # Verify they're stopped
        local remaining=$(pgrep -f "storescp.*:${PORT}" || true)
        if [[ -n "$remaining" ]]; then
          log "Force killing remaining process(es)..."
          kill -9 $remaining 2>/dev/null || true
          sleep 1
        fi
        log "Existing storescp process(es) stopped"
      else
        die "Cannot start storescp - port ${PORT} is already in use. Use --force to automatically stop existing processes."
      fi
    fi
  fi
}

# Set the application directory
APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_BIN="$APP_DIR/Karnak/bin"

# Add dcm4che bin directory to PATH
DCM4CHE_BIN="$APP_BIN/dcm4che/bin"
[[ ! -d "$DCM4CHE_BIN" ]] && die "dcm4che bin directory not found at '$DCM4CHE_BIN'"
export PATH="$DCM4CHE_BIN:$PATH"

# Check if storescp exists
STORESCP_BIN="$(command -v storescp || true)"
[[ -z "$STORESCP_BIN" ]] && die "storescp not found in PATH. Ensure dcm4che is installed."

# Check if Karnak executable exists
KARNAK_BIN="$APP_BIN/Karnak"
[[ ! -x "$KARNAK_BIN" ]] && die "Karnak executable not found at '$KARNAK_BIN'"

# Check for existing storescp processes
check_existing_storescp

# Create dicom directory if it doesn't exist
mkdir -p "$DICOM_DIR"
log "DICOM storage directory: '$DICOM_DIR'"

# Start storescp listener in background
log "Starting dcm4che storescp listener on ${AE_TITLE}:${PORT}"
storescp -b "${AE_TITLE}:${PORT}" \
  --directory "$DICOM_DIR" \
  --filepath "$FILEPATH_PATTERN" &

STORESCP_PID=$!
log "storescp started with PID: $STORESCP_PID"

# Give storescp a moment to initialize
sleep 2

# Verify storescp is running
if ! kill -0 "$STORESCP_PID" 2>/dev/null; then
  die "Failed to start storescp"
fi

# Trap to ensure cleanup on exit
# Improve the cleanup function
cleanup() {
  log "Shutting down services..."

  # Stop Karnak if running
  if [[ -n "${KARNAK_PID:-}" ]] && kill -0 "$KARNAK_PID" 2>/dev/null; then
    log "Stopping Karnak (PID: $KARNAK_PID)"
    kill "$KARNAK_PID" 2>/dev/null || true
    # Wait briefly for Karnak to stop
    sleep 1
  fi

  # Stop storescp
  if [[ -n "${STORESCP_PID:-}" ]]; then
    log "Shutting down storescp (PID: $STORESCP_PID)"

    # Try graceful shutdown first
    if kill "$STORESCP_PID" 2>/dev/null; then
      # Wait up to 5 seconds for graceful shutdown
      for i in {1..5}; do
        if ! kill -0 "$STORESCP_PID" 2>/dev/null; then
          log "storescp stopped gracefully"
          break
        fi
        sleep 1
      done

      # Force kill if still running
      if kill -0 "$STORESCP_PID" 2>/dev/null; then
        log "Force killing storescp..."
        kill -9 "$STORESCP_PID" 2>/dev/null || true
      fi
    fi
  fi

  # Double-check for any remaining storescp on the port
  local remaining
  if [[ "$(uname)" == "Darwin" ]]; then
    remaining=$(pgrep -f "storescp" | xargs ps -p 2>/dev/null | grep ":${PORT}" | awk '{print $1}' | grep -v PID || true)
  else
    remaining=$(pgrep -f "storescp" | xargs -r ps -p 2>/dev/null | grep ":${PORT}" | awk '{print $1}' || true)
  fi

  if [[ -n "$remaining" ]]; then
    log "Cleaning up remaining storescp process(es): $remaining"
    kill -9 $remaining 2>/dev/null || true
  fi

  log "Cleanup complete"
}

trap cleanup EXIT INT TERM

# Run Karnak
log "Starting Karnak from '$KARNAK_BIN'"
"$KARNAK_BIN" &
KARNAK_PID=$!

# Wait for Karnak to finish
wait $KARNAK_PID
