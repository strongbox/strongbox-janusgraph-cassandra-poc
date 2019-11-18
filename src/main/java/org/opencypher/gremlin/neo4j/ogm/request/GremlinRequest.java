package org.opencypher.gremlin.neo4j.ogm.request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.carlspring.strongbox.janusgraph.domain.DomainEntity;
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
import org.opencypher.gremlin.translation.CypherAst;
import org.opencypher.gremlin.translation.groovy.GroovyPredicate;
import org.opencypher.gremlin.translation.translator.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.collection.parallel.ParIterableLike.FlatMap;

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
        logger.debug("Cypher: {} with params {}", cypherStatement, parameterMap);
        if (cypherStatement.contains("MERGE"))
        {
            cypherStatement = normalizeMergeStatement(cypherStatement, parameterMap);
        }

        cypherStatement = inlileParameters(cypherStatement, parameterMap);

        logger.debug("Cypher(normalized): {}", cypherStatement);
        CypherAst ast = CypherAst.parse(cypherStatement, parameterMap);
        Translator<String, GroovyPredicate> translator = Translator.builder()
                                                                   .gremlinGroovy()
                                                                   .enableCypherExtensions()
                                                                   .build();
        logger.debug("Gremlin: {}", ast.buildTranslation(translator));

        return statementRunner.run(cypherStatement, parameterMap);
    }

    protected String inlileParameters(String cypherStatement,
                                      Map<String, Object> parameterMap)
    {
        String placeholderFormat;
        Map<String, Object> params;
        Collection<Map<String, Object>> rows = (Collection<Map<String, Object>>) parameterMap.get("rows");        
        if (rows == null || rows.size() == 0)
        {
            //regular case
            placeholderFormat = "$%s";
            params = parameterMap;
        }
        else
        {
            //the case with `UNWIND {rows} as row ...`
            placeholderFormat = "row.%s";
            params = rows.iterator().next();
        }

        List<Pair<String, Object>> props = params.entrySet()
                                                 .stream()
                                                 .flatMap(GremlinRequest::flatten)
                                                 .collect(Collectors.toList());

        for (Pair<String, Object> p : props)
        {
            cypherStatement = cypherStatement.replace(String.format(placeholderFormat, p.getKey()),
                                                      inlinedValue(p.getValue()));
        }

        return cypherStatement;
    }

    protected String inlinedValue(Object value)
    {
        if (value instanceof Number) {
            return value.toString();
        }
            
        return "'" + value.toString() + "'";
    }
    
    private static Stream<Pair<String, Object>> flatten(Map.Entry<String, Object> root)
    {
        if (root.getValue() instanceof Map<?, ?>)
        {
            return ((Map<String, Object>) root.getValue()).entrySet()
                                                          .stream()
                                                          .map(e -> Pair.of(root.getKey() + "." + e.getKey(),
                                                                            e.getValue()))
                                                          .flatMap(GremlinRequest::flatten);
        }
        return Stream.of(Pair.of(root.getKey(), root.getValue()));
    }
    
    protected String normalizeMergeStatement(String cypherStatement,
                                             Map<String, Object> parameterMap)
    {
        //cleanup multiple labels for DomainEntity inheritance 
        cypherStatement = cypherStatement.replace(String.format(":`%s`", DomainEntity.class.getSimpleName()), "");
        
        if (!cypherStatement.contains("n=row.props"))
        {
            return cypherStatement;
        }
        
        Collection<Map<String, Object>> rows = (Collection<Map<String, Object>>) parameterMap.get("rows");
        if (rows == null || rows.size() == 0)
        {
            return cypherStatement;
        }

        Map<String, Object> row = rows.iterator().next();
        Map<String, Object> props = (Map<String, Object>) row.get("props");
        if (props == null || props.size() == 0)
        {
            return cypherStatement;
        }
       
        //specify concrete properties to set
        String propsClause = props.keySet()
                                  .stream()
                                  .map(p -> String.format("n.%s = row.props.%s", p, p))
                                  .reduce((p1,
                                           p2) -> p1 + "," + p2)
                                  .get();

        return cypherStatement.replace("n=row.props", propsClause);
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
