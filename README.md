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

You will need to set the `IDRIS2_PREFIX` environment variable to make the base Idris2 libraries accessible.

By default, the plugin will automatically download the Idris2 compiler and the base libs to the local Maven Artifact Repository. The default artifact repository directory is in the `.m2` directory in the home directory.

On UNIX and Linux, that is `~/.m2/repository/io/github/mmhelloworld/idris-jvm-compiler/0.5.1/idris2-0.5.1/lib`

On Windows, that is `C:\Documents and Settings\UserName\.m2\repository\io\github\mmhelloworld\idris-jvm-compiler\0.5.1\idris2-0.5.1\lib`

For example, if you are on Linux with all of the Maven plugin's default settings, you should run `export IDRIS2_PREFIX=~/.m2/repository/io/github/mmhelloworld/idris-jvm-compiler/0.5.1/idris2-0.5.1/lib` from the command line to set the `IDRIS2_PREFIX` environment variable correctly.

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

For Nix users, `nix develop` in the root of the repository to enter a developer environment, which already has the required versions of the JDK and Maven. It also automatically downloads and installs the [idris-jvm backend](https://github.com/mmhelloworld/idris-jvm).

## Contributing
If you want to contribute to this project, please fork the repository and open a pull request. Your pull request will be automatically checked for linting and style.

## Planned Features
- Add a Help Mojo
- Support for compiling library code into JARs without needing to specify a main class
- Support downloading Idris dependencies through a user specified package manager (pack, sirdi, etc.)
- Allow running the compiler and Idris applications in a separate thread
- Support for mixed Java and Idris projects (Maybe? Depends on what is required to make this work.)

