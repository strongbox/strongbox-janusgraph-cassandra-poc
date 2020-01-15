package org.carlspring.strongbox.janusgraph.domain;

import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(RepositoryArtifactIdGroup.LABEL)
public class RepositoryArtifactIdGroupEntity extends DomainEntity implements RepositoryArtifactIdGroup<ArtifactEntity>
{
    private String storageId;
    private String repositoryId;
    @Relationship(type = "RepositoryArtifactIdGroupEntity_ArtifactGroupEntity", direction = Relationship.OUTGOING)
    private ArtifactGroupEntity artifactGroup;

    public RepositoryArtifactIdGroupEntity()
    {
        this(new ArtifactGroupEntity());
    }

    public RepositoryArtifactIdGroupEntity(ArtifactGroupEntity artifactGroup)
    {
        this.artifactGroup = artifactGroup;
    }

    public void setUuid(String uuid)
    {
        super.setUuid(uuid);
        artifactGroup.setUuid(uuid);
    }

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

    public String getGroupId()
    {
        return artifactGroup.getGroupId();
    }

    public void setGroupId(String groupId)
    {
        artifactGroup.setGroupId(groupId);
    }

    @Override
    public Set<ArtifactEntity> getArtifacts()
    {
        return artifactGroup.getArtifacts();
    }

    @Override
    public void setArtifacts(Set<ArtifactEntity> artifactEntries)
    {
        artifactGroup.setArtifacts(artifactEntries);
    }

    public ArtifactGroupEntity getArtifactGroup()
    {
        return artifactGroup;
    }

}
