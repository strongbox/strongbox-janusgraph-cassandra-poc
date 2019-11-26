package org.carlspring.strongbox.janusgraph.reposiotries;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.app.Application;
import org.carlspring.strongbox.janusgraph.domain.Artifact;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinates;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinatesEntity;
import org.carlspring.strongbox.janusgraph.domain.ArtifactEntity;
import org.carlspring.strongbox.janusgraph.domain.ArtifactGroup;
import org.carlspring.strongbox.janusgraph.domain.Edges;
import org.carlspring.strongbox.janusgraph.domain.RepositoryArtifactIdGroup;
import org.carlspring.strongbox.janusgraph.domain.RepositoryArtifactIdGroupEntity;
import org.carlspring.strongbox.janusgraph.repositories.ArtifactCoordinatesRepository;
import org.carlspring.strongbox.janusgraph.repositories.ArtifactEntryRepository;
import org.carlspring.strongbox.janusgraph.repositories.RepositoryArtifactIdGroupRepository;
import org.janusgraph.core.JanusGraph;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.session.Session;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
public class RepositoryArtifactIdGroupRepositoryTest
{

    @Inject
    private JanusGraph janusGraph;

    @Inject
    private RepositoryArtifactIdGroupRepository repositoryArtifactIdGroupRepository;

    @Inject
    private ArtifactEntryRepository artifactEntryRepository;
    
    @Inject
    private ArtifactCoordinatesRepository artifactCoordinatesRepository;

    @Inject
    private Session session;
    
    @Test
    public void crudShouldWork()
    {
        ArtifactEntity artifactEntity = new ArtifactEntity();
        artifactEntity.setUuid(UUID.randomUUID().toString());
        artifactEntity.setUuid(UUID.randomUUID().toString());
        artifactEntity.setStorageId("storage0");
        artifactEntity.setRepositoryId("releases");
        artifactEntity.setSizeInBytes(123L);
        artifactEntity.setCreated(new Date());
        
        ArtifactCoordinatesEntity artifactCoordinatesEntity = new ArtifactCoordinatesEntity();
        artifactCoordinatesEntity.setPath("org/carlspring/repository-artifact-id-group-repository-test/crud.should.work");
        artifactCoordinatesEntity.setUuid(UUID.randomUUID().toString());
        artifactCoordinatesEntity.setVersion("1.2.3");
        artifactEntity.setArtifactCoordinates(artifactCoordinatesEntity);
        
        artifactEntity = artifactEntryRepository.save(artifactEntity);

        RepositoryArtifactIdGroupEntity groupEntity = new RepositoryArtifactIdGroupEntity();
        groupEntity.setUuid(UUID.randomUUID().toString());
        groupEntity.setGroupId("crudShouldWorkGroup");
        groupEntity.setStorageId("storage0");
        groupEntity.setRepositoryId("releases");
        groupEntity.setArtifacts(Collections.singleton(artifactEntity));

        groupEntity = repositoryArtifactIdGroupRepository.save(groupEntity);
        assertEquals(1, groupEntity.getArtifacts().size());
        assertEquals("crudShouldWorkGroup", groupEntity.getGroupId());
        
        artifactEntity = artifactEntryRepository.findByPath("org/carlspring/repository-artifact-id-group-repository-test/crud.should.work");
        assertEquals(artifactEntity.getUuid(), groupEntity.getArtifacts().iterator().next().getUuid());        
        
        //TODO: we need recursive `CypherQueryUtils.normalizeMatchByIdWithRelationResult` for depth more then one  
        //groupEntity = session.load(RepositoryArtifactIdGroupEntity.class, groupEntity.getUuid(), 3);
        
        //`deleteById` will begins with `findById` which  will fetch only one depth 
        repositoryArtifactIdGroupRepository.deleteById(groupEntity.getUuid());
        
        assertEquals(Optional.empty(), repositoryArtifactIdGroupRepository.findById(groupEntity.getUuid()));
        //TODO: this assertions should work when above will be fixed 
        //assertEquals(Optional.empty(), artifactEntryRepository.findById(artifactEntity.getUuid()));
        //assertEquals(Optional.empty(), artifactCoordinatesRepository.findById(artifactCoordinatesEntity.getUuid()));
    }

    @Test
    public void findArtifactsByGroupIdShouldWork()
    {
        GraphTraversalSource g = janusGraph.traversal();

        g.inject(1)
         .map(addArtifactGroup("findArtifactsByGroupIdShouldWorkGroup"))
         .addE(Edges.ARTIFACTGROUP_ARTIFACT)
         .to(addArtifact("org/carlspring/test/findArtifactsByGroupIdShouldWork.jar",
                         "1.2.7"))
         .outV()
         .addE(Edges.ARTIFACTGROUP_ARTIFACT)
         .to(addArtifact("org/carlspring/test/findArtifactsByGroupIdShouldWork.jar",
                         "1.2.8"))
         .outV()
         .addE(Edges.ARTIFACTGROUP_ARTIFACT)
         .to(addArtifact("org/carlspring/test/findArtifactsByGroupIdShouldWork.jar",
                         "1.2.9"))
         .next();

        g.inject(1)
         .map(addArtifactGroup("anotherGroup"))
         .addE(Edges.ARTIFACTGROUP_ARTIFACT)
         .to(addArtifact("org/carlspring/test/anotherGroup.jar",
                         "1.2.7"))
         .outV()
         .addE(Edges.ARTIFACTGROUP_ARTIFACT)
         .to(addArtifact("org/carlspring/test/anotherGroup.jar",
                         "1.2.8"))
         .next();

        g.tx().commit();

        List<ArtifactEntity> result = repositoryArtifactIdGroupRepository.findArtifactsByGroupId("storage0", "releases",
                                                                                                 "findArtifactsByGroupIdShouldWorkGroup");
        assertEquals(3, result.size());

        List<ArtifactEntity> anotherResult = repositoryArtifactIdGroupRepository.findArtifactsByGroupId("storage0",
                                                                                                        "releases",
                                                                                                        "anotherGroup");
        assertEquals(2, anotherResult.size());
    }

    protected GraphTraversal<Object, Vertex> addArtifactGroup(String groupId)
    {
        String artifactGroupUuid = UUID.randomUUID().toString();

        return __.addV(ArtifactGroup.LABEL)
                 .property("uuid",
                           artifactGroupUuid)
                 .property("groupId",
                           groupId)
                 .as("ag1")
                 .addV(RepositoryArtifactIdGroup.LABEL)
                 .property("uuid",
                           artifactGroupUuid)
                 .property("storageId",
                           "storage0")
                 .property("repositoryId",
                           "releases")
                 .addE(Edges.REPOSITORYARTIFACTIDGROUP_ARTIFACTGROUP)
                 .to("ag1")
                 .select("ag1");
    }

    protected GraphTraversal<Object, Vertex> addArtifact(String path,
                                                         String version)
    {
        String alias = "ae1";
        String coordinatesAslias = alias + ".coordinates";

        return __.addV(ArtifactCoordinates.LABEL)
                 .property("uuid", UUID.randomUUID().toString())
                 .property("path",
                           path)
                 .property("version",
                           version)
                 .as(coordinatesAslias)
                 .addV(Artifact.LABEL)
                 .property("uuid", UUID.randomUUID().toString())
                 .property("storageId", "storage0")
                 .property("repositoryId", "releases")
                 .as(alias)
                 .addE(Edges.ARTIFACT_ARTIFACTCOORDINATES)
                 .to(coordinatesAslias)
                 .select(alias);
    }

}
