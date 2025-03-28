package Exceptions;

public class ItemNotFoundException extends RepositoryException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}