# Idris Maven Plugin
The idris-maven-plugin is used to compile and run Idris code in Maven. It is meant to be used with the [idris-jvm backend](https://github.com/mmhelloworld/idris-jvm).

## Supported Features

### Compile Mojo
- Compiles Idris applications to JVM bytecode
- Automatically downloads the idris-jvm compiler from Maven Central if it is not supplied

### Run Mojo
- Runs Idris applications
- Automatically downloads dependencies from Maven Central
- Automatically downloads the idris-jvm runtime from Maven Central if it is not supplied

## Creating a new Idris Maven project
To create a new Idris Maven project, you can use the [idris-maven-archetype](https://github.com/jonathanrlouie/idris-maven-archetype).

With the archetype plugin installed, run the following Maven command to create a new Idris Maven project from the archetype:
```
mvn archetype:generate                                  \
  -DarchetypeGroupId=io.github.jonathanrlouie           \
  -DarchetypeArtifactId=idris-maven-archetype           \
  -DarchetypeVersion=1.0-SNAPSHOT                       \
  -DgroupId=<my.groupid>                                \
  -DartifactId=<my-artifactId>
```

You should now be able to compile and run the project using `mvn idris:run` from the root of the newly created project.

## How to run the examples
To run the examples, run `mvn idris:run` from the root of the example project you want to run.

## Requirements for building the Plugin
Before building and installing the plugin, you will need JDK 11 or higher and Maven 3.8.1 or higher. Lower versions of Maven may work, but are not explicitly supported. You also will need the [idris-jvm backend](https://github.com/mmhelloworld/idris-jvm). See their documentation for more information on installing the backend.

## Installing the Plugin
To install the plugin, clone the repository and run `mvn install` from the root of the repository.

## Running the Tests
From the root of the repository, run `mvn test`.

To run the functional tests, run the `run_functional_tests.sh` script in the root of the repository.

## Nix Environment
Windows users should use WSL and install Nix before following the instructions below.

For Nix users, run `NIXPKGS_ALLOW_UNFREE=1 nix develop --impure` in the root of the repository to enter a developer environment, which already has the required versions of the JDK and Maven. It also automatically downloads and installs the [idris-jvm backend](https://github.com/mmhelloworld/idris-jvm). VSCode is included with a Java plugin installed for easy alignment on code style and formatting, hence the need for `NIXPKGS_ALLOW_UNFREE` and `--impure`.

## Contributing
TODO

## Planned Features
- Add a Help Mojo
- Support for compiling library code into JARs without needing to specify a main class
- Support downloading Idris dependencies through a user specified package manager (pack, sirdi, etc.)
- Allow running the compiler and Idris applications in a separate thread
- Support for mixed Java and Idris projects (Maybe? Depends on what is required to make this work.)

