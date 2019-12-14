package org.carlspring.strongbox.janusgraph.gremlin.dsl;

import java.util.function.Function;

import org.apache.tinkerpop.gremlin.process.traversal.Traverser;

public interface EntityTraversalExceptions
{

    Function<Traverser<Object>, Object> UUID_PROPERTY_REQUIRED = (t) -> {
        throw new IllegalStateException("The uuid property value is null");
    };

    Function<Traverser<Object>, Object> INVALID_MANY_TO_ONE = (t) -> {
        throw new IllegalStateException("Invalid many-to-one relation.");
    };
}
