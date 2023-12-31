{ pkgs ? import <nixpkgs> {} }:
pkgs.mkShell {
  buildInputs = with pkgs; [
    jdk11
    maven
  ];
  shellHook = ''
    export IDRIS2_PREFIX=~/.m2/repository/io/github/mmhelloworld/idris-jvm-compiler/0.5.1/idris2-0.5.1/lib
  '';
}
