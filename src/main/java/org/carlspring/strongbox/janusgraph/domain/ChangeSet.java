package org.carlspring.strongbox.janusgraph.domain;

import java.util.UUID;

import org.neo4j.ogm.annotation.NodeEntity;

/**
 * @author Przemyslaw Fusik
 */
@NodeEntity
public class ChangeSet
        extends DomainEntity
        implements Comparable<ChangeSet>
{

    private Integer order;

    private String name;

    public static ChangeSet build(final String name,
                                  final Integer order)
    {
        ChangeSet changeSet = new ChangeSet();
        changeSet.setUuid(UUID.randomUUID().toString());
        changeSet.setName(name);
        changeSet.setOrder(order);
        return changeSet;
    }

    public Integer getOrder()
    {
        return order;
    }

    public void setOrder(final Integer order)
    {
        this.order = order;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    @Override
    public int compareTo(final ChangeSet o)
    {
        return Integer.compare(getOrder(), o.getOrder());
    }
}
