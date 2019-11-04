package org.carlspring.strongbox.janusgraph.cassandra;

import org.apache.cassandra.service.EmbeddedCassandraService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


@Configuration
@ComponentScan
public class CassandraEmbeddedConfig
{
    @Bean
    public EmbeddedCassandraService embeddedCassandraService() throws IOException
    {
        System.setProperty( "cassandra.config.loader",
                "org.carlspring.strongbox.janusgraph.cassandra.CassandraEmbeddedProperties" );

        System.setProperty( "cassandra-foreground", "true" );
        System.setProperty( "cassandra.native.epoll.enabled", "false" );
        System.setProperty( "cassandra.unsafesystem", "true" );

        EmbeddedCassandraService cassandra = new EmbeddedCassandraService();
        cassandra.start();

        return cassandra;
    }

}
