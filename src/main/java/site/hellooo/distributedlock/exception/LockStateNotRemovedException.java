package site.hellooo.distributedlock.exception;

import java.io.IOException;

public class LockStateNotRemovedException extends IOException {
    public LockStateNotRemovedException() {

    }

    public LockStateNotRemovedException(String message) {
        super(message);
    }
}
