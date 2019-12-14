package org.carlspring.strongbox.janusgraph.gremlin.repositories;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.DomainObject;

public abstract class GremlinVertexReposiotry<E extends DomainObject> extends GremlinRepository<Vertex, E>
{

    @Override
    public <R extends E> R save(R entity)
    {
        Vertex resultVertex = start(this::g).saveV(label(), entity.getUuid(), adapter().unfold(entity))
                                            .next();
        E resultEntity = findById(resultVertex.<String>property("uuid").value()).get();

        return (R) resultEntity;
    }

}
