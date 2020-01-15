package org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters;

import static org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality.single;
import static org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalUtils.extractObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.Artifact;
import org.carlspring.strongbox.janusgraph.domain.ArtifactEntity;
import org.carlspring.strongbox.janusgraph.domain.ArtifactGroup;
import org.carlspring.strongbox.janusgraph.domain.ArtifactGroupEntity;
import org.carlspring.strongbox.janusgraph.domain.Edges;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversal;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.__;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author sbespalov
 *
 */
@Component
public class ArtifactGroupAdapter extends VertexEntityTraversalAdapter<ArtifactGroupEntity>
{

    private static final Logger logger = LoggerFactory.getLogger(ArtifactGroupAdapter.class);

    @Inject
    private ArtifactAdapter artifactAdapter;

    @Override
    public String getLabel()
    {
        return ArtifactGroup.LABEL;
    }

    @Override
    public EntityTraversal<Vertex, ArtifactGroupEntity> fold()
    {
        return __.<Vertex, Object>project("uuid", "groupId", "artifacts")
                 .by(__.enrichPropertyValue("uuid"))
                 .by(__.enrichPropertyValue("groupId"))
                 .by(__.outE(Edges.ARTIFACTGROUP_ARTIFACT)
                       .mapToObject(__.inV()
                                      .hasLabel(Artifact.LABEL)
                                      .map(artifactAdapter.fold())
                                      .map(EntityTraversalUtils::castToObject))
                       .fold())
                 .map(this::map);
    }

    private ArtifactGroupEntity map(Traverser<Map<String, Object>> t)
    {
        ArtifactGroupEntity result = new ArtifactGroupEntity();
        result.setUuid(extractObject(String.class, t.get().get("uuid")));
        result.setGroupId(extractObject(String.class, t.get().get("groupId")));
        result.setArtifacts(new HashSet<>((Collection<ArtifactEntity>) t.get().get("artifacts")));

        return result;
    }

    @Override
    public EntityTraversal<Vertex, Vertex> unfold(ArtifactGroupEntity entity)
    {
        EntityTraversal<Vertex, Vertex> t = __.<Vertex>identity();

        if (entity.getGroupId() != null)
        {
            t = t.property(single, "groupId", entity.getGroupId());
        }
        t = t.optional(__.outE(Edges.ARTIFACTGROUP_ARTIFACT).drop().outV());
        if (entity.getArtifacts() != null)
        {
            int i = 0;
            for (ArtifactEntity artifactEntity : entity.getArtifacts())
            {
                t = t.addE(Edges.ARTIFACTGROUP_ARTIFACT)
                     .to(__.coalesce(updateArtifact(i++, artifactEntity),
                                     createArtifact(i++, artifactEntity)))
                     .outV();
            }
        }

        return t;
    }

    private Traversal<Edge, Vertex> createArtifact(int i,
                                                   ArtifactEntity artifactEntity)
    {
        String alias = String.format("%s_%s", Edges.ARTIFACTGROUP_ARTIFACT, i);
        return this.<Edge>saveArtifact(artifactEntity);
    }

    private Traversal<Edge, Vertex> updateArtifact(int i,
                                                   ArtifactEntity artifactEntity)
    {
        String alias = String.format("%s_%s", Edges.ARTIFACTGROUP_ARTIFACT, i);
        return __.<Vertex, Edge>findById(Artifact.LABEL, artifactEntity.getUuid())
                 .map(saveArtifact(artifactEntity))
                 .select(alias);
    }

    private <S2> EntityTraversal<S2, Vertex> saveArtifact(ArtifactEntity artifact)
    {
        return __.<S2>V()
                 .saveV(Artifact.LABEL,
                        artifact.getUuid(),
                        artifactAdapter.unfold(artifact));
    }

    @Override
    public EntityTraversal<Vertex, ? extends Element> cascade()
    {
        return __.<Vertex>aggregate("x")
                 .outE(Edges.ARTIFACTGROUP_ARTIFACT)
                 .inV()
                 .map(artifactAdapter.cascade())
                 .select("x")
                 .unfold();
    }

}
