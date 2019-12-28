package org.carlspring.strongbox.janusgraph.gremlin.dsl;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.GremlinDsl;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.Edges;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sbespalov
 *
 * @param <S>
 * @param <E>
 */
@GremlinDsl
public interface EntityTraversalDsl<S, E> extends GraphTraversal.Admin<S, E>
{

    Logger logger = LoggerFactory.getLogger(EntityTraversalDsl.class);

    String NULL = "__null";

    default <E2> GraphTraversal<S, E2> findById(String label,
                                                String uuid)
    {
        return (GraphTraversal<S, E2>) hasLabel(label).has("uuid", uuid);
    }

    default Traversal<S, Object> enrichProperty(String propertyName)
    {
        return properties(propertyName).fold().choose(t -> t.isEmpty(), __.<Object>constant(NULL), __.unfold().value());
    }

    default Traversal<S, Object> enrichPropertySet(String propertyName)
    {
        return propertyMap(propertyName).fold()
                                        .choose(t -> t.isEmpty(),
                                                __.<Object>constant(NULL),
                                                __.<Map<String, Object>>unfold().map(t -> t.get().get(propertyName)));
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

    default <S2> Traversal<S, Vertex> saveV(String label,
                                            String uuid,
                                            EntityTraversal<S2, Vertex> unfoldTraversal)
    {
        uuid = Optional.ofNullable(uuid)
                       .orElse(NULL);
        GraphTraversal<S, E> element = findById(label, uuid);

        return element.fold()
                      .choose(t -> t.isEmpty(),
                              __.addV(label).property("uuid", UUID.randomUUID().toString()),
                              __.unfold())
                      .sideEffect(t -> logger.debug(String.format("Saved [%s]-[%s]-[%s]",
                                                                  ((Element) t.get()).label(),
                                                                  ((Element) t.get()).id(),
                                                                  ((Element) t.get()).property("uuid").value())))
                      .map(unfoldTraversal);
    }

}
