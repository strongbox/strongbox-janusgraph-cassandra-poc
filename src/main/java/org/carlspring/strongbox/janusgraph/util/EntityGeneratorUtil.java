package org.carlspring.strongbox.janusgraph.util;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinatesEntity;
import org.carlspring.strongbox.janusgraph.domain.ArtifactDependencyEntity;
import org.carlspring.strongbox.janusgraph.domain.ArtifactEntity;

public class EntityGeneratorUtil
{

    public static ArtifactEntity createRandomArtifact()
    {
        ArtifactEntity entry = new ArtifactEntity();
        entry.setStorageId(RandomStringUtils.randomAlphanumeric(24));
        entry.setRepositoryId(RandomStringUtils.randomAlphanumeric(24));
        entry.setSizeInBytes(RandomUtils.nextLong(10000L, 1000000000L));
        entry.setCreated(new Date(RandomUtils.nextLong(820454400000L, new Date().getTime())));
        entry.setTags(new HashSet<>(Arrays.asList("release", "stable")));
        entry.setArtifactCoordinates(createRandomArtifactCoordinates());
        entry.setUuid(UUID.randomUUID().toString());
        return entry;
    }

    public static ArtifactDependencyEntity createArtifactDependency(ArtifactEntity from, ArtifactEntity to)
    {
        ArtifactDependencyEntity dependency = new ArtifactDependencyEntity();
        dependency.setDependency(to.getArtifactCoordinates());
        dependency.setSubject(from);
        dependency.setUuid(UUID.randomUUID().toString());
        return dependency;
    }

    public static ArtifactCoordinatesEntity createRandomArtifactCoordinates()
    {
        ArtifactCoordinatesEntity coordinates = new ArtifactCoordinatesEntity();
        coordinates.setUuid(UUID.randomUUID().toString());
        coordinates.setPath(RandomStringUtils.randomAlphanumeric(24));
        coordinates.setVersion(RandomStringUtils.random(16, "0123456789-."));
        return coordinates;
    }

}
