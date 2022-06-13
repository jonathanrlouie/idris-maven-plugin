{ pkgs ? import (builtins.fetchTarball {
  name = "nixos-stable-2021-05-22";
  url = "https://github.com/nixos/nixpkgs/archive/12c5acf3760c8b88d0f2ae9578c4805da7866ed1.tar.gz";
  sha256 = "0cwicic3vr40wwlpzgb7p7brss2rgzpsvnq0lgyp7mmsixmxnm88";
}) {} }:
pkgs.mkShell {
  buildInputs = with pkgs; [
    jdk11
    maven
  ];
  idrisJvm = pkgs.fetchzip {
    url = "https://github.com/mmhelloworld/idris-jvm/releases/download/v0.5.1/idris2-0.5.1-SNAPSHOT.zip";
    sha256 = "105ni64cygc73nsy07rbk1c1blj60cx3ci6glya53zjp83hvxhmm";
  };
  shellHook = ''
    export PATH=$PATH:$idrisJvm/bin
    export IDRIS2_PREFIX=$idrisJvm/lib
  '';
}
