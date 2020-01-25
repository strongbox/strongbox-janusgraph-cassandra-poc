package org.carlspring.strongbox.janusgraph.cassandra;

import javax.annotation.PreDestroy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.service.CassandraDaemon;
import org.apache.cassandra.service.StorageService;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

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

    @PreDestroy
    public void destroy()
        throws IOException
    {
        deleteDirectory(DatabaseDescriptor.getRawConfig().commitlog_directory);
        deleteDirectory(DatabaseDescriptor.getRawConfig().hints_directory);
        deleteDirectory(DatabaseDescriptor.getRawConfig().saved_caches_directory);

        String[] dataDirectories = DatabaseDescriptor.getRawConfig().data_file_directories;
        for (String directory : dataDirectories)
        {
            deleteDirectory(directory);
        }
    }

    private void deleteDirectory(String dataDirectory)
        throws IOException
    {
        if (StringUtils.isEmpty(dataDirectory))
        {
            return;
        }

        Path rootPath = Paths.get(dataDirectory);
        try (Stream<Path> walk = Files.walk(rootPath))
        {
            walk.sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                // .peek(System.out::println)
                .forEach(File::delete);
        }
    }

}
