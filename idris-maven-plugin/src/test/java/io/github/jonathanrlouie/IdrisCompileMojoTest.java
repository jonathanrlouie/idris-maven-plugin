package io.github.jonathanrlouie;


import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;

import org.junit.Rule;
import static org.junit.Assert.*;
import org.junit.Test;
import java.io.File;

public class IdrisCompileMojoTest
{
    @Rule
    public MojoRule rule = new MojoRule()
    {
        @Override
        protected void before() throws Throwable 
        {
        }

        @Override
        protected void after()
        {
        }
    };

    /**
     * @throws Exception if any
     */
    @Test
    public void testCompile() throws Exception
    {
        File pom = new File("target/test-classes/project-to-test/");
        assertNotNull(pom);
        assertTrue(pom.exists());

        IdrisCompileMojo compileMojo = (IdrisCompileMojo) rule.lookupConfiguredMojo(pom, "compile");
        assertNotNull(compileMojo);
        compileMojo.execute();

	String outputPath = (String) rule.getVariableValueFromObject(compileMojo, "buildOutput");
        File outputDirectory = new File(outputPath + "_app");
        assertNotNull(outputDirectory);
        assertTrue(outputDirectory.exists());

        File outputJar = new File(outputDirectory, "output.jar");
        assertTrue(outputJar.exists());
    }
}

