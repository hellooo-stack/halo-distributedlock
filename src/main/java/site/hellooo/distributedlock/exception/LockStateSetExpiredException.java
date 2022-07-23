package site.hellooo.distributedlock.exception;

import java.io.IOException;

public class LockStateSetExpiredException extends IOException {
    public LockStateSetExpiredException() {

    }
    public LockStateSetExpiredException(String message) {
        super(message);
    }
}
