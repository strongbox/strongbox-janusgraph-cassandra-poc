package org.carlspring.strongbox.janusgraph.domain;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity("ArtifactDependency")
public class ArtifactDependency extends DomainEntity
{

    public static final String LABEL = "ArtifactDependency";

    @StartNode
    private ArtifactEntry subject;
    @EndNode
    private ArtifactCoordinates dependency;

    public ArtifactEntry getSubject()
    {
        return subject;
    }

    public void setSubject(ArtifactEntry subject)
    {
        this.subject = subject;
    }

    public ArtifactCoordinates getDependency()
    {
        return dependency;
    }

    public void setDependency(ArtifactCoordinates dependency)
    {
        this.dependency = dependency;
    }

}
