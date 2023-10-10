package site.hellooo.distributedlock.core.exception;

public class BuilderEssentialFieldNotSetException extends RuntimeException {
    public BuilderEssentialFieldNotSetException() {

    }

    public BuilderEssentialFieldNotSetException(String message) {
        super(message);
    }
}
