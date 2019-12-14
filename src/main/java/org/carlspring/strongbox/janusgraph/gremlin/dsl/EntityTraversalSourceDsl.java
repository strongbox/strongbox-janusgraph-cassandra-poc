package org.carlspring.strongbox.janusgraph.gremlin.dsl;

import org.apache.tinkerpop.gremlin.process.remote.RemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategies;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinates;

public class EntityTraversalSourceDsl extends GraphTraversalSource
{

    public EntityTraversalSourceDsl(Graph graph,
                                    TraversalStrategies traversalStrategies)
    {
        super(graph, traversalStrategies);
    }

    public EntityTraversalSourceDsl(Graph graph)
    {
        super(graph);
    }

    public EntityTraversalSourceDsl(RemoteConnection connection)
    {
        super(connection);
    }

    public GraphTraversal<Vertex, Vertex> artifactCoordinates()
    {
        return this.clone().V().hasLabel(ArtifactCoordinates.LABEL);
    }

}
