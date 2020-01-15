package org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters;

import static org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality.single;
import static org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalUtils.extractObject;

import java.util.Map;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.ArtifactGroup;
import org.carlspring.strongbox.janusgraph.domain.ArtifactGroupEntity;
import org.carlspring.strongbox.janusgraph.domain.Edges;
import org.carlspring.strongbox.janusgraph.domain.RepositoryArtifactIdGroup;
import org.carlspring.strongbox.janusgraph.domain.RepositoryArtifactIdGroupEntity;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversal;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.__;
import org.springframework.stereotype.Component;

/**
 * @author sbespalov
 *
 */
@Component
public class RepositoryArtifactIdGroupAdapter extends VertexEntityTraversalAdapter<RepositoryArtifactIdGroupEntity>
{

    @Inject
    private ArtifactGroupAdapter artifactGroupAdapter;

    @Override
    public String getLabel()
    {
        return RepositoryArtifactIdGroup.LABEL;
    }

    @Override
    public EntityTraversal<Vertex, RepositoryArtifactIdGroupEntity> fold()
    {
        return __.<Vertex, Object>project("uuid", "storageId", "repositoryId", "artifactGroup")
                 .by(__.enrichPropertyValue("uuid"))
                 .by(__.enrichPropertyValue("storageId"))
                 .by(__.enrichPropertyValue("repositoryId"))
                 .by(__.outE(Edges.REPOSITORYARTIFACTIDGROUP_ARTIFACTGROUP)
                       .mapToObject(__.inV()
                                      .hasLabel(ArtifactGroup.LABEL)
                                      .map(artifactGroupAdapter.fold())
                                      .map(EntityTraversalUtils::castToObject)))
                 .map(this::map);
    }

    private RepositoryArtifactIdGroupEntity map(Traverser<Map<String, Object>> t)
    {
        RepositoryArtifactIdGroupEntity result = new RepositoryArtifactIdGroupEntity(
                extractObject(ArtifactGroupEntity.class, t.get().get("artifactGroup")));
        result.setUuid(extractObject(String.class, t.get().get("uuid")));
        result.setStorageId(extractObject(String.class, t.get().get("storageId")));
        result.setRepositoryId(extractObject(String.class, t.get().get("repositoryId")));

        return result;
    }

    @Override
    public EntityTraversal<Vertex, Vertex> unfold(RepositoryArtifactIdGroupEntity entity)
    {
        ArtifactGroupEntity artifactGroup = entity.getArtifactGroup();

        return __.<Vertex, Edge>coalesce(updateArtifactGroup(artifactGroup),
                                         createArtifactGroup(artifactGroup))
                 .outV()
                 .map(unfoldRepositoryArtifactIdGroup(entity));
    }

    private Traversal<Vertex, Edge> updateArtifactGroup(ArtifactGroupEntity artifactGroup)
    {
        return __.<Vertex>outE(Edges.REPOSITORYARTIFACTIDGROUP_ARTIFACTGROUP)
                 .as(Edges.REPOSITORYARTIFACTIDGROUP_ARTIFACTGROUP)
                 .inV()
                 .map(saveArtifactGroup(artifactGroup))
                 .select(Edges.REPOSITORYARTIFACTIDGROUP_ARTIFACTGROUP);
    }

    private <S2 extends Element> Traversal<S2, Edge> createArtifactGroup(ArtifactGroupEntity artifactGroup)
    {
        return __.<S2>addE(Edges.REPOSITORYARTIFACTIDGROUP_ARTIFACTGROUP)
                 .from(__.identity())
                 .to(saveArtifactGroup(artifactGroup));
    }

    private <S2> EntityTraversal<S2, Vertex> saveArtifactGroup(ArtifactGroupEntity artifactGroup)
    {
        return __.<S2>V()
                 .saveV(ArtifactGroup.LABEL,
                        artifactGroup.getUuid(),
                        artifactGroupAdapter.unfold(artifactGroup));
    }

    private EntityTraversal<Vertex, Vertex> unfoldRepositoryArtifactIdGroup(RepositoryArtifactIdGroupEntity entity)
    {
        EntityTraversal<Vertex, Vertex> t = __.<Vertex>identity();

        if (entity.getStorageId() != null)
        {
            t = t.property(single, "storageId", entity.getStorageId());
        }
        if (entity.getRepositoryId() != null)
        {
            t = t.property(single, "repositoryId", entity.getRepositoryId());
        }

        return t;
    }

    @Override
    public EntityTraversal<Vertex, ? extends Element> cascade()
    {
        return __.<Vertex>aggregate("x")
                 .outE(Edges.REPOSITORYARTIFACTIDGROUP_ARTIFACTGROUP)
                 .inV()
                 .map(artifactGroupAdapter.cascade())
                 .select("x")
                 .unfold();
    }

}
