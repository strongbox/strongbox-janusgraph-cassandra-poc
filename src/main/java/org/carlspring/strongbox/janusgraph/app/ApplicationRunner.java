package org.carlspring.strongbox.janusgraph.app;

import org.springframework.boot.SpringApplication;

public class ApplicationRunner
{

    public static void main(String[] args)
        throws Exception
    {
        Class.forName("org.carlspring.strongbox.janusgraph.cassandra.CassandraEmbeddedPropertiesLoader");

        SpringApplication.run(Application.class, args);
    }

}
