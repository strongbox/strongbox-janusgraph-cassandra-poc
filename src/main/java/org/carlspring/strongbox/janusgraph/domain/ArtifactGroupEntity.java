package org.carlspring.strongbox.janusgraph.domain;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(ArtifactGroup.LABEL)
public class ArtifactGroupEntity extends DomainEntity implements ArtifactGroup<ArtifactEntity>
{

    private String groupId;
    @Relationship(type = Edges.ARTIFACTGROUP_ARTIFACT, direction = Relationship.OUTGOING)
    private Set<ArtifactEntity> artifacts = new HashSet<>();

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    @Override
    public Set<ArtifactEntity> getArtifacts()
    {
        return artifacts;
    }

    @Override
    public void setArtifacts(Set<ArtifactEntity> artifacts)
    {
        this.artifacts = artifacts;
    }

}
