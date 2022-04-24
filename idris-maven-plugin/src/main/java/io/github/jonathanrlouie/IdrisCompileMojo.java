package io.github.jonathanrlouie;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
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
 * Goal that compiles Idris 2 code.
 */
@Mojo( name = "compile", defaultPhase = LifecyclePhase.COMPILE )
public class IdrisCompileMojo extends AbstractMojo
{
    /** 
     * The maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;

    /**
     * Location of build output relative to build output directory.
     * Equivalent to setting the -o flag of the Idris compiler.
     */
    @Parameter(defaultValue = "output", property = "buildOutput")
    private String buildOutput;

    /**
     * Location of build output directory relative to current directory.
     * Equivalent to setting the --output-dir option of the Idris compiler.
     */
    @Parameter(defaultValue = ".", property = "outputDir")
    private String outputDir;

    /**
     * The file with the main function.
     * Equivalent to the primary argument given to the Idris compiler.
     * This is required for now until https://github.com/idris-lang/Idris2/issues/475 is resolved.
     */
    @Parameter(defaultValue = "Main.idr", property = "mainFile")
    private File mainFile;

    /**
     * The name of the main class of the Idris compiler JAR.
     */
    @Parameter(required = false, property = "maven.idris.className")
    private String idrisClassName;

    /** 
     * Path to Idris installation to use instead of the artifact.
     */
    @Parameter(property = "idris.home")
    private String idrisHome;

    /**
     * The Maven Session Object
     */
    @Parameter(property = "session", required = true, readonly = true)
    protected MavenSession session;

    /** Used to look up Artifacts in the remote repository. */
    @Component RepositorySystem factory;

    public void execute()
        throws MojoExecutionException
    {
        try {
            JavaCommand cmd = new JavaCommand();
	    cmd.addOption("-o", buildOutput);
	    cmd.addOption("--output-dir", outputDir);
	    cmd.addArgs(mainFile.getAbsolutePath());
	    ClassLoader cl = getCompilerClassLoader(idrisHome);
            String mainClassName = compilerMainClassName(idrisClassName);
            cmd.run(mainClassName, cl, getLog());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ClassLoader getCompilerClassLoader(String idrisHome) throws Exception {
        if (idrisHome == null || idrisHome.isEmpty()) {
            // TODO: When Idris2 compiler jar is uploaded to Maven Central, change this to use getRemoteCompilerClassLoader()
	    throw new Exception("idris.home property is not specified in the POM file");
	} else {
            return getLocalCompilerClassLoader(idrisHome);
        }
    }

    private ClassLoader getLocalCompilerClassLoader(String idrisHome) throws Exception {
        Set<File> d = new TreeSet<>();
        File idrisHomeFile = new File(idrisHome);
	if (idrisHomeFile == null) {
            throw new Exception("Unable to find Idris home file " + idrisHome);
	}

	File[] idrisHomeFiles = idrisHomeFile.listFiles();
	if (idrisHomeFiles == null) {
	    throw new Exception("Either Idris home " + idrisHome + " was not a directory, or an I/O error occurred");
	}

        for (File f : idrisHomeFiles) {
          String name = f.getName();
          if (name.endsWith(".jar")) {
            d.add(f);
          }
        }
	File[] compilerJars = d.stream()
            .collect(Collectors.toList())
            .toArray(new File[] {});
	URL[] compilerJarUrls = Arrays.stream(compilerJars)
            .map(file -> {
		try {
                    return file.toURI().toURL();
                } catch (MalformedURLException e) {
                    throw new RuntimeException("failed to convert into url " + file, e);
		}
            })
	    .toArray(URL[]::new);
	return new URLClassLoader(compilerJarUrls, null);
    }

    private String compilerMainClassName(String override) {
        if (override == null || override.isEmpty()) {
            return "idris2.Main";
        } else {
            return override;
        }
    }
}
