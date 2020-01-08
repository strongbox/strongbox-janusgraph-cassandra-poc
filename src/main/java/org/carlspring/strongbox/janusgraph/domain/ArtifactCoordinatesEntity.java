package org.carlspring.strongbox.janusgraph.domain;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(ArtifactCoordinates.LABEL)
public class ArtifactCoordinatesEntity extends DomainEntity implements ArtifactCoordinates
{

    private String path;
    private String version;

    @Override
    public String getUuid()
    {
        return getPath();
    }

    @Override
    public void setUuid(String uuid)
    {
        setPath(uuid);
    }

    @Override
    public String getVersion()
    {
        return version;
    }

    @Override
    public void setVersion(String version)
    {
        this.version = version;
    }

    @Override
    public String getPath()
    {
        return path;
    }

    @Override
    public void setPath(String id)
    {
        this.path = id;
        super.setUuid(id);
    }

}
