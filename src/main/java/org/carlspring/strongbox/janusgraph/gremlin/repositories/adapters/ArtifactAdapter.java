package org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters;

import static org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality.set;
import static org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality.single;
import static org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalUtils.extractDate;
import static org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalUtils.extractList;
import static org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalUtils.extractObject;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.Artifact;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinates;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinatesEntity;
import org.carlspring.strongbox.janusgraph.domain.ArtifactEntity;
import org.carlspring.strongbox.janusgraph.domain.Edges;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversal;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.__;
import org.springframework.stereotype.Component;

@Component
public class ArtifactAdapter extends VertexEntityTraversalAdapter<ArtifactEntity>
{

    @Inject
    private ArtifactCoordinatesAdapter artifactCoordinatesAdapter;

    @Override
    public String getLabel()
    {
        return Artifact.LABEL;
    }

    @Override
    public EntityTraversal<Vertex, ArtifactEntity> fold()
    {
        return __.<Vertex, Object>project("uuid", "storageId", "repositoryId", "sizeInBytes", "created", "tags",
                                          "artifactCoordinates")
                 .by(__.enrichProperty("uuid"))
                 .by(__.enrichProperty("storageId"))
                 .by(__.enrichProperty("repositoryId"))
                 .by(__.enrichProperty("sizeInBytes"))
                 .by(__.enrichProperty("created"))
                 .by(__.enrichPropertySet("tags"))
                 .by(__.enrichArtifactCoordinates(artifactCoordinatesAdapter.fold()
                                                                            .map(t -> Object.class.cast(t.get()))))
                 .map(this::map);
    }

    private ArtifactEntity map(Traverser<Map<String, Object>> t)
    {
        ArtifactEntity result = new ArtifactEntity();
        result.setUuid(extractObject(String.class, t.get().get("uuid")));
        result.setStorageId(extractObject(String.class, t.get().get("storageId")));
        result.setRepositoryId(extractObject(String.class, t.get().get("repositoryId")));
        result.setSizeInBytes(extractObject(Long.class, t.get().get("sizeInBytes")));
        result.setCreated(extractDate(t.get().get("created")));
        result.setTags(new HashSet<>(extractList(String.class, t.get().get("tags"))));
        result.setArtifactCoordinates(extractObject(ArtifactCoordinates.class, t.get().get("artifactCoordinates")));

        return result;
    }

    @Override
    public EntityTraversal<Vertex, Vertex> unfold(ArtifactEntity entity)
    {
        ArtifactCoordinatesEntity artifactCoordinates = entity.getArtifactCoordinates();

        return __.<Vertex, Edge>coalesce(updateArtifactCoordinates(artifactCoordinates),
                                         createArtifactCoordinates(artifactCoordinates))
                 .outV()
                 .map(unfoldArtifact(entity));
    }

    private EntityTraversal<Vertex, Edge> updateArtifactCoordinates(ArtifactCoordinatesEntity artifactCoordinates)
    {
        return __.outE(Edges.ARTIFACT_ARTIFACTCOORDINATES)
                 .as(Edges.ARTIFACT_ARTIFACTCOORDINATES)
                 .inV()
                 .map(saveArtifactCoordinates(artifactCoordinates))
                 .select(Edges.ARTIFACT_ARTIFACTCOORDINATES);
    }

    private EntityTraversal<Vertex, Vertex> unfoldArtifact(ArtifactEntity entity)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(EntityTraversalUtils.DATE_FORMAT);

        EntityTraversal<Vertex, Vertex> t = __.<Vertex>identity();
        
        if (entity.getStorageId() != null) {
            t = t.property(single, "storageId", entity.getStorageId());
        }
        if (entity.getRepositoryId() != null) {
            t = t.property(single, "repositoryId", entity.getRepositoryId());
        }
        if (entity.getSizeInBytes() != null) {
            t = t.property(single, "sizeInBytes", entity.getSizeInBytes());
        }
        if (entity.getCreated() != null) {
            t = t.property(single, "created", sdf.format(entity.getCreated()));
        }
        
        if (entity.getTags() != null)
        {
            t = t.sideEffect(__.properties("tags").drop());
            
            Set<String> tags = entity.getTags();
            for (String tag : tags)
            {
                t = t.property(set, "tags", tag);
            }
        }

        return t;
    }

    private <S2> EntityTraversal<S2, Edge> createArtifactCoordinates(ArtifactCoordinatesEntity artifactCoordinates)
    {
        return __.<S2>addE(Edges.ARTIFACT_ARTIFACTCOORDINATES)
                 .from(__.identity())
                 .to(saveArtifactCoordinates(artifactCoordinates));
    }

    private <S2> EntityTraversal<S2, Vertex> saveArtifactCoordinates(ArtifactCoordinatesEntity artifactCoordinates)
    {
        return __.<S2>V()
                 .saveV(ArtifactCoordinates.LABEL,
                        artifactCoordinates.getUuid(),
                        artifactCoordinatesAdapter.unfold(artifactCoordinates))
                 .map(artifactCoordinatesAdapter.unfold(artifactCoordinates));
    }

}
