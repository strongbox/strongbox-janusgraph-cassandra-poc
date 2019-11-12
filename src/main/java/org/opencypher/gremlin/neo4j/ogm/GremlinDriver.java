package org.opencypher.gremlin.neo4j.ogm;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphTransaction;
import org.janusgraph.core.TransactionBuilder;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.driver.AbstractConfigurableDriver;
import org.neo4j.ogm.request.Request;
import org.neo4j.ogm.transaction.Transaction;
import org.neo4j.ogm.transaction.Transaction.Type;
import org.neo4j.ogm.transaction.TransactionManager;
import org.opencypher.gremlin.client.CypherGremlinClient;
import org.opencypher.gremlin.neo4j.driver.GremlinStatementRunner;
import org.opencypher.gremlin.neo4j.ogm.request.GremlinRequest;
import org.opencypher.gremlin.neo4j.ogm.response.GremlinEntityAdapter;
import org.opencypher.gremlin.neo4j.ogm.transaction.GremlinTransaction;

public class GremlinDriver extends AbstractConfigurableDriver
{

    private final JanusGraph graph;

    public GremlinDriver(JanusGraph graph)
    {
        this.graph = graph;
    }

    @Override
    public void configure(Configuration config)
    {

    }

    @Override
    public Function<TransactionManager, BiFunction<Type, Iterable<String>, Transaction>> getTransactionFactorySupplier()
    {
        return transactionManager -> (type,
                                      bookmarks) -> {
            TransactionBuilder txBuilder = graph.buildTransaction();
            if (Type.READ_ONLY.equals(type))
            {
                txBuilder.readOnly();
            }
            JanusGraphTransaction transaction = txBuilder.start();

            return new GremlinTransaction(transactionManager, transaction, type);
        };

    }

    @Override
    public void close()
    {

    }

    @Override
    public Request request(Transaction transaction)
    {
        Graph graph = ((GremlinTransaction) transaction).getNativeTransaction();
        CypherGremlinClient cypherGremlinClient = CypherGremlinClient.inMemory(graph.traversal());

        return new GremlinRequest(new GremlinStatementRunner(cypherGremlinClient),
                new GremlinEntityAdapter(typeSystem));
    }

    @Override
    public Configuration getConfiguration()
    {
        return null;
    }

    @Override
    protected String getTypeSystemName()
    {
        return GremlinDriver.class.getName() + ".types";
    }

}
