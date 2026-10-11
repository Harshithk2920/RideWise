package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Rider;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Ride matching strategy that pairs the rider with the geographically closest driver.
 */
public class NearestDriverStrategy implements RideMatchingStrategy {

    @Override
    public Optional<Driver> matchDriver(Rider rider, List<Driver> availableDrivers, double pickupX, double pickupY) {
        if (availableDrivers == null || availableDrivers.isEmpty()) {
            return Optional.empty();
        }

        return availableDrivers.stream()
                .min(Comparator.comparingDouble(driver -> driver.distanceTo(pickupX, pickupY)));
    }

    @Override
    public String getStrategyName() {
        return "Nearest Driver Allocation";
    }
}
