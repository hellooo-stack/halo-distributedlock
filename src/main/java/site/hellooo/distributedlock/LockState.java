package site.hellooo.distributedlock;

import java.io.Serializable;

public interface LockState<T> extends Serializable {

    // return the identifier representing this state
    // such as a redis key, a zookeeper path, a file name, etc.
    String getIdentifier();

    void setIdentifier(String identifier);

    // return the value in the state
    T getValue();

    void setValue(T value);
}
