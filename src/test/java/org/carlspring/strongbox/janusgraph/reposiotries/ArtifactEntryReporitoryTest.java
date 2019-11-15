package org.carlspring.strongbox.janusgraph.reposiotries;

import static org.apache.tinkerpop.gremlin.process.traversal.P.eq;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.app.Application;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinates;
import org.carlspring.strongbox.janusgraph.domain.ArtifactEntry;
import org.janusgraph.core.JanusGraph;
import org.junit.jupiter.api.Test;
import org.opencypher.gremlin.translation.TranslationFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
public class ArtifactEntryReporitoryTest
{

    private static final Logger logger = LoggerFactory.getLogger(ArtifactEntryReporitoryTest.class);

    @Inject
    private ArtifactEntryRepository artifactEntryRepository;

    @Inject
    private JanusGraph janusGraph;

    @Test
    public void crudShouldWork()
    {
        ArtifactCoordinates artifactCoordinates = new ArtifactCoordinates();
        artifactCoordinates.setPath("org/carlspring/test-artifact-3.0.0.jar");
        artifactCoordinates.setUuid(UUID.randomUUID().toString());
        artifactCoordinates.setVersion("3.0.0");

        ArtifactEntry artifactEntry = new ArtifactEntry();
        artifactEntry.setUuid(UUID.randomUUID().toString());
        artifactEntry.setStorageId("storage0");
        artifactEntry.setRepositoryId("releases");
        artifactEntry.setSizeInBytes(123L);
        artifactEntry.setTags(new HashSet<>(Arrays.asList("release", "stabile")));
        artifactEntry.setArtifactCoordinates(artifactCoordinates);

        ArtifactEntry artifactEntrySaved = artifactEntryRepository.save(artifactEntry);
        assertEquals(artifactEntrySaved.getUuid(), artifactEntry.getUuid());

        artifactEntrySaved = artifactEntryRepository.findByPath("org/carlspring/test-artifact-3.0.0.jar");
        assertNotNull(artifactEntrySaved);
        assertEquals(artifactEntrySaved.getUuid(), artifactEntry.getUuid());
        assertNotNull(artifactEntrySaved.getArtifactCoordinates());
        assertNotNull(artifactEntrySaved.getTags());
        // TODO: fix the embedded set
        //assertTrue(artifactEntrySaved.getTags().contains("release"));
    }

    @Test
    void manyToOneRelationShouldWork()
    {
        String artifactCoordinatesUuid = UUID.randomUUID().toString();
        ArtifactCoordinates artifactCoordinates = new ArtifactCoordinates();
        artifactCoordinates.setPath("org/carlspring/test-artifact-4.0.0.jar");
        artifactCoordinates.setUuid(artifactCoordinatesUuid);
        artifactCoordinates.setVersion("4.0.0");

        String artifactEntryUuid = UUID.randomUUID().toString();
        ArtifactEntry artifactEntry = new ArtifactEntry();
        artifactEntry.setUuid(artifactEntryUuid);
        artifactEntry.setStorageId("storage0");
        artifactEntry.setRepositoryId("releases");
        artifactEntry.setSizeInBytes(123L);
        artifactEntry.setTags(new HashSet<>(Arrays.asList("release", "stabile")));
        artifactEntry.setArtifactCoordinates(artifactCoordinates);

        ArtifactEntry artifactEntrySaved = artifactEntryRepository.save(artifactEntry);
        assertEquals(artifactEntrySaved.getUuid(), artifactEntry.getUuid());

        GraphTraversalSource g = janusGraph.traversal();

        // Relation should exists
        GraphTraversal<Vertex, Edge> edgeQuery = g.V()
                                                  .hasLabel(ArtifactEntry.class.getSimpleName())
                                                  .has("uuid", artifactEntryUuid)
                                                  .outE();
        assertTrue(edgeQuery.hasNext());
        Edge artifactEntry2ArtifactCoordinatesEdge = edgeQuery.next();
        logger.info(String.valueOf(artifactEntry2ArtifactCoordinatesEdge));
        assertEquals(ArtifactEntry.class.getSimpleName() + "#" + ArtifactCoordinates.class.getSimpleName(),
                     artifactEntry2ArtifactCoordinatesEdge.label());

        // ArtufactCoordinates should exists
        GraphTraversal<Vertex, Vertex> vertexQuery = g.V()
                                                      .hasLabel(ArtifactEntry.class.getSimpleName())
                                                      .has("uuid", artifactEntryUuid)
                                                      .outE()
                                                      .inV();
        assertTrue(vertexQuery.hasNext());
        Vertex artifactCoordinatesVertex = vertexQuery.next();
        logger.info(String.valueOf(artifactCoordinatesVertex));
        assertEquals(artifactCoordinatesUuid, artifactCoordinatesVertex.property("uuid").value());
        assertEquals(ArtifactCoordinates.class.getSimpleName(), artifactCoordinatesVertex.label());

        // ArtufactCoordinates should still exists
        vertexQuery = g.V()
                       .hasLabel(ArtifactEntry.class.getSimpleName())
                       .outE()
                       .inV()
                       .hasLabel(ArtifactCoordinates.class.getSimpleName())
                       .has("path", eq("org/carlspring/test-artifact-4.0.0.jar"));
        assertTrue(vertexQuery.hasNext());
        Vertex artifactCoordinatesAnotherVertex = vertexQuery.next();
        assertEquals(artifactCoordinatesUuid, artifactCoordinatesVertex.property("uuid").value());
        assertEquals(ArtifactCoordinates.class.getSimpleName(), artifactCoordinatesAnotherVertex.label());
    }

    public static void main(String args[])
    {
        String cypher = "MATCH (ae:`ArtifactEntry`)-[`ArtifactEntry#ArtifactCoordinates`]->(ac:`ArtifactCoordinates` {path:'a/b/c'}) RETURN ae";
        TranslationFacade cfog = new TranslationFacade();
        System.out.println(cfog.toGremlinGroovy(cypher));
    }

}
