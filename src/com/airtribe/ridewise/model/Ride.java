package com.airtribe.ridewise.model;

import java.time.LocalDateTime;

/**
 * Represents a Ride entity encapsulating rider, driver, route, status,
 * and composed fare receipt.
 *
 * Relationships:
 * - Rider -> Ride (Association)
 * - Driver -> Ride (Association)
 * - Ride -> FareReceipt (Composition: FareReceipt belongs strictly to this Ride)
 */
public class Ride {
    private final String rideId;
    private final Rider rider;
    private final Driver driver;
    private final double pickupX;
    private final double pickupY;
    private final double dropX;
    private final double dropY;
    private final double distanceKm;
    private RideStatus status;
    private FareReceipt fareReceipt; // Composed receipt
    private final LocalDateTime requestedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public Ride(String rideId, Rider rider, Driver driver,
                double pickupX, double pickupY, double dropX, double dropY, double distanceKm) {
        this.rideId = rideId;
        this.rider = rider;
        this.driver = driver;
        this.pickupX = pickupX;
        this.pickupY = pickupY;
        this.dropX = dropX;
        this.dropY = dropY;
        this.distanceKm = distanceKm;
        this.status = RideStatus.ACCEPTED;
        this.requestedAt = LocalDateTime.now();
    }

    public String getRideId() {
        return rideId;
    }

    public Rider getRider() {
        return rider;
    }

    public Driver getDriver() {
        return driver;
    }

    public double getPickupX() {
        return pickupX;
    }

    public double getPickupY() {
        return pickupY;
    }

    public double getDropX() {
        return dropX;
    }

    public double getDropY() {
        return dropY;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public RideStatus getStatus() {
        return status;
    }

    public FareReceipt getFareReceipt() {
        return fareReceipt;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    /**
     * Starts the ride if it is in ACCEPTED status.
     */
    public void start() {
        if (this.status != RideStatus.ACCEPTED) {
            throw new IllegalStateException("Ride cannot be started from state: " + this.status);
        }
        this.status = RideStatus.STARTED;
        this.startedAt = LocalDateTime.now();
    }

    /**
     * Completes the ride and attaches the composed FareReceipt.
     */
    public void complete(FareReceipt receipt) {
        if (this.status != RideStatus.STARTED) {
            throw new IllegalStateException("Ride cannot be completed from state: " + this.status);
        }
        this.fareReceipt = receipt;
        this.status = RideStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        
        // Update driver statistics and coordinates
        this.driver.incrementCompletedRides();
        this.driver.updateLocation(dropX, dropY);
        this.driver.setAvailable(true);

        // Update rider coordinates
        this.rider.updateLocation(dropX, dropY);
    }

    /**
     * Cancels the ride if it hasn't completed.
     */
    public void cancel() {
        if (this.status == RideStatus.COMPLETED) {
            throw new IllegalStateException("Completed ride cannot be cancelled.");
        }
        this.status = RideStatus.CANCELLED;
        this.driver.setAvailable(true);
    }

    // Law of Demeter convenience helpers
    public String getRiderName() {
        return rider != null ? rider.getName() : "Unknown";
    }

    public String getDriverName() {
        return driver != null ? driver.getName() : "Unassigned";
    }

    public String getDriverVehicle() {
        return driver != null ? driver.getVehicleNumber() : "N/A";
    }

    @Override
    public String toString() {
        return String.format("Ride[id='%s', rider='%s', driver='%s', status=%s, distance=%.2f km]",
                rideId, getRiderName(), getDriverName(), status, distanceKm);
    }
}
