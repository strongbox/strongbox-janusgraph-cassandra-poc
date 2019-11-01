package org.carlspring.strongbox.janusgraph.cassandra;

import org.apache.cassandra.config.Config;
import org.apache.cassandra.config.ConfigurationLoader;
import org.apache.cassandra.exceptions.ConfigurationException;

public class CassandraEmbeddedPropertiesLoader implements ConfigurationLoader
{
    static final Config config = new Config();

    @Override
    public Config loadConfig()
        throws ConfigurationException
    {
        return config;
    }

}
