package com.airtribe.ridewise.model;

/**
 * Represents a driver providing transportation services in RideWise.
 */
public class Driver {
    private final String id;
    private final String name;
    private final String phoneNumber;
    private final String vehicleNumber;
    private double currentX;
    private double currentY;
    private boolean available;
    private int completedRidesCount;

    public Driver(String id, String name, String phoneNumber, String vehicleNumber, double currentX, double currentY) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.vehicleNumber = vehicleNumber;
        this.currentX = currentX;
        this.currentY = currentY;
        this.available = true;
        this.completedRidesCount = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public double getCurrentX() {
        return currentX;
    }

    public double getCurrentY() {
        return currentY;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getCompletedRidesCount() {
        return completedRidesCount;
    }

    public void incrementCompletedRides() {
        this.completedRidesCount++;
    }

    public void updateLocation(double newX, double newY) {
        this.currentX = newX;
        this.currentY = newY;
    }

    /**
     * Calculates Euclidean distance to a given coordinate.
     * Encapsulates coordinate geometry within Driver respecting Law of Demeter.
     */
    public double distanceTo(double targetX, double targetY) {
        double dx = this.currentX - targetX;
        double dy = this.currentY - targetY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return String.format("Driver[id='%s', name='%s', vehicle='%s', location=(%.2f, %.2f), available=%b, completedRides=%d]",
                id, name, vehicleNumber, currentX, currentY, available, completedRidesCount);
    }
}
