package org.carlspring.strongbox.janusgraph.repositories;

import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinatesEntity;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtifactCoordinatesRepository extends CrudRepository<ArtifactCoordinatesEntity, String>
{

    @Query("MATCH (ac:ArtifactCoordinates {path:$path}) RETURN ac")
    ArtifactCoordinatesEntity findByPath(String path);

}
