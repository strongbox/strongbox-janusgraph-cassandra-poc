package org.carlspring.strongbox.janusgraph.domain;

import java.util.Set;

public interface ArtifactGroup<T extends Artifact> extends DomainObject
{
    public static final String LABEL = "ArtifactGroup";

    String getGroupId();

    void setGroupId(String groupId);

    
    Set<T> getArtifacts();

    void setArtifacts(Set<T> artifactEntries);

}