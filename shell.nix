{ pkgs ? import <nixpkgs> {} }:
pkgs.mkShell {
  buildInputs = with pkgs; [
    jdk11
    maven
  ];
  shellHook = ''
    export IDRIS2_PREFIX=lib
  '';
}
