package site.hellooo.distributedlock.exception;

import java.io.IOException;

public class LockStateRemoveExpiredException extends IOException {
    public LockStateRemoveExpiredException() {

    }

    public LockStateRemoveExpiredException(String message) {
        super(message);
    }
}
