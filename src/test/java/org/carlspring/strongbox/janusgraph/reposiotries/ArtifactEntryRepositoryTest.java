package org.carlspring.strongbox.janusgraph.reposiotries;

import static org.apache.tinkerpop.gremlin.process.traversal.P.eq;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.app.Application;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinates;
import org.carlspring.strongbox.janusgraph.domain.ArtifactDependency;
import org.carlspring.strongbox.janusgraph.domain.ArtifactEntry;
import org.janusgraph.core.JanusGraph;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
public class ArtifactEntryRepositoryTest
{

    private static final Logger logger = LoggerFactory.getLogger(ArtifactEntryRepositoryTest.class);

    @Inject
    private ArtifactEntryRepository artifactEntryRepository;

    @Inject
    private JanusGraph janusGraph;

    @Inject
    private SessionFactory sessionFactory;
    
    @Test
    public void crudShouldWork()
    {
        ArtifactCoordinates artifactCoordinates = new ArtifactCoordinates();
        artifactCoordinates.setPath("org/carlspring/test-artifact-3.0.0.jar");
        artifactCoordinates.setUuid(UUID.randomUUID().toString());
        artifactCoordinates.setVersion("3.0.0");

        Date createdOn = new Date();
        ArtifactEntry artifactEntry = new ArtifactEntry();
        artifactEntry.setUuid(UUID.randomUUID().toString());
        artifactEntry.setStorageId("storage0");
        artifactEntry.setRepositoryId("releases");
        artifactEntry.setSizeInBytes(123L);
        artifactEntry.setCreated(createdOn);
        artifactEntry.setTags(new HashSet<>(Arrays.asList("release", "stabile")));
        artifactEntry.setArtifactCoordinates(artifactCoordinates);

        ArtifactEntry artifactEntrySaved = artifactEntryRepository.save(artifactEntry);
        assertEquals(artifactEntrySaved.getUuid(), artifactEntry.getUuid());

        artifactEntrySaved = artifactEntryRepository.findByPath("org/carlspring/test-artifact-3.0.0.jar");
        assertNotNull(artifactEntrySaved);
        assertEquals(artifactEntrySaved.getUuid(), artifactEntry.getUuid());
        assertNotNull(artifactEntrySaved.getArtifactCoordinates());
        assertEquals(createdOn, artifactEntrySaved.getCreated());
        Set<String> tags = artifactEntrySaved.getTags();
        assertNotNull(tags);
        assertEquals(2, tags.size());
        assertTrue(tags.contains("release"));
        assertTrue(tags.contains("stabile"));
    }

    @Test
    public void manyToOneRelationShouldWork()
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
        artifactEntry.setCreated(new Date());
        
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

        // ArtifactCoordinates should exists
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

        // ArtifactCoordinates should still exists
        vertexQuery = g.V()
                       .hasLabel(ArtifactCoordinates.class.getSimpleName())
                       .has("path", eq("org/carlspring/test-artifact-4.0.0.jar"))
                       .inE()
                       .outV()
                       .hasLabel(ArtifactEntry.class.getSimpleName());
        assertTrue(vertexQuery.hasNext());
        Vertex artifactEntryAnotherVertex = vertexQuery.next();
        assertEquals(artifactCoordinatesUuid, artifactCoordinatesVertex.property("uuid").value());
        assertEquals(ArtifactEntry.class.getSimpleName(), artifactEntryAnotherVertex.label());
    }
    
    @Test
    public void artifactDependencyTreeShouldWork() {
        String subjectUuid = UUID.randomUUID().toString();
        
        ArtifactEntry artifactEntrySubject = new ArtifactEntry();
        artifactEntrySubject.setUuid(subjectUuid);
        artifactEntrySubject.setStorageId("storage0");
        artifactEntrySubject.setRepositoryId("releases");
        artifactEntrySubject.setSizeInBytes(123L);
        artifactEntrySubject.setTags(new HashSet<>(Arrays.asList("release", "stabile")));
        //artifactEntry.setArtifactCoordinates(artifactCoordinates);
        artifactEntrySubject.setCreated(new Date());

        String dependencyUuid = UUID.randomUUID().toString();
        ArtifactEntry artifactEntryDependency = new ArtifactEntry();
        artifactEntryDependency.setUuid(dependencyUuid);
        artifactEntryDependency.setStorageId("storage0");
        artifactEntryDependency.setRepositoryId("releases");
        artifactEntryDependency.setSizeInBytes(123L);
        artifactEntryDependency.setTags(new HashSet<>(Arrays.asList("release", "stabile")));
        //artifactEntry.setArtifactCoordinates(artifactCoordinates);
        artifactEntryDependency.setCreated(new Date());
        
        ArtifactDependency artifactDependency = new ArtifactDependency();
        artifactDependency.setUuid(UUID.randomUUID().toString());
        artifactDependency.setSubject(artifactEntrySubject);
        artifactDependency.setDependency(artifactEntryDependency);
        
        Session session = sessionFactory.openSession();
        session.save(artifactEntrySubject);
        session.save(artifactEntryDependency);
        session.save(artifactDependency);

        List<ArtifactEntry> dependencies = artifactEntryRepository.findAllDependentArtifactEntries(dependencyUuid);
        assertEquals(1, dependencies.size());
    }

}
