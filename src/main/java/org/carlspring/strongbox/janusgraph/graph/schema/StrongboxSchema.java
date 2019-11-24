package org.carlspring.strongbox.janusgraph.graph.schema;

import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.DatabaseSchema;
import org.carlspring.strongbox.janusgraph.graph.schema.changesets.ChangeSet;
import org.carlspring.strongbox.janusgraph.reposiotries.DatabaseSchemaRepository;
import org.janusgraph.core.JanusGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author Przemyslaw Fusik
 */
@Component
public class StrongboxSchema
        implements InitializingBean
{

    private static final Logger logger = LoggerFactory.getLogger(StrongboxSchema.class);

    private final DatabaseSchemaRepository databaseSchemaRepository;

    private final NavigableSet<ChangeSet> changeSets;

    private final JanusGraph janusGraph;

    public StrongboxSchema(final DatabaseSchemaRepository databaseSchemaRepository,
                           final NavigableSet<ChangeSet> changeSets,
                           final JanusGraph janusGraph)
    {
        this.databaseSchemaRepository = databaseSchemaRepository;
        this.changeSets = changeSets;
        this.janusGraph = janusGraph;
    }

    @Override
    public void afterPropertiesSet()
    {
        validateChangeSets();
        DatabaseSchema databaseSchema = getOrCreateDatabaseSchema();
        applyChangeSets(databaseSchema);
    }

    void applyChangeSets(final DatabaseSchema databaseSchema)
    {

        final SortedSet<org.carlspring.strongbox.janusgraph.domain.ChangeSet> appliedChangeSets = databaseSchema.getChangeSets();
        final NavigableSet<ChangeSet> changeSetsToApply =
                CollectionUtils.isEmpty(appliedChangeSets) ? changeSets : filterNotApplied(appliedChangeSets.last());

        logger.info(String.format("Changes to be applied: Size = [%d]. Values = [%s]",
                                  changeSetsToApply.size(),
                                  changeSetsToApply.stream()
                                                   .map(cs -> Pair.of(cs.name(), cs.getOrder()))
                                                   .collect(Collectors.toList())));

        if (changeSetsToApply.size() == 0)
        {
            return;
        }

        try
        {
            changeSetsToApply.stream()
                             .forEach(cs -> cs.apply(janusGraph));
            appliedChangeSets.addAll(mapChangeSetsToNodes(changeSetsToApply));
            databaseSchemaRepository.save(databaseSchema);
        }
        catch (Exception ex)
        {
            // TODO follow up task: ChangeSet should have rollback function
            throw ex;
        }
    }

    private NavigableSet<ChangeSet> filterNotApplied(final org.carlspring.strongbox.janusgraph.domain.ChangeSet lastAppliedChangeSet)
    {
        ChangeSet lastCounterpart = changeSets.stream()
                                              .filter(cs -> cs.getOrder() ==
                                                            lastAppliedChangeSet.getOrder() &&
                                                            Objects.equals(cs.name(),
                                                                           lastAppliedChangeSet.getName()))
                                              .findFirst()
                                              .orElseThrow(() -> new IllegalStateException(
                                                      String.format(
                                                              "Unable to find applied changeSet with name = [%s] and order = [%d]",
                                                              lastAppliedChangeSet.getName(),
                                                              lastAppliedChangeSet.getOrder())));

        return this.changeSets.tailSet(lastCounterpart, false);
    }

    DatabaseSchema getOrCreateDatabaseSchema()
    {
        DatabaseSchema databaseSchema;
        long count = countDatabaseSchemas();

        switch ((int) count)
        {
            case 0:
                databaseSchema = new DatabaseSchema();
                databaseSchema.setUuid(UUID.randomUUID().toString());
                databaseSchema.setChangeSets(new TreeSet<>());
                databaseSchema = databaseSchemaRepository.save(databaseSchema);
                Assert.isTrue(countDatabaseSchemas() == 1, "DatabaseSchema not persisted");
                break;
            case 1:
                databaseSchema = databaseSchemaRepository.findAll().iterator().next();
                break;
            default:
                throw new IllegalStateException(
                        String.format("Database schemas size = [%d]. Expected one database schema", count));
        }

        return databaseSchema;
    }

    void validateChangeSets()
    {
        int previousOrder = -1;
        for (ChangeSet changeSet : changeSets)
        {
            int currentOrder = changeSet.getOrder();
            String changeSetName = changeSet.name();

            Assert.state(currentOrder >= 0,
                         String.format("ChangeSet [%s] order should be greater or equal to 0 but was [%d]",
                                       changeSetName, currentOrder));
            Assert.state(currentOrder != previousOrder,
                         String.format("Two ChangeSets should not have the same order value. ChangeSets [%s]",
                                       changeSets.stream()
                                                 .map(cs -> Pair.of(cs.name(), cs.getOrder()))
                                                 .collect(Collectors.toList())));
            int expectedOrder = previousOrder + 1;
            Assert.state(expectedOrder == currentOrder,
                         String.format(
                                 "ChangeSets should be incremental by 1 but was: previousOrder [%d], currentOrder [%d],  changeSet name [%s]",
                                 previousOrder, currentOrder, changeSetName)
            );
            previousOrder = currentOrder;
        }
    }

    private long countDatabaseSchemas()
    {
        GraphTraversalSource g = janusGraph.traversal();
        GraphTraversal<Vertex, Long> databaseSchemas = g.V().hasLabel(DatabaseSchema.class.getSimpleName()).count();
        Long count = databaseSchemas.next();
        g.tx().commit();
        return count;
    }

    private Set<org.carlspring.strongbox.janusgraph.domain.ChangeSet> mapChangeSetsToNodes(final NavigableSet<ChangeSet> changeSets)
    {
        return changeSets.stream().map(
                cs -> org.carlspring.strongbox.janusgraph.domain.ChangeSet.build(cs.name(), cs.getOrder())).collect(
                Collectors.toSet());
    }

}


