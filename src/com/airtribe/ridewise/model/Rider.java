package com.airtribe.ridewise.model;

/**
 * Represents a rider requesting transportation in the RideWise system.
 */
public class Rider {
    private final String id;
    private final String name;
    private final String phoneNumber;
    private double currentX;
    private double currentY;

    public Rider(String id, String name, String phoneNumber, double currentX, double currentY) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.currentX = currentX;
        this.currentY = currentY;
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

    public double getCurrentX() {
        return currentX;
    }

    public double getCurrentY() {
        return currentY;
    }

    public void updateLocation(double newX, double newY) {
        this.currentX = newX;
        this.currentY = newY;
    }

    @Override
    public String toString() {
        return String.format("Rider[id='%s', name='%s', phone='%s', location=(%.2f, %.2f)]",
                id, name, phoneNumber, currentX, currentY);
    }
}
