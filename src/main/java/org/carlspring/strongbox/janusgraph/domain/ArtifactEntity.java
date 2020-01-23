package org.carlspring.strongbox.janusgraph.domain;

import static org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalUtils.DATE_FORMAT;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.carlspring.strongbox.janusgraph.domain.converter.SetPropertyConverter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.annotation.typeconversion.DateString;

@NodeEntity(Artifact.LABEL)
public class ArtifactEntity extends DomainEntity implements Artifact
{
    private String storageId;

    private String repositoryId;

    private Long sizeInBytes;

    @DateString(DATE_FORMAT)
    private Date created;

    @Convert(SetPropertyConverter.class)
    private Set<String> tags = new HashSet<>();

    @Relationship(type = Edges.ARTIFACT_ARTIFACTCOORDINATES, direction = Relationship.OUTGOING)
    private ArtifactCoordinatesEntity artifactCoordinates;

    @Override
    public String getStorageId()
    {
        return storageId;
    }

    @Override
    public void setStorageId(String storageId)
    {
        this.storageId = storageId;
    }

    @Override
    public String getRepositoryId()
    {
        return repositoryId;
    }

    @Override
    public void setRepositoryId(String repositoryId)
    {
        this.repositoryId = repositoryId;
    }

    @Override
    public Long getSizeInBytes()
    {
        return sizeInBytes;
    }

    @Override
    public void setSizeInBytes(Long sizeInBytes)
    {
        this.sizeInBytes = sizeInBytes;
    }

    @Override
    public Date getCreated()
    {
        return created;
    }

    @Override
    public void setCreated(final Date created)
    {
        this.created = created;
    }

    @Override
    public Set<String> getTags()
    {
        return tags;
    }

    @Override
    public void setTags(Set<String> tags)
    {
        this.tags = tags;
    }

    @Override
    public ArtifactCoordinatesEntity getArtifactCoordinates()
    {
        return artifactCoordinates;
    }

    @Override
    public void setArtifactCoordinates(ArtifactCoordinates artifactCoordinates)
    {
        this.artifactCoordinates = (ArtifactCoordinatesEntity) artifactCoordinates;
    }

}
