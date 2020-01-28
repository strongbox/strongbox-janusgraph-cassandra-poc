package org.carlspring.strongbox.janusgraph.cassandra;

import java.io.IOException;

import org.apache.cassandra.service.CassandraDaemon;
import org.apache.cassandra.service.StorageService;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@ConfigurationPropertiesScan
public class CassandraEmbeddedConfig
{

    @Bean(destroyMethod = "deactivate")
    public CassandraDaemon activateCassandraDaemon(CassandraEmbeddedProperties properties)
        throws IOException,
        ClassNotFoundException
    {
        System.setProperty("cassandra.config.loader",
                           "org.carlspring.strongbox.janusgraph.cassandra.CassandraEmbeddedProperties$CassandraEmbeddedPropertiesLoader");

        System.setProperty("cassandra-foreground", "true");
        System.setProperty("cassandra.native.epoll.enabled", "false");
        System.setProperty("cassandra.unsafesystem", "true");

        CassandraDaemon cassandraDaemon = new CassandraDaemon(true);
        cassandraDaemon.activate();

        // Remove Cassandra StorageService shutdown hook to allow Spring context to shutdown in order
        StorageService.instance.removeShutdownHook();

        return cassandraDaemon;
    }

    @Bean(destroyMethod = "shutdown")
    public CassandraShutdown shutDownCassandra(CassandraDaemon cassandraDaemon)
    {
        return new CassandraShutdown(cassandraDaemon);
    }

}
