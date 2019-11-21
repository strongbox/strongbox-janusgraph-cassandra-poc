package org.carlspring.strongbox.janusgraph.domain;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity("ArtifactDependency")
public class ArtifactDependency extends DomainEntity
{

    @StartNode
    private ArtifactEntry subject;
    @EndNode
    private ArtifactEntry dependency;

    public ArtifactEntry getSubject()
    {
        return subject;
    }

    public void setSubject(ArtifactEntry subject)
    {
        this.subject = subject;
    }

    public ArtifactEntry getDependency()
    {
        return dependency;
    }

    public void setDependency(ArtifactEntry dependency)
    {
        this.dependency = dependency;
    }

}
