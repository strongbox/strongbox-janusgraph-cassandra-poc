package org.opencypher.gremlin.neo4j.ogm;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphTransaction;
import org.janusgraph.core.TransactionBuilder;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.driver.AbstractConfigurableDriver;
import org.neo4j.ogm.request.Request;
import org.neo4j.ogm.transaction.Transaction;
import org.neo4j.ogm.transaction.Transaction.Type;
import org.neo4j.ogm.transaction.TransactionManager;
import org.opencypher.gremlin.neo4j.driver.CypherGremlinStatementRunner;
import org.opencypher.gremlin.neo4j.ogm.request.GremlinRequest;
import org.opencypher.gremlin.neo4j.ogm.transaction.GremlinTransaction;

public class JanusGraphDriver extends AbstractConfigurableDriver
{

    private final JanusGraph graph;

    public JanusGraphDriver(JanusGraph graph)
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
        return new GremlinRequest(new CypherGremlinStatementRunner((GremlinTransaction) transaction));
    }

    @Override
    public Configuration getConfiguration()
    {
        return null;
    }

    @Override
    protected String getTypeSystemName()
    {
        return JanusGraphDriver.class.getName() + ".types";
    }

}
