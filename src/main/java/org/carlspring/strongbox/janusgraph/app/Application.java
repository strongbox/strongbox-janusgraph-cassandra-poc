package org.carlspring.strongbox.janusgraph.app;

import org.carlspring.strongbox.janusgraph.cassandra.CassandraEmbeddedConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(CassandraEmbeddedConfig.class)
public class Application
{

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

}
