package org.carlspring.strongbox.janusgraph.domain;

public interface ArtifactDependency extends DomainObject
{
    public static final String LABEL = "ArtifactDependency";

    Artifact getSubject();

    void setSubject(Artifact subject);

    ArtifactCoordinates getDependency();

    void setDependency(ArtifactCoordinates dependency);

}