package org.carlspring.strongbox.janusgraph.gremlin.repositories;

import java.util.function.Supplier;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.ArtifactGroupEntity;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversal;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversalSource;
import org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.ArtifactGroupAdapter;
import org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalAdapter;
import org.springframework.stereotype.Repository;

@Repository("gremlinArtifactGroupRepository")
public class ArtifactGroupRepository extends GremlinVertexReposiotry<ArtifactGroupEntity>
{

    @Inject
    private ArtifactGroupAdapter adapter;

    @Override
    protected EntityTraversalAdapter<Vertex, ArtifactGroupEntity> adapter()
    {
        return adapter;
    }

    @Override
    public EntityTraversal<Vertex, Vertex> start(Supplier<EntityTraversalSource> g)
    {
        return g.get().V();
    }
}