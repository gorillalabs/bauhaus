# Build
in the project root directory, run:

```
podman build -t bauhaus-dev-env devenv/container/
```

# Run
in the project root directory, run:

```
devenv/container/start.sh
```

# Stop

```
podman stop bauhaus-repl
```



--

Running locally (not containerized)
If you want to stop the nREPL server and the Clojure-MCP server, use this

```
lsof -iTCP -sTCP:LISTEN -P -n | grep '7[08]80'
```

and kill the two processes (nrepl:7880 and mcp:7080)