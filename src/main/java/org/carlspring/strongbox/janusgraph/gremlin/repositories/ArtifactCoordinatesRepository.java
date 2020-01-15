package org.carlspring.strongbox.janusgraph.gremlin.repositories;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinatesEntity;
import org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.ArtifactCoordinatesAdapter;
import org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalAdapter;
import org.springframework.stereotype.Repository;

@Repository("gremlinArtifactCoordinatesRepository")
public class ArtifactCoordinatesRepository extends GremlinVertexReposiotry<ArtifactCoordinatesEntity>
{

    @Inject
    private ArtifactCoordinatesAdapter adapter;

    @Override
    protected EntityTraversalAdapter<Vertex, ArtifactCoordinatesEntity> adapter()
    {
        return adapter;
    }

}