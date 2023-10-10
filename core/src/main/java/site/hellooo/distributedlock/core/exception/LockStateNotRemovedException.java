package site.hellooo.distributedlock.core.exception;

import java.io.IOException;

public class LockStateNotRemovedException extends IOException {
    public LockStateNotRemovedException() {

    }

    public LockStateNotRemovedException(String message) {
        super(message);
    }

    public LockStateNotRemovedException(Throwable cause) {
        super(cause);
    }

    public LockStateNotRemovedException(String message, Throwable cause) {
        super(message, cause);
    }
}
