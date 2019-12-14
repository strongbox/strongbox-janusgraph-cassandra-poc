package org.carlspring.strongbox.janusgraph.gremlin.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import javax.inject.Inject;

import org.carlspring.strongbox.janusgraph.app.Application;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinatesEntity;
import org.carlspring.strongbox.janusgraph.domain.ArtifactEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
public class GremlinArtifactCoordinatesRepositoryTest
{

    @Inject
    @Qualifier("gremlinArtifactCoordinatesRepository")
    private ArtifactCoordinatesRepository gremlinArtifactCoordinatesRepository;

    @Inject
    @Qualifier("gremlinArtifactRepository")
    private ArtifactRepository gremlinArtifactRepository;

    @Test
    public void crudShouldWork()
    {
        Date created = new Date();

        ArtifactCoordinatesEntity artifactCoordinatesEntity = new ArtifactCoordinatesEntity();
        artifactCoordinatesEntity.setPath("org/carlspring/artifact-gacrt.jar");
        artifactCoordinatesEntity.setVersion("1.2.3");

        ArtifactEntity artifactEntity = new ArtifactEntity();
        artifactEntity.setStorageId("storage0");
        artifactEntity.setRepositoryId("releases");
        artifactEntity.setSizeInBytes(777L);
        artifactEntity.setTags(new HashSet<String>(Arrays.asList(new String[] { "release", "latest" })));
        artifactEntity.setArtifactCoordinates(artifactCoordinatesEntity);
        artifactEntity.setCreated(created);

        artifactEntity = gremlinArtifactRepository.save(artifactEntity);
        artifactCoordinatesEntity = artifactEntity.getArtifactCoordinates();
        assertNotNull(artifactEntity);
        assertNotNull(artifactEntity.getUuid());
        assertNotNull(artifactCoordinatesEntity);
        assertNotNull(artifactCoordinatesEntity.getUuid());

        Optional<ArtifactEntity> artifactOptional = gremlinArtifactRepository.findById(artifactEntity.getUuid());
        assertNotEquals(Optional.empty(), artifactOptional);

        Optional<ArtifactCoordinatesEntity> artifactCoordinatesOptional = gremlinArtifactCoordinatesRepository.findById(artifactCoordinatesEntity.getUuid());
        assertNotEquals(Optional.empty(), artifactCoordinatesOptional);

        artifactEntity = artifactOptional.get();
        assertEquals(created, artifactEntity.getCreated());

        artifactCoordinatesEntity = artifactCoordinatesOptional.get();
        assertEquals("org/carlspring/artifact-gacrt.jar", artifactCoordinatesEntity.getPath());
        assertEquals("1.2.3", artifactCoordinatesEntity.getVersion());

        artifactEntity.setCreated(created = new Date());
        artifactEntity.setArtifactCoordinates(artifactCoordinatesEntity);
        artifactCoordinatesEntity.setVersion("3.2.1");
        artifactEntity = gremlinArtifactRepository.save(artifactEntity);

        artifactOptional = gremlinArtifactRepository.findById(artifactEntity.getUuid());
        artifactEntity = artifactOptional.get();
        assertEquals(created, artifactOptional.get().getCreated());

        artifactCoordinatesOptional = gremlinArtifactCoordinatesRepository.findById(artifactCoordinatesEntity.getUuid());
        artifactCoordinatesEntity = artifactCoordinatesOptional.get();
        assertEquals("3.2.1", artifactCoordinatesEntity.getVersion());
    }

}
