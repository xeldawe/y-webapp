package hu.davidder.webapp.core.base.order.enums;

public enum OrderStatus {

    PLACED("Placed"),
    APPROVED("Approved"),
    DELIVERED("Delivered");

    private final String name;

    OrderStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static OrderStatus fromName(String name) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getName().toUpperCase().equals(name.toUpperCase())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown name: " + name);
    }
}
