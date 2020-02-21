package org.carlspring.strongbox.janusgraph.domain;

public interface ArtifactCoordinates extends DomainObject
{

    String LABEL = "ArtifactCoordinates";

    String getVersion();

    void setVersion(String version);

    String getPath();

    void setPath(String id);

}