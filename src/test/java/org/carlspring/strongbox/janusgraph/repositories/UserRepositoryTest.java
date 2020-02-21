package org.carlspring.strongbox.janusgraph.repositories;

import org.carlspring.strongbox.janusgraph.app.Application;
import org.carlspring.strongbox.janusgraph.domain.User;
import org.carlspring.strongbox.janusgraph.domain.UserEntity;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphTransaction;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.opencypher.gremlin.neo4j.driver.GremlinDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import static org.apache.tinkerpop.gremlin.process.traversal.P.eq;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
public class UserRepositoryTest
{

    @Inject
    private JanusGraph janusGraph;

    @Inject
    private UserRepository userRepository;


//    @Test
//    public void queriesShouldWork()
//    {
//        String uuid = createUserVertex();
//
//        Driver driver = GremlinDatabase.driver(janusGraph.traversal());
//
//        try (Session session = driver.session())
//        {
//            StatementResult result = session.run("MATCH (u:User { username:\"testuser\" }) RETURN u");
//            Value value = result.single().get(0);
//            Node node = value.asNode();
//
//            //!assertEquals("topsecret", node.get("password").asString());
//            System.out.println();
//        }
//
////        GraphTraversalSource g = janusGraph.traversal();
////
////        Map<String, Object> vertex1 = g.V().hasLabel(User.LABEL).has("path", eq("org/carlspring/test-artifact.jar")).project("ac").next();
////        System.out.println(vertex1);
////
////        Map<String, Object> vertex2 = g.V().has("path", eq("org/carlspring/test-artifact.jar")).hasLabel("User").project("ac").next();
////        System.out.println(vertex2);
//    }

    @Test
    public void crudShouldWork()
    {
        String uuid = UUID.randomUUID().toString();
        String username = "testuser" + uuid;

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword("password");
        user.setEnabled(true);
        user.setLastUpdated(new Date());
        user.setUuid(uuid);

        // Create
        UserEntity userSaved = userRepository.save(user);
        assertEquals(user.getUuid(), userSaved.getUuid());

        // Get
        UserEntity byUsername = userRepository.findByUsername(username);
        assertEquals(user.getUsername(), byUsername.getUsername());

        // Update
        user.setEnabled(false);
        userRepository.save(user);

        UserEntity updatedUser = userRepository.findByUsername(username);
        assertFalse(updatedUser.isEnabled());

        // Delete
        userRepository.deleteById(updatedUser.getUuid());

        assertNull(userRepository.findByUsername(username));
    }
    
}
