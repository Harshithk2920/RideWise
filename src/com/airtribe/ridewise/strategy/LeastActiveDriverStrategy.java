package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Rider;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Ride matching strategy that promotes fairness by assigning rides to the
 * least active driver (fewest completed trips), breaking ties with distance.
 */
public class LeastActiveDriverStrategy implements RideMatchingStrategy {

    @Override
    public Optional<Driver> matchDriver(Rider rider, List<Driver> availableDrivers, double pickupX, double pickupY) {
        if (availableDrivers == null || availableDrivers.isEmpty()) {
            return Optional.empty();
        }

        return availableDrivers.stream()
                .min(Comparator
                        .comparingInt(Driver::getCompletedRidesCount)
                        .thenComparingDouble(driver -> driver.distanceTo(pickupX, pickupY)));
    }

    @Override
    public String getStrategyName() {
        return "Least Active Driver Allocation (Fair Distribution)";
    }
}
