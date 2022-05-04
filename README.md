# Idris Maven Plugin

The idris-maven-plugin is used to compile and run Scala code in Maven.

## Supported Features

### Compile Mojo
- Compiles Idris applications to JVM bytecode

### Run Mojo
- Runs Idris applications
- Automatically downloads dependencies from Maven Central

## Building the Plugin

To build the plugin, you need JDK 8 or higher and Maven 3.8.1 or higher. Lower versions of Maven may work, but are not explicitly supported. You also will need the [idris-jvm backend](https://github.com/mmhelloworld/idris-jvm). See their documentation for more information on installing the backend.

## Running the Tests

From the root of the repository, run `mvn test`.

## Nix Environment

For Nix users, run `nix-develop` in the root of the repository to enter a developer environment, which already has the required versions of the JDK and Maven. It also automatically downloads and installs the [idris-jvm backend](https://github.com/mmhelloworld/idris-jvm).

## Contributing

TODO

## Planned Features

TODO
