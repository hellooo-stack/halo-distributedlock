package site.hellooo.distributedlock.core.exception;

public class GenericRuntimeLockException extends RuntimeException {
    public GenericRuntimeLockException() {
    }

    public GenericRuntimeLockException(String message) {
        super(message);
    }
}
