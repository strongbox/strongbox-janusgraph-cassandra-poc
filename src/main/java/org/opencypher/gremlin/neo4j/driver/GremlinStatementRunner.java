package org.opencypher.gremlin.neo4j.driver;

import java.util.Map;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.StatementRunner;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.TypeSystem;
import org.opencypher.gremlin.client.CypherGremlinClient;

public class GremlinStatementRunner implements StatementRunner
{

    private Session session;

    public GremlinStatementRunner(CypherGremlinClient client)
    {
        this.session = new GremlinServerSession(null, client, new GremlinCypherValueConverter(false));
    }

    public StatementResult run(String statementTemplate,
                               Value parameters)
    {
        return session.run(statementTemplate, parameters);
    }

    public StatementResult run(String statementTemplate,
                               Map<String, Object> statementParameters)
    {
        return session.run(statementTemplate, statementParameters);
    }

    public StatementResult run(String statementTemplate,
                               Record statementParameters)
    {
        return session.run(statementTemplate, statementParameters);
    }

    public StatementResult run(String statementTemplate)
    {
        return session.run(statementTemplate);
    }

    public StatementResult run(Statement statement)
    {
        return session.run(statement);
    }

    public TypeSystem typeSystem()
    {
        return session.typeSystem();
    }

}
