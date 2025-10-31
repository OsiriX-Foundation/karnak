#!/usr/bin/env bash
# Download latest (or specified) dcm4che 5.x, unzip, and keep only native lib folder matching $ARC_OS.
# Usage:
#   get-dcm4che.sh [-v|--version 5.x.y] [-o|--output <dir>] [-a|--arc <os-arch>]
# Examples:
#   ARC_OS=linux-x86-64 ./get-dcm4che.sh -o target/dcm4che
#   ./get-dcm4che.sh -v 5.34.1 -o target/dcm4che -a linux-x86-64

set -euo pipefail

die() { echo "ERROR: $*" >&2; exit 1; }
log() { echo "[get-dcm4che] $*"; }

VERSION=""
OUTPUT_DIR=""
ARC="${ARC_OS:-}"
while [[ $# -gt 0 ]]; do
  case "$1" in
    -v|--version) VERSION="${2:-}"; shift 2;;
    -o|--output)  OUTPUT_DIR="${2:-}"; shift 2;;
    -a|--arc)     ARC="${2:-}"; shift 2;;
    -h|--help)
      echo "Usage: $0 [-v|--version 5.x.y] [-o|--output <dir>] [-a|--arc <os-arch>]"; exit 0;;
    *) die "Unknown argument: $1";;
  esac
done

# Tool checks
CURL_BIN="$(command -v curl || true)"
WGET_BIN="$(command -v wget || true)"
UNZIP_BIN="$(command -v unzip || true)"
[[ -z "$UNZIP_BIN" ]] && die "unzip is required"
[[ -z "$CURL_BIN" && -z "$WGET_BIN" ]] && die "curl or wget is required"

# Default output dir
if [[ -z "$OUTPUT_DIR" ]]; then
  OUTPUT_DIR="target/dcm4che"
fi
mkdir -p "$OUTPUT_DIR"

# If ARC not set, try to detect from system
if [[ -z "$ARC" ]]; then
  os="$(uname -s)"
  arch="$(uname -m)"
  case "$os" in
    Linux)   osPart="linux" ;;
    Darwin)  osPart="macosx" ;;
    MINGW*|MSYS*|CYGWIN*|Windows_NT) osPart="windows" ;;
    *) die "Unsupported OS for ARC detection: $os" ;;
  esac
  case "$arch" in
    x86_64|amd64) archPart="x86-64" ;;
    aarch64|arm64) archPart="aarch64" ;;
    *) die "Unsupported arch for ARC detection: $arch" ;;
  esac
  ARC="${osPart}-${archPart}"
  log "Auto-detected ARC_OS as '$ARC'"
fi

# Resolve latest version if not provided
if [[ -z "$VERSION" ]]; then
  INDEX_URL="https://sourceforge.net/projects/dcm4che/files/dcm4che3/"
  log "Resolving latest dcm4che 5.x from: $INDEX_URL"

  if [[ -n "$CURL_BIN" ]]; then
    html="$("$CURL_BIN" -fsSL "$INDEX_URL")"
  else
    html="$("$WGET_BIN" -qO- "$INDEX_URL")"
  fi

  # Extract version numbers like 5.x.y and pick the highest
  VERSION="$(printf "%s" "$html" \
    | grep -oE '/projects/dcm4che/files/dcm4che3/[0-9]+\.[0-9]+\.[0-9]+/?' \
    | grep -oE '[0-9]+\.[0-9]+\.[0-9]+' \
    | sort -V | uniq | tail -n1)"

  [[ -z "$VERSION" ]] && die "Unable to detect latest version from SourceForge index"
  log "Latest version detected: $VERSION"
else
  log "Using specified version: $VERSION"
fi

ZIP_NAME="dcm4che-${VERSION}-bin.zip"
ZIP_URL="https://sourceforge.net/projects/dcm4che/files/dcm4che3/${VERSION}/${ZIP_NAME}/download"
ZIP_PATH="${OUTPUT_DIR}/${ZIP_NAME}"

log "Downloading: $ZIP_URL"
if [[ -n "$CURL_BIN" ]]; then
  "$CURL_BIN" -fL --retry 3 -o "$ZIP_PATH" "$ZIP_URL"
else
  "$WGET_BIN" --tries=3 -O "$ZIP_PATH" "$ZIP_URL"
fi

[[ -s "$ZIP_PATH" ]] || die "Download failed or empty file: '$ZIP_PATH'"

log "Unzipping to: '$OUTPUT_DIR'"
"$UNZIP_BIN" -q -o "$ZIP_PATH" -d "$OUTPUT_DIR"

# Move contents up to OUTPUT_DIR
EXTRACTED_DIR="${OUTPUT_DIR}/dcm4che-${VERSION}"
[[ -d "$EXTRACTED_DIR" ]] || die "Lib directory not found at '$EXTRACTED_DIR'"

log "Moving contents from '$EXTRACTED_DIR' to '$OUTPUT_DIR'"
mv "$EXTRACTED_DIR"/* "$OUTPUT_DIR"/
rmdir "$EXTRACTED_DIR"

LIB_DIR="${OUTPUT_DIR}/lib"
[[ -d "$LIB_DIR" ]] || die "Lib directory not found at '$LIB_DIR'"

log "Pruning native lib folders in '$LIB_DIR', keeping only '$ARC'"
kept=0
shopt -s nullglob
for d in "$LIB_DIR"/*/; do
  bn="$(basename "$d")"
  if [[ "$bn" == "$ARC" ]]; then
    log "Keeping native folder: '$bn'"
    kept=1
  else
    log "Removing native folder: '$bn'"
    rm -rf "$d"
  fi
done
shopt -u nullglob

if [[ $kept -eq 0 ]]; then
  log "Warning: did not find a native folder matching '$ARC' under '$LIB_DIR'"
fi

# Remove the downloaded zip
log "Removing downloaded archive: '$ZIP_PATH'"
rm -f "$ZIP_PATH"

log "Done. Cleaned dcm4che at: ${LIB_DIR}"
