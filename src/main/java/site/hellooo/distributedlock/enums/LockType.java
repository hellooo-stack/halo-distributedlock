package site.hellooo.distributedlock.enums;

public enum LockType {

    REENTRANT("reentrant");

    private final String name;

    LockType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
