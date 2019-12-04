package org.carlspring.strongbox.janusgraph.reposiotries;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import org.assertj.core.util.Sets;
import org.carlspring.strongbox.janusgraph.app.Application;
import org.carlspring.strongbox.janusgraph.domain.ChangeSet;
import org.carlspring.strongbox.janusgraph.domain.DatabaseSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Przemyslaw Fusik
 */
@Transactional
@SpringBootTest(classes = Application.class)
class DatabaseSchemaRepositoryTest
{

    @Inject
    private DatabaseSchemaRepository databaseSchemaRepository;

    @BeforeEach
    public void before()
    {
        databaseSchemaRepository.deleteAll();
    }

    @Test
    public void crudShouldWork()
    {
        DatabaseSchema databaseSchema = new DatabaseSchema();
        String uuid = UUID.randomUUID().toString();
        databaseSchema.setUuid(uuid);
        databaseSchemaRepository.save(databaseSchema);

        Optional<DatabaseSchema> byId = databaseSchemaRepository.findById(uuid);
        assertThat(byId).isPresent();
        databaseSchema = byId.get();
        assertThat(databaseSchema.getUuid()).isEqualTo(uuid);
        assertThat(databaseSchema.getChangeSets()).isNullOrEmpty();

        ChangeSet changeSet = ChangeSet.build("first changeset", 0);

        databaseSchema.setChangeSets(Sets.newTreeSet(changeSet));
        databaseSchemaRepository.save(databaseSchema);

        byId = databaseSchemaRepository.findById(uuid);
        assertThat(byId).isPresent();
        databaseSchema = byId.get();
        assertThat(databaseSchema.getUuid()).isEqualTo(uuid);
        assertThat(databaseSchema.getChangeSets()).hasSize(1);
        ChangeSet appliedChangeSet = databaseSchema.getChangeSets().iterator().next();
        assertThat(appliedChangeSet.getName()).isEqualTo("first changeset");
        assertThat(appliedChangeSet.getOrder()).isEqualTo(0);

        ChangeSet changeSetSecond = ChangeSet.build("second changeset", 1);

        databaseSchema.getChangeSets().add(changeSetSecond);
        databaseSchemaRepository.save(databaseSchema);

        byId = databaseSchemaRepository.findById(uuid);
        assertThat(byId).isPresent();
        databaseSchema = byId.get();
        assertThat(databaseSchema.getUuid()).isEqualTo(uuid);
        assertThat(databaseSchema.getChangeSets()).hasSize(2);

        final Iterator<ChangeSet> iterator = databaseSchema.getChangeSets().iterator();

        appliedChangeSet = iterator.next();
        assertThat(appliedChangeSet.getName()).isEqualTo("first changeset");
        assertThat(appliedChangeSet.getOrder()).isEqualTo(0);

        appliedChangeSet = iterator.next();
        assertThat(appliedChangeSet.getName()).isEqualTo("second changeset");
        assertThat(appliedChangeSet.getOrder()).isEqualTo(1);

        databaseSchemaRepository.deleteById(uuid);
        byId = databaseSchemaRepository.findById(uuid);
        assertThat(byId).isEmpty();
    }

}