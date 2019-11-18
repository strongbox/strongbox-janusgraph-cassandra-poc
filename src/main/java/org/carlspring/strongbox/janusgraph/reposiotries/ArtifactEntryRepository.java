package org.carlspring.strongbox.janusgraph.reposiotries;

import org.carlspring.strongbox.janusgraph.domain.ArtifactEntry;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtifactEntryRepository extends CrudRepository<ArtifactEntry, String>
{

    @Query("MATCH (ac:`ArtifactCoordinates`)<-[aeac:`ArtifactEntry#ArtifactCoordinates`]-(ae:`ArtifactEntry`) WHERE ac.path=$path RETURN ae, aeac, ac")
    ArtifactEntry findByPath(String path);
    
}
