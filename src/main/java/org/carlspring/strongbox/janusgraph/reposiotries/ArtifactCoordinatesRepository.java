package org.carlspring.strongbox.janusgraph.reposiotries;

import org.carlspring.strongbox.janusgraph.domain.ArtifactCoordinates;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtifactCoordinatesRepository extends CrudRepository<ArtifactCoordinates, String>
{

    @Query("MATCH (ac:ArtifactCoordinates {path:$path}) RETURN ac")
    ArtifactCoordinates findByPath(String path);

}
