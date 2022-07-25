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

/**
 * Goal that compiles Idris 2 code.
 */
@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE)
public class IdrisCompileMojo extends AbstractMojo {
    /**
     * The maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    /**
     * Name of the output file. Equivalent to
     * setting the -o flag of the Idris compiler.
     */
    @Parameter(defaultValue = "main", property = "outputFile")
    private String outputFile;

    /**
     * Location of build output directory relative to current directory. Equivalent
     * to setting the --output-dir option of the Idris compiler.
     */
    @Parameter(defaultValue = ".", property = "outputDir")
    private String outputDir;

    /**
     * The file with the main function. Equivalent to the primary argument given to
     * the Idris compiler. This is required for now until
     * https://github.com/idris-lang/Idris2/issues/475 is resolved.
     */
    @Parameter(defaultValue = "Main.idr", property = "mainFile")
    private File mainFile;

    /**
     * The name of the main class of the Idris compiler JAR.
     */
    @Parameter(required = false, property = "maven.idris.className")
    private String idrisClassName;

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
     * The Maven Session Object.
     */
    @Parameter(property = "session", required = true, readonly = true)
    private MavenSession session;

    /** Used to look up Artifacts in the remote repository. */
    @Component
    private RepositorySystem repositorySystem;

    public void execute() throws MojoExecutionException {
        try {
            JavaCommand cmd = new JavaCommand();
            cmd.addOption("-o", outputFile);
            cmd.addOption("--output-dir", outputDir);
            cmd.addArgs(mainFile.getAbsolutePath());
            ClassLoader cl = getCompilerClassLoader(idrisHome);
            String mainClassName = compilerMainClassName(idrisClassName);
            cmd.run(mainClassName, cl, getLog());
        } catch (Exception e) {
            throw new MojoExecutionException("Source error: " + e, e);
        }
    }

    private ClassLoader getCompilerClassLoader(String idrisHome) {
        if (idrisHome == null || idrisHome.isEmpty()) {
            return ClassLoaderUtils.getRemoteCompilerClassLoader(
                this.repositorySystem,
                this.session,
                this.idrisVersion);
        } else {
            return ClassLoaderUtils.getLocalCompilerClassLoader(idrisHome);
        }
    }

    private String compilerMainClassName(String override) {
        if (override == null || override.isEmpty()) {
            return "idris2.Main";
        } else {
            return override;
        }
    }
}
