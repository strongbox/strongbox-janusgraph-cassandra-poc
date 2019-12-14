package org.carlspring.strongbox.janusgraph.gremlin.projections;

import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinates;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinatesEntity;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.__;

public class ArtifactCoordinatesProjection implements EntityProjection<ArtifactCoordinates>
{

    @Override
    public <S> Traversal<S, ArtifactCoordinates> traversal()
    {
        return __.<S, Object>project("uuid", "path", "version")
                 .by(__.properties("uuid").value())
                 .by(__.properties("path").value())
                 .by(__.properties("version").value())
                 .map(this::map);
    }

    private ArtifactCoordinates map(Traverser<Map<String, Object>> t)
    {
        ArtifactCoordinatesEntity result = new ArtifactCoordinatesEntity();
        result.setUuid((String)t.get().get("uuid"));
        result.setVersion((String)t.get().get("version"));
        result.setPath((String)t.get().get("path"));

        return result;
    }
}
