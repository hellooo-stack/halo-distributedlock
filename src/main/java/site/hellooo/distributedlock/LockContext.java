package site.hellooo.distributedlock;

import site.hellooo.distributedlock.config.LockOptions;

public interface LockContext {
    LockOptions lockOptions();

    Thread holdingThread();
}
