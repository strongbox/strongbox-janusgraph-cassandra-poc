package org.carlspring.strongbox.janusgraph.gremlin.repositories;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.ArtifactEntity;
import org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.ArtifactAdapter;
import org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalAdapter;
import org.springframework.stereotype.Repository;

@Repository("gremlinArtifactRepository")
public class ArtifactRepository extends GremlinVertexRepository<ArtifactEntity>
{

    @Inject
    private ArtifactAdapter adapter;

    @Override
    protected EntityTraversalAdapter<Vertex, ArtifactEntity> adapter()
    {
        return adapter;
    }

}
