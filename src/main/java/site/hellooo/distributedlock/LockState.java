package site.hellooo.distributedlock;

import java.io.Serializable;

public interface LockState<T> extends Serializable {

    String getName();

    void setName(String name);

    T getState();

    void setState(T state);

}
