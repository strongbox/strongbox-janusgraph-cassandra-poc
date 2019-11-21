package org.carlspring.strongbox.janusgraph.reposiotries;

import java.util.List;

import org.carlspring.strongbox.janusgraph.domain.ArtifactEntry;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtifactEntryRepository extends CrudRepository<ArtifactEntry, String>
{

    @Query("MATCH (ac:`ArtifactCoordinates`)<-[aeac:`ArtifactEntry#ArtifactCoordinates`]-(ae:`ArtifactEntry`) WHERE ac.path=$path RETURN ae, aeac, ac")
    ArtifactEntry findByPath(String path);
 
    @Query("MATCH (dependency:`ArtifactEntry` {uuid:$artifactEntryUuid})<-[:`ArtifactDependency`*]-(subject:`ArtifactEntry`) RETURN subject")
    List<ArtifactEntry> findAllDependentArtifactEntries(String artifactEntryUuid);
}
