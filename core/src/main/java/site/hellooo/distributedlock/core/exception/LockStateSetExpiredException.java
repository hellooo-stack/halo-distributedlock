package site.hellooo.distributedlock.core.exception;

import java.io.IOException;

public class LockStateSetExpiredException extends IOException {
    public LockStateSetExpiredException() {

    }

    public LockStateSetExpiredException(String message) {
        super(message);
    }
}
