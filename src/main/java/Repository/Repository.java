package Repository;

import Domain.Entitate;
import Exceptions.DuplicateEntityException;
import Exceptions.ItemNotFoundException;
import Exceptions.RepositoryException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Repository<T extends Entitate> implements IRepository<T> {

    ArrayList<T> entities = new ArrayList<>();

    @Override
    public void add(T entity) throws RepositoryException, DuplicateEntityException {
        if (entity == null)
            throw new RepositoryException("Entitatea este null, nu este posibila adaugarea.");
        if (find(entity.getId()) != null) {
            throw new DuplicateEntityException("Exista entitate cu id :" + entity.getId() + "!");
        } else
            entities.add(entity);
    }

    @Override
    public void remove(int id) throws ItemNotFoundException {
        T toDelete = find(id);
        if (toDelete != null)
            entities.remove(toDelete);
        else
            throw new ItemNotFoundException("Aceasta entitate nu exista!");
    }

    @Override
    public T find(int id) {
        for (T e : entities) {
            if (e.getId() == id)
                return e;
        }
        return null;
    }

    @Override
    public Collection<T> getAll() {
        return entities;
    }

    @Override
    public Iterator<T> iterator() {
        return entities.iterator();
    }
}