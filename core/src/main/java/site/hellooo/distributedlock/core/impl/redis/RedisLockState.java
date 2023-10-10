package site.hellooo.distributedlock.core.impl.redis;

import site.hellooo.distributedlock.core.LockState;
import site.hellooo.distributedlock.core.common.NetworkUtils;
import site.hellooo.distributedlock.core.common.ProcessUtils;

public class RedisLockState implements LockState<String> {

    private String identifier;
    private String value;

    public RedisLockState(String identifier) {
        this.identifier = identifier;
        this.value = generateDefaultValue();
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
