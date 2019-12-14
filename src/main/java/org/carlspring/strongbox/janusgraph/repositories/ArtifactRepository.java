package org.carlspring.strongbox.janusgraph.repositories;

import java.util.List;

import org.carlspring.strongbox.janusgraph.domain.ArtifactEntity;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtifactRepository extends CrudRepository<ArtifactEntity, String>
{

    @Query("MATCH (ac:`ArtifactCoordinates`)<-[aeac:`Artifact_ArtifactCoordinates`]-(ae:`Artifact`) WHERE ac.path=$path RETURN ae, aeac, ac")
    ArtifactEntity findByPath(String path);
 
    @Query("MATCH (dependency:`ArtifactCoordinates`{uuid:$artifactCoordinatesUuid})<-[:`ArtifactDependency`*]-(subject:`Artifact`) RETURN subject")
    List<ArtifactEntity> findAllDependentArtifactEntries(String artifactCoordinatesUuid);
}
