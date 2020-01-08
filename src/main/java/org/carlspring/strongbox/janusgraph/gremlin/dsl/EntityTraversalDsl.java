package org.carlspring.strongbox.janusgraph.gremlin.dsl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.GremlinDsl;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.Artifact;
import org.carlspring.strongbox.janusgraph.domain.DomainObject;
import org.carlspring.strongbox.janusgraph.domain.Edges;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GremlinDsl
public interface EntityTraversalDsl<S, E> extends GraphTraversal.Admin<S, E>
{

    Logger logger = LoggerFactory.getLogger(EntityTraversalDsl.class);

    String NULL = "__null";

    @SuppressWarnings("unchecked")
    default <E2> GraphTraversal<S, E2> findById(String label,
                                                String uuid)
    {
        return (GraphTraversal<S, E2>) hasLabel(label).has("uuid", uuid);
    }

    @SuppressWarnings("unchecked")
    default Traversal<S, Object> enrichProperty(String propertyName)
    {
        return coalesce(__.properties(propertyName).value(), __.<Object>constant(NULL));
    }

    @SuppressWarnings("unchecked")
    default Traversal<S, Object> enrichPropertySet(String propertyName)
    {
        return coalesce(__.propertyMap(propertyName).map(t -> t.get().get(propertyName)), __.<Object>constant(NULL));
    }

    default Traversal<S, Object> enrichArtifactCoordinates(EntityTraversal<S, Object> foldTraversal)
    {
        return outE(Edges.ARTIFACT_ARTIFACTCOORDINATES).fold()
                                                       .choose(t -> t.isEmpty(),
                                                               __.<Object>constant(NULL),
                                                               __.<Edge>unfold()
                                                                 .inV()
                                                                 .fold()
                                                                 .choose(t -> t.isEmpty(),
                                                                         __.<Object>constant(NULL),
                                                                         __.<Vertex>unfold().map(foldTraversal)));
    }

    default GraphTraversal<S, List<Object>> enrichArtifacts(EntityTraversal<S, Object> foldTraversal)
    {
        return outE(Edges.ARTIFACTGROUP_ARTIFACT).fold()
                                                 .choose(t -> t.isEmpty(),
                                                         __.<Object>constant(NULL),
                                                         __.<Edge>unfold()
                                                           .inV()
                                                           .hasLabel(Artifact.LABEL)
                                                           .map(foldTraversal))
                                                 .fold();
    }

    default <S2> Traversal<S, Vertex> saveV(String label,
                                            String uuid,
                                            EntityTraversal<S2, Vertex> unfoldTraversal)
    {
        uuid = Optional.ofNullable(uuid)
                       .orElse(NULL);
        GraphTraversal<S, DomainObject> element = findById(label, uuid);

        return element.fold()
                      .choose(t -> t.isEmpty(),
                              __.addV(label)
                                .property("uuid",
                                          Optional.of(uuid)
                                                  .filter(x -> !NULL.equals(x))
                                                  .orElse(UUID.randomUUID().toString())),
                              __.unfold())
                      .sideEffect(t -> logger.debug(String.format("Saved [%s]-[%s]-[%s]",
                                                                  ((Element) t.get()).label(),
                                                                  ((Element) t.get()).id(),
                                                                  ((Element) t.get()).property("uuid").value())))
                      .map(unfoldTraversal);
    }

}
