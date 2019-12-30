package org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters;

import static org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality.single;
import static org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalUtils.extractObject;

import java.util.Map;
import java.util.Optional;

import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinates;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinatesEntity;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversal;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.__;
import org.springframework.stereotype.Component;

@Component
public class ArtifactCoordinatesAdapter extends VertexEntityTraversalAdapter<ArtifactCoordinatesEntity>
{

    @Override
    public String getLabel()
    {
        return ArtifactCoordinates.LABEL;
    }

    @Override
    public EntityTraversal<Vertex, ArtifactCoordinatesEntity> fold()
    {
        return __.<Vertex, Object>project("uuid", "path", "version")
                 .by(__.enrichProperty("uuid"))
                 .by(__.enrichProperty("path"))
                 .by(__.enrichProperty("version"))
                 .map(this::map);
    }

    private ArtifactCoordinatesEntity map(Traverser<Map<String, Object>> t)
    {
        ArtifactCoordinatesEntity result = new ArtifactCoordinatesEntity();
        result.setUuid(extractObject(String.class, t.get().get("uuid")));
        result.setVersion(extractObject(String.class, t.get().get("version")));
        result.setPath(extractObject(String.class, t.get().get("path")));

        return result;
    }

    @Override
    public EntityTraversal<Vertex, Vertex> unfold(ArtifactCoordinatesEntity entity)
    {
        EntityTraversal<Vertex, Vertex> result = __.<Vertex>identity();
        
        if (Optional.of(entity).map(e -> e.getPath()).isPresent())
        {
            result = result.property(single, "path", entity.getPath());
        }
        if (Optional.of(entity).map(e -> e.getVersion()).isPresent())
        {
            result = result.property(single, "version", entity.getPath());
        }
        
        return __.<Vertex>property(single, "path", entity.getPath())
                 .property(single,
                           "version",
                           entity.getVersion());
    }

}
