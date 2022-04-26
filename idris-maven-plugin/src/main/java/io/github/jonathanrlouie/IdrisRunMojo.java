package io.github.jonathanrlouie;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.repository.RepositorySystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

/**
 * Goal that runs Idris 2 code.
 */
@Mojo( name = "run", requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true )
@Execute(phase = LifecyclePhase.TEST_COMPILE)
public class IdrisRunMojo extends AbstractMojo
{
    /** 
     * The maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;

    /**
     * Class name of the Idris application to execute.
     */
    @Parameter(property = "mainClass")
    private String mainClassName;

    /** 
     * Path to Idris installation to use instead of the artifact.
     */
    @Parameter(property = "idris.home")
    private String idrisHome;

    /** 
     * Path to the compiled JAR file of the application.
     */
    @Parameter(defaultValue = "./output_app/output.jar", property = "appJar")
    private File appJar;

    /**
     * The Maven Session Object
     */
    @Parameter(property = "session", required = true, readonly = true)
    protected MavenSession session;

    /**
     * Used to look up Artifacts in the remote repository.
     */
    @Component RepositorySystem factory;

    public void execute()
        throws MojoExecutionException
    {
        try {
            JavaCommand cmd = new JavaCommand();
	    ClassLoader cl = getAppClassLoader(idrisHome);
            cmd.run(mainClassName, cl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ClassLoader getAppClassLoader(String idrisHome) throws Exception {
        Set<File> d = project.getTestClasspathElements().stream().map(File::new).collect(Collectors.toSet());
        for (File f : new File(idrisHome).listFiles()) {
          String name = f.getName();
          if (name.endsWith(".jar")) {
            d.add(f);
          }
        }
	List<File> f = d.stream()
            .collect(Collectors.toList());
	f.add(appJar);
	File[] depJars = f.toArray(new File[] {});
	URL[] depJarUrls = Arrays.stream(depJars)
            .map(file -> {
		try {
                    return file.toURI().toURL();
                } catch (MalformedURLException e) {
                    throw new RuntimeException("failed to convert into url " + file, e);
		}
            })
	    .toArray(URL[]::new);
	return new URLClassLoader(depJarUrls, null);
    }
}

