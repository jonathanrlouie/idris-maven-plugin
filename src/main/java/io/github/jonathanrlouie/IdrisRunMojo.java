package io.github.jonathanrlouie;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

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
import java.util.Arrays;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

/**
 * Goal that runs Idris 2 code.
 */
@Mojo(name = "run", requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
@Execute(phase = LifecyclePhase.TEST_COMPILE)
public class IdrisRunMojo extends AbstractMojo {
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
     * Idris 2 version to use.
     */
    @Parameter(defaultValue = "0.5.1", property = "idris.version")
    private String idrisVersion;

    /**
     * Path to Idris installation to use instead of the artifact.
     */
    @Parameter(property = "idris.home")
    private String idrisHome;

    /**
     * Path to the compiled JAR file of the application.
     */
    @Parameter(defaultValue = "./main_app/main.jar", property = "appJar")
    private File appJar;

    /**
     * The Maven Session Object
     */
    @Parameter(property = "session", required = true, readonly = true)
    protected MavenSession session;

    /**
     * Used to look up Artifacts in the remote repository.
     */
    @Component
    RepositorySystem repositorySystem;

    public void execute() throws MojoExecutionException {
        try {
            JavaCommand cmd = new JavaCommand();
            ClassLoader cl = getAppClassLoader(idrisHome, getLog());

            if (mainClassName == null || mainClassName.isEmpty()) {
                throw new RuntimeException("mainClass property was not set.");
            }

            cmd.run(mainClassName, cl, getLog());
        } catch (Exception e) {
            throw new MojoExecutionException("Source error: " + e, e);
        }
    }

    private ClassLoader getAppClassLoader(String idrisHome, Log log) throws DependencyResolutionRequiredException {
        if (idrisHome == null || idrisHome.isEmpty()) {
            return getRemoteAppClassLoader(log);
        } else {
            return getLocalAppClassLoader(idrisHome, log);
        }
    }

    private ClassLoader getRemoteAppClassLoader(Log logger) throws DependencyResolutionRequiredException {
        Artifact artifact = this.repositorySystem.createArtifact("io.github.mmhelloworld", "idris-jvm-runtime", this.idrisVersion, "jar");
        Set<Artifact> resolvedArtifacts = this.resolve(artifact);
        if (resolvedArtifacts.size() == 0) {
            throw new RuntimeException("No resolved artifacts found for idris-jvm-runtime");
        }

        List<File> jars = resolvedArtifacts.stream().map(Artifact::getFile).collect(Collectors.toList());
        List<File> dependencies = project.getTestClasspathElements()
            .stream()
            .map(File::new)
            .collect(Collectors.toSet())
            .stream()
            .collect(Collectors.toList());

        jars.addAll(dependencies);
        if (appJar == null) {
            throw new RuntimeException("No application jar found at appJar path");
        }

        // Make sure Application Jar is at beginning of classpath
        jars.add(0, appJar);
        File[] depJars = jars.toArray(new File[] {});
        URL[] depJarUrls = Arrays.stream(depJars).map(file -> {
            try {
                return file.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException("failed to convert into url " + file, e);
            }
        }).toArray(URL[]::new);
        return new URLClassLoader(depJarUrls, null);
    }

    private Set<Artifact> resolve(Artifact artifact) {
        ArtifactResolutionRequest request = new ArtifactResolutionRequest().setArtifact(artifact).setResolveRoot(true)
                .setResolveTransitively(true).setServers(this.session.getRequest().getServers())
                .setMirrors(this.session.getRequest().getMirrors()).setProxies(this.session.getRequest().getProxies())
                .setLocalRepository(this.session.getLocalRepository())
                .setRemoteRepositories(this.session.getCurrentProject().getRemoteArtifactRepositories());
        return this.repositorySystem.resolve(request).getArtifacts();
    }

    private ClassLoader getLocalAppClassLoader(String idrisHome, Log logger) throws DependencyResolutionRequiredException {
        File idrisHomeFile = new File(idrisHome);
        File[] idrisHomeFiles = idrisHomeFile.listFiles();
        if (idrisHomeFiles == null) {
            throw new RuntimeException("Either Idris home " + idrisHome + " was not a directory, or an I/O error occurred");
        }

        Set<File> dependencies = project.getTestClasspathElements()
            .stream()
            .map(File::new)
            .collect(Collectors.toSet());

        for (File f : new File(idrisHome).listFiles()) {
            String name = f.getName();
            if (name.endsWith(".jar")) {
                dependencies.add(f);
            }
        }
        List<File> jars = dependencies.stream().collect(Collectors.toList());

        if (appJar == null) {
            throw new RuntimeException("No application jar found at appJar path");
        }

        // Make sure Application Jar is at beginning of classpath
        jars.add(0, appJar);
        File[] depJars = jars.toArray(new File[] {});
        URL[] depJarUrls = Arrays.stream(depJars).map(file -> {
            try {
                return file.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException("failed to convert into url " + file, e);
            }
        }).toArray(URL[]::new);
        return new URLClassLoader(depJarUrls, null);
    }
}
