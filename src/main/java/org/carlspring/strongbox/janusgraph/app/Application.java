package org.carlspring.strongbox.janusgraph.app;

import org.carlspring.strongbox.janusgraph.cassandra.CassandraEmbeddedConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableConfigurationProperties
@Import(CassandraEmbeddedConfig.class)
public class Application
{

}
