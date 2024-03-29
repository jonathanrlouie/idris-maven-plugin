package io.github.jonathanrlouie;


import org.apache.maven.plugin.testing.MojoRule;

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
    public void testCompileApp() throws Exception
    {
        File pom = new File("target/test-classes/project-to-test/");
        assertNotNull(pom);
        assertTrue(pom.exists());

        IdrisCompileMojo compileMojo = (IdrisCompileMojo) rule.lookupConfiguredMojo(pom, "compile");
        assertNotNull(compileMojo);
        compileMojo.execute();

	String outputFile = (String) rule.getVariableValueFromObject(compileMojo, "outputFile");
	String outputDir = (String) rule.getVariableValueFromObject(compileMojo, "outputDir");
        File jarOutputDirectory = new File(outputDir + outputFile + "_app");
        assertNotNull(jarOutputDirectory);
        assertTrue(jarOutputDirectory.exists());

        File outputJar = new File(jarOutputDirectory, "main.jar");
        assertTrue(outputJar.exists());
    }
}

