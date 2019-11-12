package org.opencypher.gremlin.neo4j.ogm.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.StatementRunner;
import org.neo4j.driver.v1.exceptions.ClientException;
import org.neo4j.ogm.exception.CypherException;
import org.neo4j.ogm.model.GraphModel;
import org.neo4j.ogm.model.GraphRowListModel;
import org.neo4j.ogm.model.RestModel;
import org.neo4j.ogm.model.RowModel;
import org.neo4j.ogm.request.DefaultRequest;
import org.neo4j.ogm.request.GraphModelRequest;
import org.neo4j.ogm.request.GraphRowListModelRequest;
import org.neo4j.ogm.request.Request;
import org.neo4j.ogm.request.RestModelRequest;
import org.neo4j.ogm.request.RowModelRequest;
import org.neo4j.ogm.request.Statement;
import org.neo4j.ogm.response.EmptyResponse;
import org.neo4j.ogm.response.Response;
import org.opencypher.gremlin.neo4j.ogm.response.GremlinEntityAdapter;
import org.opencypher.gremlin.neo4j.ogm.response.GremlinModelResponse;
import org.opencypher.gremlin.neo4j.ogm.response.GremlinRowModelResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GremlinRequest implements Request
{

    private static final Logger logger = LoggerFactory.getLogger(GremlinRequest.class);

    private final StatementRunner statementRunner;
    private final GremlinEntityAdapter entityAdapter;

    public GremlinRequest(StatementRunner statementRunner,
                          GremlinEntityAdapter entityAdapter)
    {
        this.statementRunner = statementRunner;
        this.entityAdapter = entityAdapter;
    }

    @Override
    public Response<GraphModel> execute(GraphModelRequest query)
    {
        if (query.getStatement().length() == 0)
        {
            return new EmptyResponse<>();
        }
        return new GremlinModelResponse(executeRequest(query), entityAdapter);
    }

    @Override
    public Response<RowModel> execute(RowModelRequest query)
    {
        if (query.getStatement().length() == 0)
        {
            return new EmptyResponse();
        }
        return new GremlinRowModelResponse(executeRequest(query), entityAdapter);
    }

    @Override
    public Response<RowModel> execute(DefaultRequest query)
    {
        final List<RowModel> rowModels = new ArrayList<>();
        String[] columns = null;
        for (Statement statement : query.getStatements())
        {

            StatementResult result = executeRequest(statement);

            if (columns == null)
            {
                try
                {
                    List<String> columnSet = result.keys();
                    columns = columnSet.toArray(new String[columnSet.size()]);
                }
                catch (ClientException e)
                {
                    throw new CypherException(e.code(), e.getMessage(), e);
                }
            }
            try (GremlinRowModelResponse rowModelResponse = new GremlinRowModelResponse(result, entityAdapter))
            {
                RowModel model;
                while ((model = rowModelResponse.next()) != null)
                {
                    rowModels.add(model);
                }
                result.consume();
            }
        }

        return new MultiStatementBasedResponse(columns, rowModels);
    }

    @Override
    public Response<GraphRowListModel> execute(GraphRowListModelRequest query)
    {
        return null;
    }

    @Override
    public Response<RestModel> execute(RestModelRequest query)
    {
        return null;
    }

    private org.neo4j.driver.v1.StatementResult executeRequest(Statement query)
    {
        Map<String, Object> parameterMap = query.getParameters();
        String cypherStatement = query.getStatement();
        logger.debug("Request: {} with params {}", cypherStatement, parameterMap);

        return statementRunner.run(cypherStatement, parameterMap);
    }

    private static class MultiStatementBasedResponse implements Response<RowModel>
    {
        // This implementation is not good, but it preserved the current
        // behaviour while fixing another bug.
        // While the statements executed in
        // org.neo4j.ogm.drivers.bolt.request.BoltRequest.execute(org.neo4j.ogm.request.DefaultRequest)
        // might return different columns, only the ones of the first result are
        // used. :(
        private final String[] columns;
        private final List<RowModel> rowModels;

        private int currentRow = 0;

        MultiStatementBasedResponse(String[] columns,
                                    List<RowModel> rowModels)
        {
            this.columns = columns;
            this.rowModels = rowModels;
        }

        @Override
        public RowModel next()
        {
            if (currentRow < rowModels.size())
            {
                return rowModels.get(currentRow++);
            }
            return null;
        }

        @Override
        public void close()
        {
        }

        @Override
        public String[] columns()
        {
            return this.columns;
        }
    }

}
