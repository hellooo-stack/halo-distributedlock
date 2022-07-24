package site.hellooo.distributedlock.impl.redis;

import site.hellooo.distributedlock.LockState;
import site.hellooo.distributedlock.common.NetworkUtils;
import site.hellooo.distributedlock.common.ProcessUtils;

public class RedisLockState implements LockState<String> {

    private String identifier;
    private String value;

    public RedisLockState(String identifier) {
        this.identifier = identifier;
    }

    public RedisLockState(String identifier, String value) {
        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getValue() {
        if (value == null) {
            this.value = generateDefaultValue();
        }

        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

//    returned value: "ip-processId-threadId"
    private String generateDefaultValue() {
        return NetworkUtils.getLocalIP() + "-" + ProcessUtils.getProcessId() + "-" + Thread.currentThread().getId();
    }
}
