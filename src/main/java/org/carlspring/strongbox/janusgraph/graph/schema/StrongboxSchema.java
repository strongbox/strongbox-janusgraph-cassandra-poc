package org.carlspring.strongbox.janusgraph.graph.schema;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinates;
import org.carlspring.strongbox.janusgraph.domain.ArtifactEntry;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.VertexLabel;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.core.schema.JanusGraphManagement;
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
            ManagementSystem.awaitGraphIndexStatus(jg, janusGraphIndex).call();
        }
        
    }

    protected Set<String> createIndexes(JanusGraph jg, JanusGraphManagement jgm) throws InterruptedException
    {
        Set<String> result = new HashSet<>();
        
        PropertyKey propertyPath = jgm.getPropertyKey("path");
        VertexLabel vertexLabel = jgm.getVertexLabel(ArtifactCoordinates.class.getSimpleName());
        
        result.add(jgm.buildIndex(ArtifactCoordinates.class.getSimpleName() + ".path", Vertex.class).addKey(propertyPath).indexOnly(vertexLabel).buildCompositeIndex().name());
        
        return result;
    }

    private void applySchemaChanges(JanusGraphManagement jgm)
    {
        // Properties
        jgm.makePropertyKey("uuid").dataType(String.class).make();
        jgm.makePropertyKey("storageId").dataType(String.class).make();
        jgm.makePropertyKey("repositoryId").dataType(String.class).make();
        jgm.makePropertyKey("sizeInBytes").dataType(Long.class).make();
        jgm.makePropertyKey("created").dataType(Date.class).make();
        jgm.makePropertyKey("tags").dataType(String.class).cardinality(Cardinality.SET).make();

        PropertyKey propertyPath = jgm.makePropertyKey("path").dataType(String.class).make();
        jgm.makePropertyKey("version").dataType(String.class).make();

        // Vertices
        jgm.makeVertexLabel(ArtifactEntry.class.getSimpleName()).make();
        jgm.makeVertexLabel(ArtifactCoordinates.class.getSimpleName()).make();

        // Edges
        jgm.makeEdgeLabel(ArtifactEntry.class.getSimpleName() + "#" + ArtifactCoordinates.class.getSimpleName())
           .multiplicity(Multiplicity.MANY2ONE)
           .make();
    }

}
