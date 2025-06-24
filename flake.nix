{
  description = "A Nix-flake-based Node.js development environment";

inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };
  outputs = {
    self,
    nixpkgs,
    flake-utils,
  }:
    flake-utils.lib.eachDefaultSystem (system: let
      pkgs = import nixpkgs {inherit system;};
    in {
      devShell = with pkgs;
        mkShell {
          buildInputs = [
            jdk23
            clojure
            git
            nushell
          ];

        shellHook = ''
        echo "Clojure dev environment activated!"
        echo "Java version: $(java --version)"
        echo "Clojure CLI version: $(clj --version)"
        exec nu
      '';  
        };
    });
}