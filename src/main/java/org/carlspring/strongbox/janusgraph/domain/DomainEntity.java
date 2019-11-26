package org.carlspring.strongbox.janusgraph.domain;

import org.neo4j.ogm.annotation.Id;

public class DomainEntity implements DomainObject
{

    @Id
    private String uuid;

    @Override
    public String getUuid()
    {
        return uuid;
    }

    @Override
    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

}
