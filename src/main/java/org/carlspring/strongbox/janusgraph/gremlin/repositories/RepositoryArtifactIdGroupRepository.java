package org.carlspring.strongbox.janusgraph.gremlin.repositories;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.ArtifactGroup;
import org.carlspring.strongbox.janusgraph.domain.ArtifactGroupEntity;
import org.carlspring.strongbox.janusgraph.domain.RepositoryArtifactIdGroupEntity;
import org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalAdapter;
import org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalUtils;
import org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.RepositoryArtifactIdGroupAdapter;
import org.springframework.stereotype.Repository;

@Repository("gremlinRepositoryArtifactIdGroupRepository")
public class RepositoryArtifactIdGroupRepository extends GremlinVertexRepository<RepositoryArtifactIdGroupEntity>
{

    @Inject
    private RepositoryArtifactIdGroupAdapter adapter;

    @Override
    protected EntityTraversalAdapter<Vertex, RepositoryArtifactIdGroupEntity> adapter()
    {
        return adapter;
    }

    @Override
    public <R extends RepositoryArtifactIdGroupEntity> R save(R entity)
    {
        ArtifactGroupEntity artifactGroup = entity.getArtifactGroup();

        Object optionalUuid = start(this::g).saveV(ArtifactGroup.LABEL, artifactGroup.getUuid(), __.identity())
                                            .enrichPropertyValue("uuid")
                                            .next();
        String uuid = EntityTraversalUtils.extractObject(String.class, optionalUuid);
        entity.setUuid(uuid);

        return super.save(entity);
    }

}