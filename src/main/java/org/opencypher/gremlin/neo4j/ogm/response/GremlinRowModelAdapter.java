package org.opencypher.gremlin.neo4j.ogm.response;

import org.neo4j.ogm.result.adapter.RowModelAdapter;

public class GremlinRowModelAdapter extends RowModelAdapter {

    private final GremlinEntityAdapter entityAdapter;

    public GremlinRowModelAdapter(GremlinEntityAdapter entityAdapter) {
        this.entityAdapter = entityAdapter;
    }

    @Override
    public boolean isPath(Object value) {
        return entityAdapter.isPath(value);
    }

    @Override
    public boolean isNode(Object value) {
        return entityAdapter.isNode(value);
    }

    @Override
    public boolean isRelationship(Object value) {
        return entityAdapter.isRelationship(value);
    }
}