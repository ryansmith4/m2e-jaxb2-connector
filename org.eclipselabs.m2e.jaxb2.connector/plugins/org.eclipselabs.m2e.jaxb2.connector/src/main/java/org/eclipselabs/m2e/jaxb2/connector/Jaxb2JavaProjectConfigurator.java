/**
 *
 */
package org.eclipselabs.m2e.jaxb2.connector;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.jdt.AbstractJavaProjectConfigurator;

/**
 *
 */
public class Jaxb2JavaProjectConfigurator extends AbstractJavaProjectConfigurator
{

    @Override
    protected String getOutputFolderParameterName()
    {
        return "sourceRoot";
    }

    @Override
    public AbstractBuildParticipant getBuildParticipant( IMavenProjectFacade projectFacade,
                                                         MojoExecution execution,
                                                         IPluginExecutionMetadata executionMetadata )
    {
        return new Jaxb2BuildParticipant( execution );
    }
}