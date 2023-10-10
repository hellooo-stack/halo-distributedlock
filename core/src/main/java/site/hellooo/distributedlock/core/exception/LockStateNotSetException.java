package site.hellooo.distributedlock.core.exception;

import java.io.IOException;

public class LockStateNotSetException extends IOException {
    public LockStateNotSetException() {

    }

    public LockStateNotSetException(String message) {
        super(message);
    }

    public LockStateNotSetException(Throwable cause) {
        super(cause);
    }

    public LockStateNotSetException(String message, Throwable cause) {
        super(message, cause);
    }
}
