package org.carlspring.strongbox.janusgraph.gremlin.repositories;

import java.util.function.Supplier;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.ArtifactEntity;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversal;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversalSource;
import org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.ArtifactAdapter;
import org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalAdapter;
import org.springframework.stereotype.Repository;

@Repository("gremlinArtifactRepository")
public class ArtifactRepository extends GremlinVertexReposiotry<ArtifactEntity>
{

    @Inject
    private ArtifactAdapter adapter;

    @Override
    protected EntityTraversalAdapter<Vertex, ArtifactEntity> adapter()
    {
        return adapter;
    }

    @Override
    public EntityTraversal<Vertex, Vertex> start(Supplier<EntityTraversalSource> g)
    {
        return g.get().V();
    }
}
