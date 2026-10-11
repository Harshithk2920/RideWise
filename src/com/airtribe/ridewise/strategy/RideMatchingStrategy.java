package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Rider;

import java.util.List;
import java.util.Optional;

/**
 * Strategy interface for matching a rider with an available driver.
 * Adheres to Open-Closed Principle (OCP) and Strategy Design Pattern.
 */
public interface RideMatchingStrategy {
    /**
     * Finds the best driver for the given rider and pickup location.
     *
     * @param rider            the rider requesting the ride
     * @param availableDrivers candidate drivers who are currently online and free
     * @param pickupX          pickup location X coordinate
     * @param pickupY          pickup location Y coordinate
     * @return an Optional containing the matched Driver, or empty if none suitable
     */
    Optional<Driver> matchDriver(Rider rider, List<Driver> availableDrivers, double pickupX, double pickupY);

    /**
     * @return human-readable description of the strategy
     */
    String getStrategyName();
}
