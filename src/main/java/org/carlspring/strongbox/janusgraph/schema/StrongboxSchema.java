package org.carlspring.strongbox.janusgraph.schema;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import org.carlspring.strongbox.janusgraph.domain.*;

import org.janusgraph.core.Cardinality;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.VertexLabel;
import org.janusgraph.core.schema.EdgeLabelMaker;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.core.schema.JanusGraphSchemaType;
import org.janusgraph.core.schema.PropertyKeyMaker;
import org.janusgraph.core.schema.SchemaAction;
import org.janusgraph.graphdb.database.management.ManagementSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StrongboxSchema
{

    private static final Logger logger = LoggerFactory.getLogger(StrongboxSchema.class);

    @Inject
    public void createSchema(JanusGraph jg) throws InterruptedException
    {
        JanusGraphManagement jgm = jg.openManagement();
        try
        {
            applySchemaChanges(jgm);
            logger.info(String.format("Schema: %n%s", jgm.printSchema()));
            jgm.commit();
        }
        catch (Exception e)
        {
            logger.error("Failed to apply schema changes.", e);
            jgm.rollback();
            throw new RuntimeException("Failed to apply schema changes.", e);
        }
        
        jgm = jg.openManagement();
        Set<String> indexes;
        try
        {
            indexes = createIndexes(jg, jgm);
            jgm.commit();
        }
        catch (Exception e)
        {
            logger.error("Failed to create indexes.", e);
            jgm.rollback();
            throw new RuntimeException("Failed to create indexes.", e);
        }

        for (String janusGraphIndex : indexes)
        {
            logger.info(String.format("Wait index [%s] to be registered.", janusGraphIndex));
            ManagementSystem.awaitGraphIndexStatus(jg, janusGraphIndex).call();
        }
        
        jgm = jg.openManagement();
        try
        {
            enableIndexes(jgm, indexes);
            jgm.commit();
        }
        catch (Exception e)
        {
            logger.error("Failed to enable indexes.", e);
            jgm.rollback();
            throw new RuntimeException("Failed to enable indexes.", e);
        }
    }

    protected void enableIndexes(JanusGraphManagement jgm,
                                 Set<String> indexes)
        throws InterruptedException,
        ExecutionException
    {
        for (String janusGraphIndex : indexes)
        {
            logger.info(String.format("Enabling index [%s].", janusGraphIndex));
            jgm.updateIndex(jgm.getGraphIndex(janusGraphIndex), SchemaAction.ENABLE_INDEX).get();
        }
    }

    protected Set<String> createIndexes(JanusGraph jg, JanusGraphManagement jgm) throws InterruptedException
    {
        Set<String> result = new HashSet<>();

        PropertyKey propertyKey = jgm.getPropertyKey("uuid");
        VertexLabel vertexLabel = jgm.getVertexLabel(Artifact.LABEL);
        buildIndexIfNecessary(jgm, Artifact.LABEL + ".uuid", Vertex.class, propertyKey,
                              vertexLabel, true).ifPresent(result::add);

        propertyKey = jgm.getPropertyKey("uuid");
        vertexLabel = jgm.getVertexLabel(ArtifactCoordinates.LABEL);
        buildIndexIfNecessary(jgm, ArtifactCoordinates.LABEL + ".uuid", Vertex.class, propertyKey,
                              vertexLabel, true).ifPresent(result::add);
        propertyKey = jgm.getPropertyKey("path");
        vertexLabel = jgm.getVertexLabel(ArtifactCoordinates.LABEL);
        buildIndexIfNecessary(jgm, ArtifactCoordinates.LABEL + ".path", Vertex.class, propertyKey,
                              vertexLabel).ifPresent(result::add);

        propertyKey = jgm.getPropertyKey("uuid");
        vertexLabel = jgm.getVertexLabel(ArtifactGroup.LABEL);
        buildIndexIfNecessary(jgm, ArtifactGroup.LABEL + ".uuid", Vertex.class, propertyKey,
                              vertexLabel, true).ifPresent(result::add);
        propertyKey = jgm.getPropertyKey("groupId");
        vertexLabel = jgm.getVertexLabel(ArtifactGroup.LABEL);
        buildIndexIfNecessary(jgm, ArtifactGroup.LABEL + ".groupId", Vertex.class, propertyKey,
                              vertexLabel).ifPresent(result::add);
        
        propertyKey = jgm.getPropertyKey("uuid");
        vertexLabel = jgm.getVertexLabel(RepositoryArtifactIdGroup.LABEL);
        buildIndexIfNecessary(jgm, RepositoryArtifactIdGroup.LABEL + ".uuid", Vertex.class, propertyKey,
                              vertexLabel, true).ifPresent(result::add);

//        EdgeLabel artifactEntryToArtifactCoordinates = jg.getEdgeLabel(ArtifactEntry.class.getSimpleName() + "_"
//                + ArtifactCoordinates.class.getSimpleName());
//        jgm.buildEdgeIndex(artifactEntryToArtifactCoordinates, "battlesByTime", Direction.OUT);

        propertyKey = jgm.getPropertyKey("uuid");
        vertexLabel = jgm.getVertexLabel(User.LABEL);
        buildIndexIfNecessary(jgm, User.LABEL + ".uuid", Vertex.class, propertyKey, vertexLabel, true).ifPresent(result::add);

        return result;
    }

    private void applySchemaChanges(JanusGraphManagement jgm)
    {
        // Properties
        makePropertyKeyIfDoesNotExist(jgm, "uuid", String.class);
        makePropertyKeyIfDoesNotExist(jgm, "storageId", String.class);
        makePropertyKeyIfDoesNotExist(jgm, "repositoryId", String.class);
        makePropertyKeyIfDoesNotExist(jgm, "sizeInBytes", Long.class);
        makePropertyKeyIfDoesNotExist(jgm, "created", String.class);
        makePropertyKeyIfDoesNotExist(jgm, "tags", String.class, Cardinality.SET);
        makePropertyKeyIfDoesNotExist(jgm, "lastUpdated", String.class);

        makePropertyKeyIfDoesNotExist(jgm, "path", String.class);
        makePropertyKeyIfDoesNotExist(jgm, "version", String.class);
        
        makePropertyKeyIfDoesNotExist(jgm, "groupId", String.class);

        // Vertices
        makeVertexLabelIfDoesNotExist(jgm, Artifact.LABEL);
        makeVertexLabelIfDoesNotExist(jgm, ArtifactCoordinates.LABEL);
        makeVertexLabelIfDoesNotExist(jgm, ArtifactGroup.LABEL);
        makeVertexLabelIfDoesNotExist(jgm, RepositoryArtifactIdGroup.LABEL);
        makeVertexLabelIfDoesNotExist(jgm, User.LABEL);

        // Edges
        makeEdgeLabelIfDoesNotExist(jgm, Edges.ARTIFACT_ARTIFACTCOORDINATES, Multiplicity.MANY2ONE);
        makeEdgeLabelIfDoesNotExist(jgm, ArtifactDependency.LABEL, Multiplicity.MULTI);
        makeEdgeLabelIfDoesNotExist(jgm, Edges.ARTIFACTGROUP_ARTIFACT, Multiplicity.ONE2MANY);
        makeEdgeLabelIfDoesNotExist(jgm, Edges.REPOSITORYARTIFACTIDGROUP_ARTIFACTGROUP, Multiplicity.ONE2ONE);
        makeEdgeLabelIfDoesNotExist(jgm, Edges.USER, Multiplicity.SIMPLE);
    }

    private Optional<String> buildIndexIfNecessary(final JanusGraphManagement jgm,
                                                   final String name,
                                                   final Class<? extends Element> elementType,
                                                   final PropertyKey propertyPath,
                                                   final JanusGraphSchemaType schemaType)
    {
        return buildIndexIfNecessary(jgm, name, elementType, propertyPath, schemaType, false);

    }
    
    private Optional<String> buildIndexIfNecessary(final JanusGraphManagement jgm,
                                                   final String name,
                                                   final Class<? extends Element> elementType,
                                                   final PropertyKey propertyPath,
                                                   final JanusGraphSchemaType schemaType,
                                                   final boolean unique)
    {
        if (jgm.containsGraphIndex(name))
        {
            return Optional.empty();
        }

        JanusGraphManagement.IndexBuilder indexBuilder = jgm.buildIndex(name, elementType);
        if (propertyPath != null)
        {
            indexBuilder = indexBuilder.addKey(propertyPath);
        }
        if (schemaType != null)
        {
            indexBuilder = indexBuilder.indexOnly(schemaType);
        }
        if (unique)
        {
            indexBuilder = indexBuilder.unique();
        }

        JanusGraphIndex janusGraphIndex = indexBuilder.buildCompositeIndex();
        return Optional.of(janusGraphIndex.name());
    }

    private void makeEdgeLabelIfDoesNotExist(final JanusGraphManagement jgm,
                                             final String name,
                                             final Multiplicity multiplicity)
    {
        if (jgm.containsEdgeLabel(name))
        {
            return;
        }
        EdgeLabelMaker edgeLabelMaker = jgm.makeEdgeLabel(name);
        if (multiplicity != null)
        {
            edgeLabelMaker = edgeLabelMaker.multiplicity(multiplicity);
        }

        edgeLabelMaker.make();
    }

    private void makeVertexLabelIfDoesNotExist(final JanusGraphManagement jgm,
                                               final String name)
    {
        if (jgm.containsVertexLabel(name))
        {
            return;
        }
        jgm.makeVertexLabel(name).make();
    }

    private void makePropertyKeyIfDoesNotExist(final JanusGraphManagement jgm,
                                               final String name,
                                               final Class<?> dataType)
    {
        makePropertyKeyIfDoesNotExist(jgm, name, dataType, null);
    }

    private void makePropertyKeyIfDoesNotExist(final JanusGraphManagement jgm,
                                               final String name,
                                               final Class<?> dataType,
                                               final Cardinality cardinality)
    {
        if (jgm.containsPropertyKey(name))
        {
            return;
        }

        PropertyKeyMaker propertyKeyMaker = jgm.makePropertyKey(name).dataType(dataType);
        if (cardinality != null)
        {
            propertyKeyMaker = propertyKeyMaker.cardinality(cardinality);
        }
        propertyKeyMaker.make();
    }

}
