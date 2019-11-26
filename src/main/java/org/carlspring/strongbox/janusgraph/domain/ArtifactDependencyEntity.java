package org.carlspring.strongbox.janusgraph.domain;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(ArtifactDependency.LABEL)
public class ArtifactDependencyEntity extends DomainEntity implements ArtifactDependency
{

    @StartNode
    private ArtifactEntity subject;
    @EndNode
    private ArtifactCoordinatesEntity dependency;

    @Override
    public Artifact getSubject()
    {
        return subject;
    }

    @Override
    public void setSubject(Artifact subject)
    {
        this.subject = (ArtifactEntity) subject;
    }

    @Override
    public ArtifactCoordinates getDependency()
    {
        return dependency;
    }

    @Override
    public void setDependency(ArtifactCoordinates dependency)
    {
        this.dependency = (ArtifactCoordinatesEntity) dependency;
    }

}
