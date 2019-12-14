package org.carlspring.strongbox.janusgraph.gremlin.dsl;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.GremlinDsl;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.carlspring.strongbox.janusgraph.domain.DomainObject;
import org.carlspring.strongbox.janusgraph.gremlin.projections.EntityProjection;

@GremlinDsl(traversalSource = "org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversalSourceDsl")
public interface EntityTraversalDsl<S, E> extends GraphTraversal.Admin<S, E>
{

    default <E2> GraphTraversal<S, E2> findById(Class<E2> targetClass,
                                                String uuid)
    {
        return map(t -> targetClass.cast(t.get())).has("uuid", uuid);
    }

    default <E2 extends DomainObject> GraphTraversal<S, E2> project(EntityProjection<E2> p)
    {
        return map(p.<S>traversal());
    }

}
