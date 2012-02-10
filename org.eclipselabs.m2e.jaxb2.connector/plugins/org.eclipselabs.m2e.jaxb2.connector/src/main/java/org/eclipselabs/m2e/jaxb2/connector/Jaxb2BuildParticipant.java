/**
 *
 */
package org.eclipselabs.m2e.jaxb2.connector;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.util.Scanner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 *
 */
public class Jaxb2BuildParticipant extends MojoExecutionBuildParticipant
{

    public Jaxb2BuildParticipant( MojoExecution execution )
    {
        super( execution, true );
    }

    @Override
    public Set<IProject> build( int kind, IProgressMonitor monitor ) throws Exception
    {
        final IMaven maven = MavenPlugin.getMaven();
        final BuildContext buildContext = getBuildContext();
        final MavenSession mavenSession = getSession();
        final MojoExecution mojoExecution = getMojoExecution();

        File generateDirectory = maven.getMojoParameterValue( mavenSession, mojoExecution, "generateDirectory", File.class );

        boolean filesModified = false;

        if ( generateDirectory != null && ( !generateDirectory.exists() || 
        		IncrementalProjectBuilder.CLEAN_BUILD == kind || IncrementalProjectBuilder.FULL_BUILD == kind ) )
        {
            filesModified = true;
        }
        else
        {
            filesModified = checkFilesModified( maven, buildContext, mavenSession, mojoExecution, "schemaDirectory", "schemaIncludes" ) ||
                            checkFilesModified( maven, buildContext, mavenSession,mojoExecution, "bindingsDirectory", "bindingIncludes" );
        }

        if ( !filesModified )
        {
            return null;
        }

        final Set<IProject> result = super.build( kind, monitor );

        if ( generateDirectory != null )
        {
            buildContext.refresh( generateDirectory );
        }

        return result;
    }

    public static <T> boolean isEmpty( final T[] array )
    {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty( String str )
    {
        return ( str == null || str.length() == 0 );
    }

    public static boolean checkFilesModified( IMaven maven, BuildContext buildContext,
                                              MavenSession mavenSession, MojoExecution mojoExecution,
                                              String mojoParamDir, String mojoParamDirIncludes ) throws Exception
    {
        File directory = maven.getMojoParameterValue( mavenSession, mojoExecution, mojoParamDir, File.class );

        if ( directory != null && directory.exists() && directory.isDirectory() )
        {
            @SuppressWarnings( "unchecked" )
            final List<String> directoryFiles =
                (List<String>)maven.getMojoParameterValue( mavenSession, mojoExecution, mojoParamDirIncludes, List.class );

            for ( String directoryFile : directoryFiles )
            {
                if ( ( !isEmpty( directoryFile ) &&
                       !isEmpty( getModifiedFiles( buildContext, new File( directory, directoryFile ) ) ) ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static String[] getModifiedFiles( BuildContext buildContext, File source )
        throws Exception
    {
        if ( buildContext == null || source == null || !source.exists() )
        {
            return null;
        }

        final Scanner ds = buildContext.newScanner( source );

        ds.scan();

        return ds.getIncludedFiles();
    }
}
