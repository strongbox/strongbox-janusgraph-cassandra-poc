package org.carlspring.strongbox.janusgraph.graph.schema;

import java.util.NavigableSet;

import org.assertj.core.util.Sets;
import org.carlspring.strongbox.janusgraph.app.Application;
import org.carlspring.strongbox.janusgraph.graph.schema.changesets.ChangeSet;
import org.carlspring.strongbox.janusgraph.reposiotries.DatabaseSchemaRepository;
import org.janusgraph.core.JanusGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Przemyslaw Fusik
 */
@SpringBootTest(classes = Application.class)
class StrongboxSchemaTest
{

    private static final Logger logger = LoggerFactory.getLogger(StrongboxSchemaTest.class);

    @Autowired
    private DatabaseSchemaRepository databaseSchemaRepository;

    @Autowired
    private JanusGraph janusGraph;

    @Autowired
    private NavigableSet<ChangeSet> changeSets;

    @BeforeEach
    public void beforeEach()
    {

        ChangeSet changeSet1 = new ChangeSet()
        {

            @Override
            public int getOrder()
            {
                return 0;
            }

            @Override
            public Logger getLogger()
            {
                return logger;
            }
        };

        ChangeSet changeSet2 = new ChangeSet()
        {

            @Override
            public int getOrder()
            {
                return 0;
            }

            @Override
            public Logger getLogger()
            {
                return logger;
            }
        };

        strongboxSchema = new StrongboxSchema(databaseSchemaRepository,
                                              Sets.newTreeSet(changeSet1, changeSet2),
                                              janusGraph);
    }

    private StrongboxSchema strongboxSchema = new StrongboxSchema(databaseSchemaRepository, null, janusGraph);

    @Test
    void testGet()
    {
        // TODO
        strongboxSchema.afterPropertiesSet();
    }


}