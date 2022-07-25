package io.github.jonathanrlouie;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;

public final class ClassLoaderUtils {
    private ClassLoaderUtils() { }

    /**
     * Gets the class loader for the Idris app with a user supplied JVM runtime.
     * @param project MavenProject of Idris app used to fetch dependencies.
     * @param appJar JAR file of the Idris app to run.
     * @param idrisHome Path to a local installation of the idris-jvm-runtime.
     * @return The class loader for the Idris app with a user supplied
     * JVM runtime.
     * @throws DependencyResolutionRequiredException if artifact file 
     * used but not resolved when getting app dependencies.
     */
    public static ClassLoader getLocalAppClassLoader(
        final MavenProject project,
        final File appJar,
        final String idrisHome)
        throws DependencyResolutionRequiredException {
        Stream<File> appDependencies = getAppDependencies(project);
        Stream<File> jars = getLocalCompilerJars(idrisHome);
        return getClassLoader(
            prependAppJar(appJar, Stream.concat(appDependencies, jars)));
    }

    /**
     * Gets the class loader for the user supplied Idris compiler.
     * @param idrisHome Path to a local installation of the idris-jvm-compiler.
     * @return The class loader for the user supplied Idris compiler.
     */
    public static ClassLoader getLocalCompilerClassLoader(
        final String idrisHome) {
        return getClassLoader(getLocalCompilerJars(idrisHome));
    }

    /**
     * Gets the class loader for the Idris app with a JVM runtime fetched 
     * from Maven Central.
     * @param repositorySystem RepositorySystem to resolve dependencies.
     * @param session MavenSession to resolve dependencies.
     * @param appJar JAR file of the Idris app to run.
     * @param project MavenProject of Idris app used to fetch dependencies.
     * @param version Version of the JVM runtime to fetch.
     * @return The class loader for the Idris app with a JVM runtime 
     * fetched from Maven Central
     * @throws DependencyResolutionRequiredException if artifact file 
     * used but not resolved when getting app dependencies.
     */
    public static ClassLoader getRemoteAppClassLoader(
        final RepositorySystem repositorySystem,
        final MavenSession session,
        final File appJar,
        final MavenProject project,
        final String version)
        throws DependencyResolutionRequiredException {
        Stream<File> appDependencies = getAppDependencies(project);
        Stream<File> jars = getRemoteArtifactJars(
            repositorySystem,
            session,
            "idris-jvm-runtime",
            version);
        return getClassLoader(
            prependAppJar(appJar, Stream.concat(appDependencies, jars)));
    }

    /**
     * Gets the class loader for the Idris compiler fetched from Maven Central.
     * @param repositorySystem RepositorySystem to resolve dependencies.
     * @param session MavenSession to resolve dependencies.
     * @param idrisVersion Version of the Idris compiler artifact to fetch.
     * @return The class loader for the Idris compiler fetched 
     * from Maven Central.
     */
    public static ClassLoader getRemoteCompilerClassLoader(
        final RepositorySystem repositorySystem,
        final MavenSession session,
        final String idrisVersion) {
        Stream<File> jars = getRemoteArtifactJars(
            repositorySystem,
            session,
            "idris-jvm-compiler",
            idrisVersion);
        return getClassLoader(jars);
    }

    private static Stream<File> getLocalCompilerJars(final String idrisHome) {
        File idrisHomeFile = new File(idrisHome);
        File[] idrisHomeFiles = idrisHomeFile.listFiles();
        if (idrisHomeFiles == null) {
            throw new RuntimeException("Either Idris home "
                + idrisHome + " was not a directory, or an I/O error occurred");
        }

        Set<File> dependencies = new TreeSet<>();
        for (File f : idrisHomeFiles) {
            String name = f.getName();
            if (name.endsWith(".jar")) {
                dependencies.add(f);
            }
        }
        return dependencies.stream();
    }

    private static Stream<File> getRemoteArtifactJars(
        final RepositorySystem repositorySystem,
        final MavenSession session,
        final String artifactId,
        final String version) {
        Artifact artifact = repositorySystem.createArtifact(
            "io.github.mmhelloworld",
            artifactId,
            version,
            "jar");
        Set<Artifact> resolvedArtifacts = resolveArtifacts(
            repositorySystem,
            session,
            artifact);
        if (resolvedArtifacts.size() == 0) {
            throw new RuntimeException(
                "No resolved artifacts found for " + artifactId);
        }

        return resolvedArtifacts.stream()
            .map(Artifact::getFile);
    }

    private static Set<Artifact> resolveArtifacts(
        final RepositorySystem repositorySystem,
        final MavenSession session,
        final Artifact artifact) {
        ArtifactResolutionRequest request = new ArtifactResolutionRequest()
            .setArtifact(artifact).setResolveRoot(true)
            .setResolveTransitively(true).setServers(
                session.getRequest().getServers())
            .setMirrors(session.getRequest().getMirrors())
            .setProxies(session.getRequest().getProxies())
            .setLocalRepository(session.getLocalRepository())
            .setRemoteRepositories(session.getCurrentProject()
                .getRemoteArtifactRepositories());
        return repositorySystem.resolve(request).getArtifacts();
    }

    private static Stream<File> getAppDependencies(final MavenProject project)
    throws DependencyResolutionRequiredException {
        return project.getTestClasspathElements()
            .stream()
            .map(File::new)
            .collect(Collectors.toSet())
            .stream();
    }

    private static Stream<File> prependAppJar(
        final File appJar, final Stream<File> jars) {
        if (appJar == null) {
            throw new RuntimeException(
                "No application jar found at appJar path");
        }

        // Make sure Application Jar is at beginning of classpath
        return Stream.concat(Stream.of(appJar), jars);
    }

    private static ClassLoader getClassLoader(final Stream<File> jars) {
        URL[] jarUrls = jars.map(file -> {
            try {
                return file.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(
                    "Failed to convert into url " + file, e);
            }
        }).toArray(URL[]::new);
        return new URLClassLoader(jarUrls, null);
    }
}
