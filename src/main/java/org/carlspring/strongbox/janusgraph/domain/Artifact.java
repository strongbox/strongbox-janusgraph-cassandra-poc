package org.carlspring.strongbox.janusgraph.domain;

import java.util.Date;
import java.util.Set;

public interface Artifact extends DomainObject
{
    public static final String LABEL = "Artifact";
    
    String getStorageId();

    void setStorageId(String storageId);

    String getRepositoryId();

    void setRepositoryId(String repositoryId);

    Long getSizeInBytes();

    void setSizeInBytes(Long sizeInBytes);

    Date getCreated();

    void setCreated(Date created);

    Set<String> getTags();

    void setTags(Set<String> tags);

    ArtifactCoordinates getArtifactCoordinates();

    void setArtifactCoordinates(ArtifactCoordinates artifactCoordinates);

}