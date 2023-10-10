package site.hellooo.distributedlock.core;

import site.hellooo.distributedlock.core.enums.Coordinator;
import site.hellooo.distributedlock.core.exception.LockStateNotRemovedException;
import site.hellooo.distributedlock.core.exception.LockStateNotSetException;
import site.hellooo.distributedlock.core.exception.LockStateRemoveExpiredException;
import site.hellooo.distributedlock.core.exception.LockStateSetExpiredException;

public interface LockHandler {

    void setState(LockState<?> lockState, LockContext lockContext) throws LockStateNotSetException;

    void setStateExpires(LockState<?> lockState, LockContext lockContext, long expireMilliseconds) throws LockStateNotSetException, LockStateSetExpiredException;

    void removeState(LockState<?> lockState, LockContext lockContext) throws LockStateNotRemovedException;

    void removeStateExpires(LockState<?> lockState, LockContext lockContext, long expireMilliseconds) throws LockStateNotRemovedException, LockStateRemoveExpiredException;

    Coordinator coordinatorType();
}
