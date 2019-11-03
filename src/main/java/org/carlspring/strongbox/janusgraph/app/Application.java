package org.carlspring.strongbox.janusgraph.app;

import org.carlspring.strongbox.janusgraph.cassandra.CassandraEmbeddedConfig;
import org.carlspring.strongbox.janusgraph.graph.JanusGraphConfig;
import org.carlspring.strongbox.janusgraph.graph.gremlin.server.GremlinServerConfig;
import org.carlspring.strongbox.janusgraph.reposiotries.RepositoriesConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableConfigurationProperties
@Import({ CassandraEmbeddedConfig.class,
          JanusGraphConfig.class,
          GremlinServerConfig.class,
          RepositoriesConfig.class })
public class Application
{

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

}
