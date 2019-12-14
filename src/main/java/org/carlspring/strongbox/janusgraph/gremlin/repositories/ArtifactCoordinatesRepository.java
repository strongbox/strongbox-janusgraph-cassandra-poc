package org.carlspring.strongbox.janusgraph.gremlin.repositories;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinates;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinatesEntity;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversal;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversalSource;
import org.carlspring.strongbox.janusgraph.gremlin.projections.ArtifactCoordinatesProjection;
import org.janusgraph.core.JanusGraph;
import org.springframework.stereotype.Repository;

@Repository
public class ArtifactCoordinatesRepository
{

    @Inject
    private JanusGraph janusGraph;

    public ArtifactCoordinatesEntity findById(String uuid)
    {
        EntityTraversalSource g = janusGraph.traversal(EntityTraversalSource.class);
        EntityTraversal<Vertex, ArtifactCoordinates> traversal = g.artifactCoordinates()
                                                                  .findById(Vertex.class, uuid)
                                                                  .project(new ArtifactCoordinatesProjection());

        if (!traversal.hasNext())
        {
            return null;
        }

        return (ArtifactCoordinatesEntity) traversal.next();
    }

}
