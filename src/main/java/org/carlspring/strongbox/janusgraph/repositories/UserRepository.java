package org.carlspring.strongbox.janusgraph.repositories;

import org.carlspring.strongbox.janusgraph.domain.UserEntity;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
        extends CrudRepository<UserEntity, String>
{

//    @Query("MATCH (ac:`ArtifactCoordinates`)<-[aeac:`Artifact_ArtifactCoordinates`]-(ae:`Artifact`) WHERE ac.path=$path RETURN ae, aeac, ac")
//    UserEntity findByPath(String path);
//
//    @Query("MATCH (dependency:`ArtifactCoordinates`{uuid:$artifactCoordinatesUuid})<-[:`ArtifactDependency`*]-(subject:`Artifact`) RETURN subject")
//    List<UserEntity> findAllDependentArtifactEntries(String artifactCoordinatesUuid);

    @Query("MATCH (u:`User`) WHERE u.username=$username RETURN u")
    UserEntity findByUsername(String username);

}
