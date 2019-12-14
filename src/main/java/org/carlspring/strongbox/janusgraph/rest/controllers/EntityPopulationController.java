package org.carlspring.strongbox.janusgraph.rest.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.RandomUtils;
import org.carlspring.strongbox.janusgraph.domain.ArtifactDependency;
import org.carlspring.strongbox.janusgraph.domain.ArtifactEntity;
import org.carlspring.strongbox.janusgraph.repositories.ArtifactRepository;
import org.carlspring.strongbox.janusgraph.rest.request.EntityPopulationRequest;
import org.carlspring.strongbox.janusgraph.util.EntityGeneratorUtil;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/entities")
public class EntityPopulationController
{

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityPopulationController.class);

    @Inject
    private ArtifactRepository artifactEntryRepository;
    @Inject
    private SessionFactory sessionFactory;

    @GetMapping
    @Transactional
    public ResponseEntity createEntities(EntityPopulationRequest requestBody)
    {
        int artifactsToCreate = requestBody.getArtifacts();
        int dependenciesToCreate = requestBody.getDependencies();

        if (artifactsToCreate < 0 || dependenciesToCreate < 0)
        {
            return ResponseEntity.unprocessableEntity().build();
        }

        List<ArtifactEntity> artifactEntries = new ArrayList<>(artifactsToCreate);
        List<ArtifactDependency> artifactDependencies = new ArrayList<>(dependenciesToCreate);

        for (int i = 0; i < artifactsToCreate; ++i)
        {
            artifactEntries.add(EntityGeneratorUtil.createRandomArtifact());
        }

        for (int j = 0; j < dependenciesToCreate; ++j)
        {
            int toArtifact, fromArtifact = RandomUtils.nextInt(0, artifactsToCreate);
            do
            {
                toArtifact = RandomUtils.nextInt(0, artifactsToCreate);
            } while (fromArtifact != toArtifact);
            ArtifactDependency dependency = EntityGeneratorUtil.createArtifactDependency(
                    artifactEntries.get(fromArtifact), artifactEntries.get(toArtifact));
            artifactDependencies.add(dependency);
        }

        for (int k = 0; k < artifactEntries.size(); ++k)
        {
            LOGGER.info("Saving entry {}", k);
            artifactEntryRepository.save(artifactEntries.get(k));
        }

        Session session = sessionFactory.openSession();
        for (ArtifactDependency dependency : artifactDependencies)
        {
            session.save(dependency);
        }

        return ResponseEntity.ok().build();
    }
}
