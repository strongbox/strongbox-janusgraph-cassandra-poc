package org.carlspring.strongbox.janusgraph.gremlin.repositories;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

        // Create
        artifactEntity = gremlinArtifactRepository.save(artifactEntity);
        artifactCoordinatesEntity = artifactEntity.getArtifactCoordinates();
        assertNotNull(artifactEntity);
        assertNotNull(artifactEntity.getUuid());
        assertNotNull(artifactCoordinatesEntity);
        assertNotNull(artifactCoordinatesEntity.getUuid());

        // Read
        Optional<ArtifactEntity> artifactOptional = gremlinArtifactRepository.findById(artifactEntity.getUuid());
        assertNotEquals(Optional.empty(), artifactOptional);

        Optional<ArtifactCoordinatesEntity> artifactCoordinatesOptional = gremlinArtifactCoordinatesRepository.findById(artifactCoordinatesEntity.getUuid());
        assertNotEquals(Optional.empty(), artifactCoordinatesOptional);

        artifactEntity = artifactOptional.get();
        assertEquals(created, artifactEntity.getCreated());
        assertArrayEquals(new String[] { "release", "latest" }, artifactEntity.getTags().toArray());

        artifactCoordinatesEntity = artifactCoordinatesOptional.get();
        assertEquals("org/carlspring/artifact-gacrt.jar", artifactCoordinatesEntity.getPath());
        assertEquals("1.2.3", artifactCoordinatesEntity.getVersion());

        // Update
        artifactEntity.setCreated(created = new Date());
        artifactEntity.setTags(new HashSet<>(Arrays.asList(new String[] { "beta" })));
        artifactEntity.setArtifactCoordinates(artifactCoordinatesEntity);
        artifactCoordinatesEntity.setVersion("3.2.1");
        artifactEntity = gremlinArtifactRepository.save(artifactEntity);

        artifactOptional = gremlinArtifactRepository.findById(artifactEntity.getUuid());
        artifactEntity = artifactOptional.get();
        assertEquals(created, artifactOptional.get().getCreated());
        assertArrayEquals(new String[] { "beta" }, artifactEntity.getTags().toArray());

        artifactCoordinatesOptional = gremlinArtifactCoordinatesRepository.findById(artifactCoordinatesEntity.getUuid());
        artifactCoordinatesEntity = artifactCoordinatesOptional.get();
        assertEquals("3.2.1", artifactCoordinatesEntity.getVersion());

        // Delete
        gremlinArtifactCoordinatesRepository.deleteById(artifactCoordinatesEntity.getUuid());

        artifactCoordinatesOptional = gremlinArtifactCoordinatesRepository.findById(artifactCoordinatesEntity.getUuid());
        assertEquals(Optional.empty(), artifactCoordinatesOptional);

        artifactOptional = gremlinArtifactRepository.findById(artifactEntity.getUuid());
        assertEquals(Optional.empty(), artifactOptional);
    }

    @Test
    public void emptyValuesShouldWork()
    {
        ArtifactCoordinatesEntity artifactCoordinatesEntity = new ArtifactCoordinatesEntity();
        artifactCoordinatesEntity.setPath("org/carlspring/artifact-gacrt-evsw.jar");

        artifactCoordinatesEntity = gremlinArtifactCoordinatesRepository.save(artifactCoordinatesEntity);
        assertEquals("org/carlspring/artifact-gacrt-evsw.jar", artifactCoordinatesEntity.getUuid());
        assertEquals("org/carlspring/artifact-gacrt-evsw.jar", artifactCoordinatesEntity.getPath());
        assertNull(artifactCoordinatesEntity.getVersion());

        artifactCoordinatesEntity.setVersion("1.2.3");
        artifactCoordinatesEntity = gremlinArtifactCoordinatesRepository.save(artifactCoordinatesEntity);
        assertEquals("1.2.3", artifactCoordinatesEntity.getVersion());
    }

    @Test
    public void cascadeArtifactCoordinatesDeleteShoudWork()
    {
        ArtifactCoordinatesEntity artifactCoordinatesEntity = new ArtifactCoordinatesEntity();
        artifactCoordinatesEntity.setPath("org/carlspring/artifact-gacrt-cacdsw.jar");
        artifactCoordinatesEntity.setVersion("1.2.3");

        ArtifactEntity artifactEntity = new ArtifactEntity();
        artifactEntity.setStorageId("storage0");
        artifactEntity.setRepositoryId("releases");
        artifactEntity.setSizeInBytes(123L);
        artifactEntity.setArtifactCoordinates(artifactCoordinatesEntity);
        artifactEntity = gremlinArtifactRepository.save(artifactEntity);

        Optional<ArtifactCoordinatesEntity> artifactCoordinatesOptional = gremlinArtifactCoordinatesRepository.findById(artifactCoordinatesEntity.getUuid());
        assertNotEquals(Optional.empty(), artifactCoordinatesOptional);
        artifactCoordinatesEntity = artifactCoordinatesOptional.get();

        ArtifactEntity anotherArtifactEntity = new ArtifactEntity();
        anotherArtifactEntity.setArtifactCoordinates(artifactCoordinatesEntity);
        anotherArtifactEntity.setStorageId("storage0");
        anotherArtifactEntity.setRepositoryId("snapshots");
        anotherArtifactEntity.setSizeInBytes(321L);
        anotherArtifactEntity = gremlinArtifactRepository.save(anotherArtifactEntity);

        gremlinArtifactRepository.delete(anotherArtifactEntity);
        artifactCoordinatesOptional = gremlinArtifactCoordinatesRepository.findById(artifactCoordinatesEntity.getUuid());
        assertNotEquals(Optional.empty(), artifactCoordinatesOptional);

        gremlinArtifactRepository.delete(artifactEntity);
        artifactCoordinatesOptional = gremlinArtifactCoordinatesRepository.findById(artifactCoordinatesEntity.getUuid());
        assertEquals(Optional.empty(), artifactCoordinatesOptional);
    }

}
