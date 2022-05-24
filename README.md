# Idris Maven Plugin

The idris-maven-plugin is used to compile and run Scala code in Maven.

## Supported Features

### Compile Mojo
- Compiles Idris applications to JVM bytecode

### Run Mojo
- Runs Idris applications
- Automatically downloads dependencies from Maven Central

## Installing the Plugin

To install the plugin, clone the repository and run `mvn install` from the root of the repository.

## How to run the examples

To run the examples, run `mvn idris:run` from the root of the example project you want to run.

Note: Currently, the examples must be run from the Nix developer environment. To run them without this environment, you will need to change the example's `idris.home` property in its pom.xml file to point to a local copy of the idris-jvm backend lib folder.

## Building the Plugin

To build the plugin, you need JDK 8 or higher and Maven 3.8.1 or higher. Lower versions of Maven may work, but are not explicitly supported. You also will need the [idris-jvm backend](https://github.com/mmhelloworld/idris-jvm). See their documentation for more information on installing the backend.

## Running the Tests

From the root of the repository, run `mvn test`.

To run the functional tests, run the `run_functional_tests.sh` script in the root of the repository.

## Nix Environment

For Nix users, run `nix-develop` in the root of the repository to enter a developer environment, which already has the required versions of the JDK and Maven. It also automatically downloads and installs the [idris-jvm backend](https://github.com/mmhelloworld/idris-jvm).

## Contributing

TODO

## Planned Features

TODO
