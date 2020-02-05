package org.carlspring.strongbox.janusgraph.gremlin.repositories;

import java.util.function.Supplier;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinatesEntity;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversal;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversalSource;
import org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.ArtifactCoordinatesAdapter;
import org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalAdapter;
import org.springframework.stereotype.Repository;

@Repository("gremlinArtifactCoordinatesRepository")
public class ArtifactCoordinatesRepository extends GremlinVertexRepository<ArtifactCoordinatesEntity>
{

    @Inject
    private ArtifactCoordinatesAdapter adapter;

    @Override
    protected EntityTraversalAdapter<Vertex, ArtifactCoordinatesEntity> adapter()
    {
        return adapter;
    }

    @Override
    public EntityTraversal<Vertex, Vertex> start(Supplier<EntityTraversalSource> g)
    {
        return g.get().V();
    }
}