package site.hellooo.distributedlock.core.enums;

public enum Coordinator {

    REDIS_SINGLETON("redis_singleton"),
    REDIS_CLUSTER("redis_cluster"),
    ZOOKEEPER("zookeeper");

    private final String name;

    Coordinator(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
