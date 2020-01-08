package org.carlspring.strongbox.janusgraph.gremlin.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.carlspring.strongbox.janusgraph.app.Application;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinatesEntity;
import org.carlspring.strongbox.janusgraph.domain.ArtifactEntity;
import org.carlspring.strongbox.janusgraph.domain.ArtifactGroupEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
public class GremlinArtifactGroupRepositoryTest
{

    @Inject
    @Qualifier("gremlinArtifactGroupRepository")
    private ArtifactGroupRepository gremlinArtifactGroupRepository;

    @Inject
    @Qualifier("gremlinArtifactRepository")
    private ArtifactRepository gremlinArtifactRepository;

    @Inject
    @Qualifier("gremlinArtifactCoordinatesRepository")
    private ArtifactCoordinatesRepository gremlinArtifactCoordinatesRepository;

    @Test
    public void crudShouldWork()
    {
        ArtifactCoordinatesEntity artifactCoordinatesEntity = new ArtifactCoordinatesEntity();
        artifactCoordinatesEntity.setPath("org/carlspring/artifact-gagrt-csw-1.2.3.jar");
        artifactCoordinatesEntity.setVersion("1.2.3");

        ArtifactEntity artifactEntity = new ArtifactEntity();
        artifactEntity.setStorageId("storage0");
        artifactEntity.setRepositoryId("releases");
        artifactEntity.setArtifactCoordinates(artifactCoordinatesEntity);

        ArtifactGroupEntity artifactGroupEntity = new ArtifactGroupEntity();
        artifactGroupEntity.setGroupId("org/carlspring/artifact-gagrt-csw");
        artifactGroupEntity.setArtifacts(new HashSet<>(Arrays.asList(new ArtifactEntity[] { artifactEntity })));

        // Create
        artifactGroupEntity = gremlinArtifactGroupRepository.save(artifactGroupEntity);
        assertNotNull(artifactGroupEntity);
        assertNotNull(artifactGroupEntity.getUuid());
        assertEquals(1, artifactGroupEntity.getArtifacts().size());

        ArtifactCoordinatesEntity artifactCoordinates = artifactGroupEntity.getArtifacts()
                                                                           .iterator()
                                                                           .next()
                                                                           .getArtifactCoordinates();
        assertNotNull(artifactCoordinates);
        assertNotNull(artifactCoordinates.getUuid());
        assertEquals("org/carlspring/artifact-gagrt-csw-1.2.3.jar",
                     artifactCoordinates.getPath());

        // Read
        Optional<ArtifactGroupEntity> artifactGroupOptional = gremlinArtifactGroupRepository.findById(artifactGroupEntity.getUuid());
        assertNotEquals(Optional.empty(), artifactGroupOptional);
        artifactGroupEntity = artifactGroupOptional.get();
        assertEquals("org/carlspring/artifact-gagrt-csw", artifactGroupEntity.getGroupId());

        artifactEntity = artifactGroupEntity.getArtifacts().iterator().next();
        Optional<ArtifactEntity> artifactOptional = gremlinArtifactRepository.findById(artifactEntity.getUuid());
        assertNotEquals(Optional.empty(), artifactOptional);
        artifactEntity = artifactOptional.get();
        assertEquals("storage0", artifactEntity.getStorageId());
        assertEquals("releases", artifactEntity.getRepositoryId());

        // Update
        ArtifactCoordinatesEntity anotherArtifactCoordinatesEntity = new ArtifactCoordinatesEntity();
        anotherArtifactCoordinatesEntity.setPath("org/carlspring/artifact-gagrt-csw-7.7.7.jar");
        anotherArtifactCoordinatesEntity.setVersion("7.7.7");

        ArtifactEntity anotherArtifactEntity = new ArtifactEntity();
        anotherArtifactEntity.setStorageId("storage0");
        anotherArtifactEntity.setRepositoryId("releases");
        anotherArtifactEntity.setArtifactCoordinates(anotherArtifactCoordinatesEntity);

        artifactGroupEntity.setGroupId("org/carlspring/artifact-gagrt-csw-new");
        artifactGroupEntity.getArtifacts().add(anotherArtifactEntity);
        artifactGroupEntity = gremlinArtifactGroupRepository.save(artifactGroupEntity);
        assertEquals(2, artifactGroupEntity.getArtifacts().size());
        HashSet<String> versionSet = new HashSet<>(Arrays.asList(new String[] { "1.2.3", "7.7.7" }));
        artifactGroupEntity.getArtifacts()
                           .forEach(a -> assertTrue(versionSet.remove(a.getArtifactCoordinates().getVersion())));
        assertTrue(versionSet.isEmpty());

        // Delete
        Set<String> artifactIdSet = artifactGroupEntity.getArtifacts()
                                                       .stream()
                                                       .map(ArtifactEntity::getUuid)
                                                       .collect(Collectors.toSet());
        Set<String> artifactCoordinatesIdSet = artifactGroupEntity.getArtifacts()
                                                                  .stream()
                                                                  .map(ArtifactEntity::getArtifactCoordinates)
                                                                  .map(ArtifactCoordinatesEntity::getUuid)
                                                                  .collect(Collectors.toSet());
        gremlinArtifactGroupRepository.delete(artifactGroupEntity);
        artifactIdSet.stream().forEach(id -> assertEquals(Optional.empty(), gremlinArtifactRepository.findById(id)));
        artifactCoordinatesIdSet.stream()
                                .forEach(id -> assertEquals(Optional.empty(),
                                                            gremlinArtifactCoordinatesRepository.findById(id)));
    }

}
