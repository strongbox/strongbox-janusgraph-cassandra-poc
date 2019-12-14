package org.carlspring.strongbox.janusgraph.gremlin.projections;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.carlspring.strongbox.janusgraph.domain.DomainObject;

public interface EntityProjection<T extends DomainObject>
{

    <S> Traversal<S, T> traversal();
    
}
