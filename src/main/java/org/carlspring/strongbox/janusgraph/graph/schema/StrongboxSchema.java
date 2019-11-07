package org.carlspring.strongbox.janusgraph.graph.schema;

import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinates;
import org.carlspring.strongbox.janusgraph.domain.ArtifactEntry;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StrongboxSchema
{

    private static final Logger logger = LoggerFactory.getLogger(StrongboxSchema.class);

    @Inject
    public void createSchema(JanusGraphFactory.Builder jgf)
    {
        JanusGraph jg = jgf.open();
        JanusGraphManagement jgm = jg.openManagement();
        try
        {
            applySchemaChanges(jgm);
        }
        catch (Exception e)
        {
            logger.error("Failed to apply schema changes.", e);
            jgm.rollback();
            throw new RuntimeException("Failed to apply schema changes.", e);
        }
        
        logger.info(String.format("Schema: %n%s", jgm.printSchema()));
        
        jgm.commit();
    }

    private void applySchemaChanges(JanusGraphManagement jgm)
    {
        // Properties
        jgm.makePropertyKey("uuid").dataType(UUID.class).make();
        jgm.makePropertyKey("storageId").dataType(String.class).make();
        jgm.makePropertyKey("repositoryId").dataType(String.class).make();
        jgm.makePropertyKey("sizeInBytes").dataType(Long.class).make();
        jgm.makePropertyKey("created").dataType(Date.class).make();
        jgm.makePropertyKey("tags").dataType(String.class).cardinality(Cardinality.SET).make();

        jgm.makePropertyKey("path").dataType(String.class).make();
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
