#!/usr/bin/env bash

get_script_dir()
{
    local SOURCE_PATH="${BASH_SOURCE[0]}"
    local SYMLINK_DIR
    local SCRIPT_DIR
    # Resolve symlinks recursively
    while [ -L "$SOURCE_PATH" ]; do
        # Get symlink directory
        SYMLINK_DIR="$( cd -P "$( dirname "$SOURCE_PATH" )" >/dev/null 2>&1 && pwd )"
        # Resolve symlink target (relative or absolute)
        SOURCE_PATH="$(readlink "$SOURCE_PATH")"
        # Check if candidate path is relative or absolute
        if [[ $SOURCE_PATH != /* ]]; then
            # Candidate path is relative, resolve to full path
            SOURCE_PATH=$SYMLINK_DIR/$SOURCE_PATH
        fi
    done
    # Get final script directory path from fully resolved source path
    SCRIPT_DIR="$(cd -P "$( dirname "$SOURCE_PATH" )" >/dev/null 2>&1 && pwd)"
    echo "$SCRIPT_DIR"
}

script_dir="$(get_script_dir)"
PROJECT_DIR="$(dirname $script_dir)"

echo "Project directory: $PROJECT_DIR"

# Create logs directory if it doesn't exist
mkdir -p ${PROJECT_DIR}/.logs
mv ${PROJECT_DIR}/.logs/applications-example-nrepl.out ${PROJECT_DIR}/.logs/applications-example-nrepl.out.bak 2>/dev/null || true
mv ${PROJECT_DIR}/.logs/mcp-sse.out ${PROJECT_DIR}/.logs/mcp-sse.out.bak 2>/dev/null || true

touch ${PROJECT_DIR}/.logs/applications-example-nrepl.out
touch ${PROJECT_DIR}/.logs/mcp-sse.out

tail -f ${PROJECT_DIR}/.logs/applications-example-nrepl.out ${PROJECT_DIR}/.logs/mcp-sse.out &

echo "Starting nREPL server for applications/example..."
( cd ${PROJECT_DIR}/applications/example && \
  nohup clojure -M:dev:test:container-nrepl >> ${PROJECT_DIR}/.logs/applications-example-nrepl.out 2>&1 & )
sleep 10
echo "Starting Clojure-MCP server..."
( cd ${PROJECT_DIR}/devenv/mcp/clj-mcp/ && \
  clojure -X:mcp >> ${PROJECT_DIR}/.logs/mcp-sse.out 2>&1)

echo "Happy hacking!"

