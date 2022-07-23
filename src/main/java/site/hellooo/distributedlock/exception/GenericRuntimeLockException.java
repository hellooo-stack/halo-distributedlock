package site.hellooo.distributedlock.exception;

public class GenericRuntimeLockException extends RuntimeException{
    public GenericRuntimeLockException() {}

    public GenericRuntimeLockException(String message) {
        super(message);
    }
}
