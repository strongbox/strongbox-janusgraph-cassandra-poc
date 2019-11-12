package org.carlspring.strongbox.janusgraph.domain;

import java.util.Date;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class ArtifactEntry extends DomainEntity
{

    private String storageId;
    private String repositoryId;
    private Long sizeInBytes;
    private Date created;
    private Set<String> tags;
    @Relationship(type = "ArtifactEntry#ArtifactCoordinates", direction = Relationship.INCOMING)
    private ArtifactCoordinates artifactCoordinates;

    public String getStorageId()
    {
        return storageId;
    }

    public void setStorageId(String storageId)
    {
        this.storageId = storageId;
    }

    public String getRepositoryId()
    {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId)
    {
        this.repositoryId = repositoryId;
    }

    public Long getSizeInBytes()
    {
        return sizeInBytes;
    }

    public void setSizeInBytes(Long sizeInBytes)
    {
        this.sizeInBytes = sizeInBytes;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public Set<String> getTags()
    {
        return tags;
    }

    public void setTags(Set<String> tags)
    {
        this.tags = tags;
    }

    public ArtifactCoordinates getArtifactCoordinates()
    {
        return artifactCoordinates;
    }

    public void setArtifactCoordinates(ArtifactCoordinates artifactCoordinates)
    {
        this.artifactCoordinates = artifactCoordinates;
    }

}
