package site.hellooo.distributedlock;

import site.hellooo.distributedlock.enums.Coordinator;
import site.hellooo.distributedlock.exception.LockStateNotRemovedException;
import site.hellooo.distributedlock.exception.LockStateNotSetException;
import site.hellooo.distributedlock.exception.LockStateRemoveExpiredException;
import site.hellooo.distributedlock.exception.LockStateSetExpiredException;

public interface LockHandler {

    void setState(LockState<?> lockState, LockContext lockContext) throws LockStateNotSetException;

    void setStateExpires(LockState<?> lockState, LockContext lockContext, long expireMilliseconds) throws LockStateSetExpiredException;

    void removeState(LockState<?> lockState, LockContext lockContext) throws LockStateNotRemovedException;

    void removeStateExpires(LockState<?> lockState, LockContext lockContext, long expireMilliseconds) throws LockStateRemoveExpiredException;

    Coordinator coordinatorType();
}
