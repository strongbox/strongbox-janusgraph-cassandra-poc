package org.carlspring.strongbox.janusgraph.graph.schema.changesets;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.schema.EdgeLabelMaker;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.core.schema.JanusGraphSchemaType;
import org.janusgraph.core.schema.PropertyKeyMaker;
import org.janusgraph.core.schema.SchemaAction;
import org.janusgraph.graphdb.database.management.ManagementSystem;
import org.slf4j.Logger;
import org.springframework.core.Ordered;

/**
 * @author Przemyslaw Fusik
 */
public interface ChangeSet
        extends Ordered, Comparable<ChangeSet>
{

    Logger getLogger();

    default String name()
    {
        return getClass().getName();
    }

    @Override
    default int compareTo(ChangeSet o)
    {
        return Integer.compare(getOrder(), o.getOrder());
    }

    default void apply(JanusGraph jg)
    {
        applySchemaChanges(jg);
        createAndEnableIndexes(jg);
    }

    default void applySchemaChanges(JanusGraph jg)
    {
        JanusGraphManagement jgm = jg.openManagement();
        try
        {
            applySchemaChanges(jgm);
            getLogger().info(String.format("Schema: %n%s", jgm.printSchema()));
            jgm.commit();
        }
        catch (Exception e)
        {
            getLogger().error("Failed to apply schema changes.", e);
            jgm.rollback();
            throw new RuntimeException("Failed to apply schema changes.", e);
        }

    }

    default void applySchemaChanges(JanusGraphManagement jgm)
    {
        // NOOP by default
    }

    default void createAndEnableIndexes(JanusGraph jg)
    {
        JanusGraphManagement jgm = jg.openManagement();
        Set<String> indexes;

        try
        {
            indexes = createIndexes(jgm);
            jgm.commit();
        }
        catch (Exception e)
        {
            getLogger().error("Failed to create indexes.", e);
            jgm.rollback();
            throw new RuntimeException("Failed to create indexes.", e);
        }

        for (String janusGraphIndex : indexes)
        {
            getLogger().info(String.format("Wait index [%s] to be registered.", janusGraphIndex));
            try
            {
                ManagementSystem.awaitGraphIndexStatus(jg, janusGraphIndex).call();
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while registering indexes.", e);
            }
        }

        jgm = jg.openManagement();
        try
        {
            enableIndexes(jgm, indexes);
            jgm.commit();
        }
        catch (Exception e)
        {
            getLogger().error("Failed to enable indexes.", e);
            jgm.rollback();
            throw new RuntimeException("Failed to enable indexes.", e);
        }
    }

    default Set<String> createIndexes(JanusGraphManagement jgm)
    {
        return Collections.emptySet();
    }

    default void enableIndexes(JanusGraphManagement jgm,
                               Set<String> indexes)
            throws ExecutionException, InterruptedException
    {
        for (String janusGraphIndex : indexes)
        {
            getLogger().info(String.format("Enabling index [%s].", janusGraphIndex));
            jgm.updateIndex(jgm.getGraphIndex(janusGraphIndex), SchemaAction.ENABLE_INDEX).get();
        }
    }

    default Optional<String> buildIndexIfNecessary(final JanusGraphManagement jgm,
                                                   final String name,
                                                   final Class<? extends Element> elementType,
                                                   final PropertyKey propertyPath,
                                                   final JanusGraphSchemaType schemaType)
    {
        return buildIndexIfNecessary(jgm, name, elementType, propertyPath, schemaType, false);

    }

    default Optional<String> buildIndexIfNecessary(final JanusGraphManagement jgm,
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

    default void makeEdgeLabelIfDoesNotExist(final JanusGraphManagement jgm,
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

    default void makeVertexLabelIfDoesNotExist(final JanusGraphManagement jgm,
                                               final String name)
    {
        if (jgm.containsVertexLabel(name))
        {
            return;
        }
        jgm.makeVertexLabel(name).make();
    }

    default void makePropertyKeyIfDoesNotExist(final JanusGraphManagement jgm,
                                               final String name,
                                               final Class<?> dataType)
    {
        makePropertyKeyIfDoesNotExist(jgm, name, dataType, null);
    }

    default void makePropertyKeyIfDoesNotExist(final JanusGraphManagement jgm,
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
