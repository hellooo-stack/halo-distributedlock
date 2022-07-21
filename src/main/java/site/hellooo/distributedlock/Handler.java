package site.hellooo.distributedlock;

public interface Handler {
    <T> void setState(LockState<T> lockState, LockContext lockContext);

    <T> void setState(LockState<T> lockState, LockContext lockContext, long expireMills);

    <T> void removeState(LockState<T> lock, LockContext lockContext);
}
