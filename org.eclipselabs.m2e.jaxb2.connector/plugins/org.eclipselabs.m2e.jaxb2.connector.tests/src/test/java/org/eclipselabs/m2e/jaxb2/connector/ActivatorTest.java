package org.eclipselabs.m2e.jaxb2.connector;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ActivatorTest
{

    @Test
    public void thePluginShouldBeActivated()
    {
        assertNotNull( Activator.getDefault() );
    }
}
