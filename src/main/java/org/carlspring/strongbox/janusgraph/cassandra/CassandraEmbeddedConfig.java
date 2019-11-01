package org.carlspring.strongbox.janusgraph.cassandra;

import java.io.IOException;

import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.service.CassandraDaemon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class CassandraEmbeddedConfig
{

    @Bean
    public CassandraDaemon cassandraDaemon(CassandraEmbeddedProperties properties)
        throws IOException
    {

        System.setProperty("cassandra.config.loader",
                           "org.carlspring.strongbox.janusgraph.cassandra.CassandraEmbeddedProperties");

        System.setProperty("cassandra-foreground", "true");
        System.setProperty("cassandra.native.epoll.enabled", "false");
        System.setProperty("cassandra.unsafesystem", "true");

        DatabaseDescriptor.daemonInitialization();

        CassandraDaemon cassandraDaemon = new CassandraDaemon();
        cassandraDaemon.activate();

        return cassandraDaemon;
    }

}
