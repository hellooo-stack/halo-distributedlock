package site.hellooo.distributedlock.exception;

public class BuilderEssentialFieldNotSetException extends RuntimeException {
    public BuilderEssentialFieldNotSetException() {

    }

    public BuilderEssentialFieldNotSetException(String message) {
        super(message);
    }
}
