package Repository;

import Domain.Entitate;
import Exceptions.DuplicateEntityException;
import Exceptions.RepositoryException;

import java.util.Collection;

public interface IRepository<T extends Entitate> extends Iterable<T> {
    void add(T entity) throws RepositoryException, DuplicateEntityException;
    void remove(int id) throws RepositoryException;
    T find(int id);
    Collection<T> getAll();
}