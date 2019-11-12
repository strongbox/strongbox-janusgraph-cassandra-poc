package org.opencypher.gremlin.neo4j.ogm.response;

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.ogm.model.GraphModel;

public class GremlinModelResponse extends GremlinResponse<GraphModel>
{

    private final GremlinGraphModelAdapter adapter;

    public GremlinModelResponse(StatementResult result,
                                GremlinEntityAdapter entityAdapter)
    {

        super(result);

        this.adapter = new GremlinGraphModelAdapter(entityAdapter);
    }

    @Override
    public GraphModel fetchNext()
    {
        if (result.hasNext())
        {
            return adapter.adapt(result.next().asMap());
        }
        return null;
    }
}