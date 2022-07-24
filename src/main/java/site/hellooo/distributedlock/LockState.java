package site.hellooo.distributedlock;

import java.io.Serializable;

public interface LockState<T> extends Serializable {

    //    get the identifier representing this state
//    such as a redis key, a zookeeper path, a file name
    String getIdentifier();

    void setIdentifier(String identifier);

    //    get the value in the state
    T getValue();

    void setValue(T value);
}
