package org.carlspring.strongbox.janusgraph.reposiotries;

import static org.apache.tinkerpop.gremlin.process.traversal.P.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.app.Application;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinates;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinatesEntity;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversalSource;
import org.carlspring.strongbox.janusgraph.gremlin.projections.ArtifactCoordinatesProjection;
import org.carlspring.strongbox.janusgraph.repositories.ArtifactCoordinatesRepository;
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
    private ArtifactCoordinatesRepository artifactCoordinatesRepository; 
    
    @Inject
    private org.carlspring.strongbox.janusgraph.gremlin.repositories.ArtifactCoordinatesRepository gremlinArtifactCoordinatesRepository;
    
    @Test
    public void queriesShouldWork()
    {
        String uuid = createArtifactCoordinatesVertex();
        
        Driver driver = GremlinDatabase.driver(janusGraph.traversal());
        
        try (Session session = driver.session())
        {
            StatementResult result = session.run("MATCH (ac:ArtifactCoordinates { path:\"org/carlspring/test-artifact.jar\" }) RETURN ac");
            Node node = result.single().get(0).asNode();

            assertEquals("org/carlspring/test-artifact.jar", node.get("path").asString());
        }
        
        GraphTraversalSource g = janusGraph.traversal();
        
        Map<String, Object> vertex1 = g.V().hasLabel(ArtifactCoordinates.LABEL).has("path", eq("org/carlspring/test-artifact.jar")).project("ac").next();
        System.out.println(vertex1);
        
        Map<String, Object> vertex2 = g.V().has("path", eq("org/carlspring/test-artifact.jar")).hasLabel("ArtifactCoordinates").project("ac").next();
        System.out.println(vertex2);
    }

    protected String createArtifactCoordinatesVertex()
    {
        String uuid = UUID.randomUUID().toString();
        try (JanusGraphTransaction tx = janusGraph.newTransaction())
        {
            
            GraphTraversalSource g = tx.traversal();
            
            g.addV(ArtifactCoordinates.LABEL)
                                         .property("uuid", uuid)
                                         .property("path", "org/carlspring/test-artifact.jar")
                                         .property("version", "1.2.3")
                                         .next();
            tx.commit();
        }
        
        return uuid;
    }

    @Test
    public void crudShouldWork() {
        String uuid = UUID.randomUUID().toString();
        
        ArtifactCoordinatesEntity artifactCoordinates = new ArtifactCoordinatesEntity();
        artifactCoordinates.setPath("org/carlspring/test-artifact-1.0.0.jar");
        artifactCoordinates.setUuid(uuid);
        artifactCoordinates.setVersion("1.0.0");
        
        ArtifactCoordinatesEntity artifactCoordinatesSaved = artifactCoordinatesRepository.save(artifactCoordinates);
        assertEquals(artifactCoordinates.getUuid(), artifactCoordinatesSaved.getUuid());
        
        artifactCoordinates.setPath("org/carlspring/test-artifact-2.0.0.jar");
        artifactCoordinates.setUuid(uuid);
        artifactCoordinates.setVersion("2.0.0");
        artifactCoordinatesSaved = artifactCoordinatesRepository.save(artifactCoordinates);
        assertEquals(artifactCoordinates.getUuid(), artifactCoordinatesSaved.getUuid());        
        
        ArtifactCoordinatesEntity result = artifactCoordinatesRepository.findByPath("org/carlspring/test-artifact-1.0.0.jar");
        assertEquals("org/carlspring/test-artifact-1.0.0.jar", result.getPath());
        
        artifactCoordinates = gremlinArtifactCoordinatesRepository.findById(uuid);
        assertEquals(uuid, artifactCoordinates.getUuid());
    }
    
}
