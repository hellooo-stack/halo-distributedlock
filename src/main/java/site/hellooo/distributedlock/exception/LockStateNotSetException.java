package site.hellooo.distributedlock.exception;

import java.io.IOException;

public class LockStateNotSetException extends IOException {
    public LockStateNotSetException() {

    }

    public LockStateNotSetException(String message) {
        super(message);
    }
}
