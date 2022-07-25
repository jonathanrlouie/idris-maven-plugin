package io.github.jonathanrlouie;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import org.apache.maven.plugin.logging.Log;

public final class JavaCommand {
    /**
     * List of CLI arguments to the Java command.
     */
    private List<String> args = new ArrayList<String>();

    /**
     * Executes a Java program with given CLI arguments.
     *
     * @param mainClassName Name of class containing Main method to execute.
     * @param classLoader Classloader required to execute Java program.
     * @param logger Logger for debugging.
     * @throws ClassNotFoundException if main class cannot be loaded.
     * @throws NoSuchMethodException if main method cannot be found.
     * @throws IllegalAccessException if main method is inaccessible.
     * @throws InvocationTargetException if main method throws an exception.
     */
    public void run(
        final String mainClassName,
        final ClassLoader classLoader,
        final Log logger)
        throws ClassNotFoundException, NoSuchMethodException,
        IllegalAccessException, InvocationTargetException {
        ClassLoader cl;
        if (classLoader == null) {
            cl = Thread.currentThread().getContextClassLoader();
        } else {
            cl = classLoader;
        }

        if (this.args.isEmpty()) {
            logger.debug("empty args list");
        }

        for (String arg : this.args) {
            logger.debug("cmd arg: " + arg);
        }

        Class<?> mainClass = cl.loadClass(mainClassName);
        Method mainMethod = mainClass.getMethod("main", String[].class);
        int mods = mainMethod.getModifiers();
        if (mainMethod.getReturnType() != void.class
        || !Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
            throw new NoSuchMethodException("main");
        }

        String[] argArray = this.args.toArray(new String[] {});

        // TODO - Redirect System.in System.err and System.out

        mainMethod.invoke(null, new Object[] {argArray});
    }

    /**
     * Adds a CLI option to the Java command.
     * For example, option could be "-o" with arg "binaryName".
     * @param option CLI option.
     * @param arg Argument to CLI option.
     */
    public void addOption(final String option, final String arg) {
        this.args.add(option);
        this.args.add(arg);
    }

    /**
     * Adds a space separated list of arguments to the Java command.
     * @param args1 List of command arguments.
     */
    public void addArgs(final String... args1) {
        for (String arg : args1) {
            this.args.add(arg);
        }
    }
}
