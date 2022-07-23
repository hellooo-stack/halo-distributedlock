package site.hellooo.distributedlock;

import site.hellooo.distributedlock.enums.Coordinator;
import site.hellooo.distributedlock.enums.LockType;

import java.util.concurrent.locks.Lock;

public interface DistributedLock extends Lock {

    LockType lockType();

    Coordinator coordinatorType();
}
