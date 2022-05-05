{ pkgs ? import <nixpkgs> {} }:
pkgs.mkShell {
  buildInputs = with pkgs; [ jdk8 wget unzip maven ];
  idrisJvm = pkgs.fetchzip {
    url = "https://github.com/mmhelloworld/idris-jvm/releases/download/v0.5.1/idris2-0.5.1-SNAPSHOT.zip";
    sha256 = "105ni64cygc73nsy07rbk1c1blj60cx3ci6glya53zjp83hvxhmm";
  };
  shellHook = ''
    export PATH=$PATH:$idrisJvm/bin
    export IDRIS2_PREFIX=$idrisJvm/lib
    cp -r $IDRIS2_PREFIX idris-maven-plugin/
  '';
}
