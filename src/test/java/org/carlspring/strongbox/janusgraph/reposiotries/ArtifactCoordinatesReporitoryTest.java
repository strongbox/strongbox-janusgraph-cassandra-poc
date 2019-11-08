package org.carlspring.strongbox.janusgraph.reposiotries;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.app.Application;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinates;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphTransaction;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.opencypher.gremlin.neo4j.driver.GremlinDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
public class ArtifactCoordinatesReporitoryTest
{

    private static final Logger logger = LoggerFactory.getLogger(ArtifactCoordinatesReporitoryTest.class);

    @Inject
    private JanusGraph janusGraph;

    @Inject
    private Driver driver;
    
    @Test
    public void crudShouldWork()
    {
        GraphTraversalSource g = janusGraph.traversal();

        Vertex artifactCoordinatesVertex;
        try (JanusGraphTransaction tx = janusGraph.newTransaction())
        {
            artifactCoordinatesVertex = g.addV(ArtifactCoordinates.class.getSimpleName())
                                         .property("path", "org/carlspring/test-artifact.jar", "version", "1.2.3")
                                         .next();

            tx.commit();
        }

        try (Session session = driver.session())
        {

            StatementResult result = session.run("MATCH (ac:ArtifactCoordinates { path:\"org/carlspring/test-artifact.jar\" }) RETURN ac");
            Node node = result.single().get(0).asNode();

            assertEquals(artifactCoordinatesVertex.properties("path").next().value(), node.get("path").asString());
        }
    }

}
