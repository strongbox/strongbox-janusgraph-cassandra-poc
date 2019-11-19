package org.carlspring.strongbox.janusgraph.reposiotries;

import org.janusgraph.core.JanusGraph;
import org.neo4j.ogm.session.SessionFactory;
import org.opencypher.gremlin.neo4j.ogm.JanusGraphDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;

@Configuration
@EnableNeo4jRepositories
public class RepositoriesConfig
{

    @Bean
    public SessionFactory sessionFactory(JanusGraph graph)
    {
        return new SessionFactory(new JanusGraphDriver(graph), "org.carlspring.strongbox.janusgraph.domain");
    }

    @Bean
    public Neo4jTransactionManager transactionManager(SessionFactory sessionFactory)
        throws Exception
    {
        return new Neo4jTransactionManager(sessionFactory);
    }

}
