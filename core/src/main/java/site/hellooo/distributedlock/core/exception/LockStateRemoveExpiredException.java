package site.hellooo.distributedlock.core.exception;

import java.io.IOException;

public class LockStateRemoveExpiredException extends IOException {
    public LockStateRemoveExpiredException() {

    }

    public LockStateRemoveExpiredException(String message) {
        super(message);
    }
}
