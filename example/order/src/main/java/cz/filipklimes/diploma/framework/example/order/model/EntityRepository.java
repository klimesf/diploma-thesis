package cz.filipklimes.diploma.framework.example.order.model;

import java.util.*;

public abstract class EntityRepository<E extends Entity>
{

    protected final TreeMap<Long, E> entities = new TreeMap<>();

    public Collection<E> findAll()
    {
        return Collections.unmodifiableCollection(entities.values());
    }

    public E findById(final long entityId)
    {
        if (entityId < 1) {
            throw new IllegalArgumentException("ID must be greater than zero");
        }
        return entities.get(entityId);
    }

    public void save(final E entity)
    {
        synchronized (entities) {
            Long newKey = entities.lastKey() + 1;
            entity.setId(newKey);
            entities.put(newKey, entity);
        }
    }

}
