package io.github.jonathanrlouie;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
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

/**
 * Goal that runs Idris 2 code.
 */
@Mojo(
    name = "run",
    requiresDependencyResolution = ResolutionScope.TEST,
    threadSafe = true)
@Execute(phase = LifecyclePhase.TEST_COMPILE)
public final class IdrisRunMojo extends AbstractMojo {
    /**
     * The maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

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
     * The Maven Session Object.
     */
    @Parameter(property = "session", required = true, readonly = true)
    private MavenSession session;

    /**
     * Used to look up Artifacts in the remote repository.
     */
    @Component
    private RepositorySystem repositorySystem;

    /**
     * The entrypoint of the Run Mojo.
     */
    public void execute() throws MojoExecutionException {
        try {
            JavaCommand cmd = new JavaCommand();
            ClassLoader cl = getAppClassLoader(idrisHome);

            if (mainClassName == null || mainClassName.isEmpty()) {
                throw new RuntimeException("mainClass property was not set.");
            }

            cmd.run(mainClassName, cl, getLog());
        } catch (Exception e) {
            throw new MojoExecutionException("Source error: " + e, e);
        }
    }

    private ClassLoader getAppClassLoader(final String idrHome)
        throws DependencyResolutionRequiredException {
        if (idrHome == null || idrHome.isEmpty()) {
            return ClassLoaderUtils.getRemoteAppClassLoader(
                this.repositorySystem,
                this.session,
                this.appJar,
                this.project,
                this.idrisVersion);
        } else {
            return ClassLoaderUtils.getLocalAppClassLoader(
                this.project,
                this.appJar,
                idrHome);
        }
    }
}
