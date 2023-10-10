package site.hellooo.distributedlock.core;

import site.hellooo.distributedlock.core.enums.Coordinator;
import site.hellooo.distributedlock.core.enums.LockType;

import java.util.concurrent.locks.Lock;

public interface DistributedLock extends Lock {

    LockType lockType();

    Coordinator coordinatorType();
}
