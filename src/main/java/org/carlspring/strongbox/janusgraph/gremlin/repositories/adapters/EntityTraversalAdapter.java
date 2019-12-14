package org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters;

import org.carlspring.strongbox.janusgraph.domain.DomainObject;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversal;

public interface EntityTraversalAdapter<S, E extends DomainObject>
{

    String getLabel();
    
    EntityTraversal<S, E> fold();

    EntityTraversal<S, S> unfold(E entity);
    
}