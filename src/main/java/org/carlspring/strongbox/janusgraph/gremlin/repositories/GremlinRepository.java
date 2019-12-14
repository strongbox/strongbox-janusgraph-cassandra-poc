package org.carlspring.strongbox.janusgraph.gremlin.repositories;

import java.util.Optional;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.carlspring.strongbox.janusgraph.domain.DomainObject;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversal;
import org.carlspring.strongbox.janusgraph.gremlin.dsl.EntityTraversalSource;
import org.carlspring.strongbox.janusgraph.gremlin.repositories.adapters.EntityTraversalAdapter;
import org.janusgraph.core.JanusGraph;
import org.springframework.data.repository.CrudRepository;

/**
 * @author sbespalov
 *
 */
public abstract class GremlinRepository<S, E extends DomainObject> implements CrudRepository<E, String>
{

    @Inject
    private JanusGraph janusGraph;

    protected abstract EntityTraversalAdapter<S, E> adapter();

    protected String label()
    {
        return adapter().getLabel();
    }

    protected EntityTraversalSource g()
    {
        return janusGraph.traversal(EntityTraversalSource.class);
    }

    protected abstract EntityTraversal<S, S> start(Supplier<EntityTraversalSource> g);

    public Optional<E> findById(String uuid)
    {
        EntityTraversal<S, E> traversal = start(this::g).findById(label(), uuid)
                                                        .map(adapter().fold());
        if (!traversal.hasNext())
        {
            return Optional.empty();
        }

        return Optional.of(traversal.next());
    }

    @Override
    public <S extends E> Iterable<S> saveAll(Iterable<S> entities)
    {
        throw new UnsupportedOperationException("TODO implement");
    }

    @Override
    public boolean existsById(String id)
    {
        throw new UnsupportedOperationException("TODO implement");
    }

    @Override
    public Iterable<E> findAll()
    {
        throw new UnsupportedOperationException("TODO implement");
    }

    @Override
    public Iterable<E> findAllById(Iterable<String> ids)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO implement");
    }

    @Override
    public long count()
    {
        throw new UnsupportedOperationException("TODO implement");
    }

    @Override
    public void deleteById(String id)
    {
        throw new UnsupportedOperationException("TODO implement");
    }

    @Override
    public void delete(E entity)
    {
        throw new UnsupportedOperationException("TODO implement");
    }

    @Override
    public void deleteAll(Iterable<? extends E> entities)
    {
        throw new UnsupportedOperationException("TODO implement");
    }

    @Override
    public void deleteAll()
    {
        throw new UnsupportedOperationException("TODO implement");
    }

}
