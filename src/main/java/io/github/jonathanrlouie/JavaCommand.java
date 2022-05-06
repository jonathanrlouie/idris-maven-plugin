package io.github.jonathanrlouie;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import org.apache.maven.plugin.logging.Log;

public class JavaCommand {
    private List<String> args = new ArrayList();

    public void run(String mainClassName, ClassLoader cl, Log logger) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (cl == null) {
          cl = Thread.currentThread().getContextClassLoader();
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
            || !Modifier.isStatic(mods)
            || !Modifier.isPublic(mods)) {
          throw new NoSuchMethodException("main");
        }

        String[] argArray = this.args.toArray(new String[] {});

        // TODO - Redirect System.in System.err and System.out

        mainMethod.invoke(null, new Object[] {argArray});
    }

    public void addOption(String key, String value) {
        this.args.add(key);
        this.args.add(value);
    }

    public void addArgs(String... args1) {
	for (String arg : args1) {
	    this.args.add(arg);
	}
    }
}

