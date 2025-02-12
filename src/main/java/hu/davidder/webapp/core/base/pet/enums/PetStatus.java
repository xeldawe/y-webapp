package hu.davidder.webapp.core.base.pet.enums;

public enum PetStatus {

    AVAILABLE("Available"),
    PENDING("Pending"),
    SOLD("Sold");

    private final String name;

    PetStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PetStatus fromName(String name) {
        for (PetStatus status : PetStatus.values()) {
            if (status.getName().toUpperCase().equals(name.toUpperCase())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown name: " + name);
    }
}
