package org.carlspring.strongbox.janusgraph.gremlin.repositories;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.ArtifactGroupEntity;
import org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.ArtifactGroupAdapter;
import org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalAdapter;
import org.springframework.stereotype.Repository;

@Repository("gremlinArtifactGroupRepository")
public class ArtifactGroupRepository extends GremlinVertexRepository<ArtifactGroupEntity>
{

    @Inject
    private ArtifactGroupAdapter adapter;

    @Override
    protected EntityTraversalAdapter<Vertex, ArtifactGroupEntity> adapter()
    {
        return adapter;
    }
}