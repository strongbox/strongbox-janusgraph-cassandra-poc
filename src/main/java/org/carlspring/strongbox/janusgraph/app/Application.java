package org.carlspring.strongbox.janusgraph.app;

import org.carlspring.strongbox.janusgraph.cassandra.CassandraEmbeddedConfig;
import org.carlspring.strongbox.janusgraph.graph.JanusGraphConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableConfigurationProperties
@Import({ CassandraEmbeddedConfig.class, JanusGraphConfig.class })
public class Application
{

    public static void main(String[] args)
        throws Exception
    {
        SpringApplication.run(Application.class, args);
    }

}
