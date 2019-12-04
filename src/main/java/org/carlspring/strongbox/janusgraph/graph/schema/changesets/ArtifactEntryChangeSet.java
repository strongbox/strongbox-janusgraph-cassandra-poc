package org.carlspring.strongbox.janusgraph.graph.schema.changesets;

import java.util.HashSet;
import java.util.Set;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinates;
import org.carlspring.strongbox.janusgraph.domain.ArtifactDependency;
import org.carlspring.strongbox.janusgraph.domain.ArtifactEntry;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.VertexLabel;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class ArtifactEntryChangeSet
        implements ChangeSet
{

    private static final Logger logger = LoggerFactory.getLogger(ArtifactEntryChangeSet.class);

    @Override
    public int getOrder()
    {
        return 1;
    }

    @Override
    public Logger getLogger()
    {
        return logger;
    }

    public void applySchemaChanges(JanusGraphManagement jgm)
    {
        // Properties
        makePropertyKeyIfDoesNotExist(jgm, "uuid", String.class);
        makePropertyKeyIfDoesNotExist(jgm, "storageId", String.class);
        makePropertyKeyIfDoesNotExist(jgm, "repositoryId", String.class);
        makePropertyKeyIfDoesNotExist(jgm, "sizeInBytes", Long.class);
        makePropertyKeyIfDoesNotExist(jgm, "created", String.class);
        makePropertyKeyIfDoesNotExist(jgm, "tags", String[].class, Cardinality.SET);

        makePropertyKeyIfDoesNotExist(jgm, "path", String.class);
        makePropertyKeyIfDoesNotExist(jgm, "version", String.class);

        // Vertices
        makeVertexLabelIfDoesNotExist(jgm, ArtifactEntry.class.getSimpleName());
        makeVertexLabelIfDoesNotExist(jgm, ArtifactCoordinates.class.getSimpleName());

        // Edges
        makeEdgeLabelIfDoesNotExist(jgm, ArtifactEntry.class.getSimpleName() + "_" +
                                         ArtifactCoordinates.class.getSimpleName(), Multiplicity.MANY2ONE);
        makeEdgeLabelIfDoesNotExist(jgm, ArtifactDependency.class.getSimpleName(), Multiplicity.MULTI);
    }

    @Override
    public Set<String> createIndexes(JanusGraphManagement jgm)
    {
        Set<String> result = new HashSet<>();

        PropertyKey propertyKey = jgm.getPropertyKey("uuid");
        VertexLabel vertexLabel = jgm.getVertexLabel(ArtifactEntry.class.getSimpleName());
        buildIndexIfNecessary(jgm, ArtifactEntry.class.getSimpleName() + ".uuid", Vertex.class, propertyKey,
                              vertexLabel, true).ifPresent(result::add);

        propertyKey = jgm.getPropertyKey("uuid");
        vertexLabel = jgm.getVertexLabel(ArtifactCoordinates.class.getSimpleName());
        buildIndexIfNecessary(jgm, ArtifactCoordinates.class.getSimpleName() + ".uuid", Vertex.class, propertyKey,
                              vertexLabel, true).ifPresent(result::add);

        propertyKey = jgm.getPropertyKey("path");
        vertexLabel = jgm.getVertexLabel(ArtifactCoordinates.class.getSimpleName());
        buildIndexIfNecessary(jgm, ArtifactCoordinates.class.getSimpleName() + ".path", Vertex.class, propertyKey,
                              vertexLabel).ifPresent(result::add);

//        EdgeLabel artifactEntryToArtifactCoordinates = jg.getEdgeLabel(ArtifactEntry.class.getSimpleName() + "_"
//                + ArtifactCoordinates.class.getSimpleName());
//        jgm.buildEdgeIndex(artifactEntryToArtifactCoordinates, "battlesByTime", Direction.OUT);


        return result;
    }


}
