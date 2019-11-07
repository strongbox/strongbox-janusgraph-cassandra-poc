package org.carlspring.strongbox.janusgraph.domain;

public class ArtifactCoordinates extends DomainEntity
{

    private String path;
    private String version;

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String id)
    {
        this.path = id;
    }

}
