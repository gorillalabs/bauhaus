# Containerized Development Environment

This directory provides a containerized development environment for the Bauhaus Clojure project using Podman.

## Benefits of Containerized Development

### Environment Consistency
- **Reproducible Builds**: Every developer works with identical tool versions (Clojure 1.12.1, Java, etc.)
- **Eliminate "Works on My Machine"**: Container ensures consistent behavior across different host systems
- **Version Lock**: Pin specific versions of Clojure CLI tools and dependencies

### Isolation and Safety
- **Clean Environment**: Isolated from host system dependencies and configurations
- **No Tool Conflicts**: Multiple projects can use different tool versions without interference
- **Easy Cleanup**: Remove entire development environment by deleting container
- **Sandbox Testing**: Experiment with tools and configurations without affecting host

### Onboarding Efficiency
- **Fast Setup**: New team members get productive environment in minutes
- **Documentation as Code**: Container definition serves as executable documentation
- **Reduced Support**: Fewer environment-specific issues and setup problems

### Development Benefits
- **Integrated Services**: nREPL server (port 7880) and Clojure-MCP server (port 7080) pre-configured
- **Hot Reloading**: Code changes reflected immediately via volume mounts
- **Persistent Dependencies**: Maven cache mounted from host to speed up subsequent runs

## Setup Instructions

### Prerequisites

Install Podman on your system:

```bash
# macOS
brew install podman

# Linux (Ubuntu/Debian)
sudo apt update && sudo apt install podman

# Fedora/RHEL
sudo dnf install podman
```

Initialize Podman machine (macOS/Windows only):
```bash
podman machine init
podman machine start
```

### Build the Development Container

From the project root directory:

```bash
podman build -t bauhaus-dev-env devenv/container/
```

This creates a container with:
- Clojure CLI tools (version 1.12.1.1550)
- Java runtime environment
- Pre-configured for nREPL and MCP servers

### Run the Development Environment

From the project root directory:

```bash
devenv/container/start.sh
```

This script:
1. Mounts the project directory into the container (`/usr/bauhaus`)
2. Mounts Maven cache (`~/.m2`) for faster dependency resolution
3. Exposes ports 7080 (MCP) and 7880 (nREPL)
4. Starts both the nREPL server and Clojure-MCP server
5. Displays log output from both services

### Stop the Development Environment

```bash
podman stop bauhaus-repl
```

## Clojure-MCP Server Integration

The development environment includes a pre-configured Clojure Model Context Protocol (MCP) server that enables enhanced IDE integration and AI-assisted development.

### MCP Server Features
- **Port 7080**: HTTP Server-Sent Events (SSE) endpoint for MCP communication
- **Port 7880**: Standard nREPL server for REPL-driven development
- **Integrated Logging**: Centralized logging to `.logs/` directory

### MCP Configuration
Located in `devenv/mcp/clj-mcp/deps.edn`:
- Uses `com.bhauman/clojure-mcp` library
- Configured with Jetty server for HTTP transport
- Integrates with Bauhaus logging setup

### Adding New Tools to MCP

To extend the MCP server with additional tools:

1. **Add Dependencies**: Update `devenv/mcp/clj-mcp/deps.edn` with new tool dependencies
2. **Configure Tools**: Modify the MCP server configuration to include new tool definitions
3. **Rebuild Container**: Run the build command to include new dependencies
4. **Test Integration**: Verify tools work correctly via MCP protocol

Example of adding a new tool dependency:
```clojure
{:aliases
 {:mcp
  {:deps {org.gorillalabs.bauhaus/setup-logging {:local/root "../../../modules/setup/logging"}
          com.bhauman/clojure-mcp {:git/url "https://github.com/bhauman/clojure-mcp.git"
                                   :git/sha "0ca6ac5f51c8703489bd6706bde1b95521f7697c"}
          ;; Add new tool dependency here
          my.new/tool {:mvn/version "1.0.0"}}
   :exec-fn clojure-mcp.sse-main/start-sse-mcp-server
   :exec-args {:mcp-sse-port 7080
               :port 7880}}}}
```

## Development Workflow

### Typical Session
1. **Start Environment**: `devenv/container/start.sh`
2. **Connect IDE**: Point your editor to `localhost:7880` for nREPL
3. **Enable MCP**: Configure AI assistant to use `localhost:7080` for MCP
4. **Develop**: Edit code, test in REPL, leverage AI assistance
5. **Monitor Logs**: Watch `.logs/` files for service output
6. **Stop Environment**: `podman stop bauhaus-repl` when done

### File Structure
```
devenv/
├── README.md                 # This file
├── container/
│   ├── Containerfile        # Container definition
│   └── start.sh            # Container startup script
├── mcp/
│   └── clj-mcp/
│       └── deps.edn        # MCP server dependencies
└── start.sh                # Host startup script
```

### Log Monitoring
Development logs are written to:
- `.logs/applications-example-nrepl.out` - nREPL server output
- `.logs/mcp-sse.out` - MCP server output

## Troubleshooting

### Running Locally (Non-Containerized)

If you prefer to run without containers, manually start the services:

```bash
# Start nREPL server (in applications/example/)
cd applications/example
clojure -M:dev:test:container-nrepl

# Start MCP server (in devenv/mcp/clj-mcp/)
cd devenv/mcp/clj-mcp
clojure -X:mcp
```

### Stop Local Services
Find and kill running processes:
```bash
lsof -iTCP -sTCP:LISTEN -P -n | grep '7[08]80'
# Kill the processes for ports 7880 (nREPL) and 7080 (MCP)
```

### Common Issues
- **Port Conflicts**: Ensure ports 7080 and 7880 are not in use
- **Volume Mount Issues**: Check that the project directory path is correct
- **Maven Cache**: First run may be slow while downloading dependencies
- **Container Rebuild**: After changing dependencies, rebuild the container

## Extending the Environment

### Adding New Services
1. Update `Containerfile` to install new tools
2. Modify `start.sh` to launch additional services
3. Expose new ports in both container startup scripts
4. Update logging configuration as needed

### Customizing Configuration
- **Resource Limits**: Add memory/CPU limits to podman run command
- **Additional Volumes**: Mount extra directories for specific tools
- **Environment Variables**: Pass configuration via -e flags
- **Network Configuration**: Modify port bindings for different setups
