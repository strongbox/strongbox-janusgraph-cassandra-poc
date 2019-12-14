package org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.carlspring.strongbox.janusgraph.domain.DomainObject;

/**
 * @author sbespalov
 *
 * @param <E>
 */
public abstract class VertexEntityTraversalAdapter<E extends DomainObject> implements EntityTraversalAdapter<Vertex, E>
{

}
