package org.carlspring.strongbox.janusgraph.domain;

import org.neo4j.ogm.annotation.Id;

public class DomainEntity
{

    @Id
    private String uuid;

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

}
