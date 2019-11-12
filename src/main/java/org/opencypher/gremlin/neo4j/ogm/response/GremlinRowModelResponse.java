package org.opencypher.gremlin.neo4j.ogm.response;

import java.util.Arrays;

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.ogm.model.RowModel;

public class GremlinRowModelResponse extends GremlinResponse<RowModel>
{

    private final GremlinRowModelAdapter adapter;

    public GremlinRowModelResponse(StatementResult result,
                                   GremlinEntityAdapter entityAdapter)
    {

        super(result);

        this.adapter = new GremlinRowModelAdapter(entityAdapter);
        this.adapter.setColumns(Arrays.asList(columns()));
    }

    @Override
    public RowModel fetchNext()
    {
        if (result.hasNext())
        {
            return adapter.adapt(result.next().asMap());
        }
        return null;
    }
}
