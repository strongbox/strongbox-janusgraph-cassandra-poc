package org.carlspring.strongbox.janusgraph.domain;

import java.util.NavigableSet;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * @author Przemyslaw Fusik
 */
@NodeEntity
public class DatabaseSchema
        extends DomainEntity
{

    @Relationship(type = "DatabaseSchema_ChangeSet")
    private NavigableSet<ChangeSet> changeSets;

    public NavigableSet<ChangeSet> getChangeSets()
    {
        return changeSets;
    }

    public void setChangeSets(final NavigableSet<ChangeSet> changeSets)
    {
        this.changeSets = changeSets;
    }
}
