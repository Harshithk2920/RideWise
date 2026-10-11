package com.airtribe.ridewise.service;

import com.airtribe.ridewise.exception.NoDriverAvailableException;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.FareReceipt;
import com.airtribe.ridewise.model.Ride;
import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.strategy.DefaultFareStrategy;
import com.airtribe.ridewise.strategy.FareStrategy;
import com.airtribe.ridewise.strategy.NearestDriverStrategy;
import com.airtribe.ridewise.strategy.RideMatchingStrategy;
import com.airtribe.ridewise.util.IdGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Core orchestrator service managing ride bookings and lifecycle transitions.
 * Demonstrates:
 * - Composition over inheritance (composes RideMatchingStrategy and FareStrategy)
 * - Strategy Pattern via interfaces (interchangeable strategies at runtime)
 * - Dependency Inversion Principle (DIP)
 * - Single Responsibility & High Cohesion
 */
public class RideService {
    private final RiderService riderService;
    private final DriverService driverService;
    private final Map<String, Ride> rideRepository = new ConcurrentHashMap<>();

    // Composed strategies (pluggable at runtime)
    private RideMatchingStrategy matchingStrategy;
    private FareStrategy fareStrategy;

    public RideService(RiderService riderService, DriverService driverService) {
        this(riderService, driverService, new NearestDriverStrategy(), new DefaultFareStrategy());
    }

    public RideService(RiderService riderService, DriverService driverService,
                       RideMatchingStrategy matchingStrategy, FareStrategy fareStrategy) {
        this.riderService = riderService;
        this.driverService = driverService;
        this.matchingStrategy = matchingStrategy;
        this.fareStrategy = fareStrategy;
    }

    /**
     * Replaces the ride matching strategy at runtime.
     */
    public void setMatchingStrategy(RideMatchingStrategy matchingStrategy) {
        if (matchingStrategy == null) {
            throw new IllegalArgumentException("Matching strategy cannot be null");
        }
        this.matchingStrategy = matchingStrategy;
    }

    public RideMatchingStrategy getMatchingStrategy() {
        return matchingStrategy;
    }

    /**
     * Replaces the fare calculation strategy at runtime.
     */
    public void setFareStrategy(FareStrategy fareStrategy) {
        if (fareStrategy == null) {
            throw new IllegalArgumentException("Fare strategy cannot be null");
        }
        this.fareStrategy = fareStrategy;
    }

    public FareStrategy getFareStrategy() {
        return fareStrategy;
    }

    /**
     * Books a ride for the specified rider with the given pickup and drop coordinates.
     *
     * @throws IllegalArgumentException if rider does not exist
     * @throws NoDriverAvailableException if no suitable driver is found by the matching strategy
     */
    public Ride bookRide(String riderId, double pickupX, double pickupY, double dropX, double dropY)
            throws NoDriverAvailableException {
        Rider rider = riderService.getRiderById(riderId)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found with ID: " + riderId));

        List<Driver> availableDrivers = driverService.getAvailableDrivers();
        if (availableDrivers.isEmpty()) {
            throw new NoDriverAvailableException("No drivers are currently available in the system.");
        }

        // Delegate matching to composed strategy
        Driver matchedDriver = matchingStrategy.matchDriver(rider, availableDrivers, pickupX, pickupY)
                .orElseThrow(() -> new NoDriverAvailableException(
                        "No driver matched using strategy: " + matchingStrategy.getStrategyName()));

        // Mark driver unavailable during ride
        matchedDriver.setAvailable(false);

        // Calculate travel distance
        double distanceKm = calculateDistance(pickupX, pickupY, dropX, dropY);
        String rideId = IdGenerator.generateRideId();

        Ride ride = new Ride(rideId, rider, matchedDriver, pickupX, pickupY, dropX, dropY, distanceKm);
        rideRepository.put(rideId, ride);
        return ride;
    }

    /**
     * Starts an accepted ride.
     */
    public Ride startRide(String rideId) {
        Ride ride = getRideOrThrow(rideId);
        ride.start();
        return ride;
    }

    /**
     * Completes an in-progress ride, calculates the fare via the composed FareStrategy,
     * and produces the itemized FareReceipt.
     */
    public FareReceipt completeRide(String rideId) {
        Ride ride = getRideOrThrow(rideId);

        // Calculate fare using composed strategy
        FareReceipt receipt = fareStrategy.calculateFare(ride.getRideId(), ride.getDistanceKm());

        // Complete ride life-cycle and attach receipt
        ride.complete(receipt);
        return receipt;
    }

    /**
     * Cancels a ride if it has not yet completed.
     */
    public void cancelRide(String rideId) {
        Ride ride = getRideOrThrow(rideId);
        ride.cancel();
    }

    public Optional<Ride> getRide(String rideId) {
        return Optional.ofNullable(rideRepository.get(rideId));
    }

    public List<Ride> getAllRides() {
        return Collections.unmodifiableList(new ArrayList<>(rideRepository.values()));
    }

    public List<Ride> getRidesForRider(String riderId) {
        return rideRepository.values().stream()
                .filter(ride -> ride.getRider().getId().equals(riderId))
                .collect(Collectors.toList());
    }

    public List<Ride> getRidesForDriver(String driverId) {
        return rideRepository.values().stream()
                .filter(ride -> ride.getDriver().getId().equals(driverId))
                .collect(Collectors.toList());
    }

    private Ride getRideOrThrow(String rideId) {
        Ride ride = rideRepository.get(rideId);
        if (ride == null) {
            throw new IllegalArgumentException("Ride not found with ID: " + rideId);
        }
        return ride;
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.round(Math.sqrt(dx * dx + dy * dy) * 100.0) / 100.0;
    }
}
