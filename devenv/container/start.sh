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
PROJECT_DIR="$(dirname $(dirname $script_dir ))"

echo "Project directory: $PROJECT_DIR"

podman run \
  --rm \
  -v "$PROJECT_DIR:/usr/bauhaus/":Z \
  -v "$HOME/.m2:/root/.m2" \
  -p 127.0.0.1:7080:7080 \
  -p 127.0.0.1:7880:7880 \
  --name bauhaus-repl \
  -it \
  bauhaus-dev-env \
  /usr/bauhaus/devenv/start.sh
  