package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.FareReceipt;

/**
 * Strategy interface for calculating fares and generating receipts.
 * Adheres to Open-Closed Principle (OCP) and Strategy Design Pattern.
 */
public interface FareStrategy {
    /**
     * Calculates the fare and builds an itemized FareReceipt.
     *
     * @param rideId     the identifier of the ride being billed
     * @param distanceKm the total distance traveled in kilometers
     * @return the generated FareReceipt
     */
    FareReceipt calculateFare(String rideId, double distanceKm);

    /**
     * @return the name and description of this fare strategy
     */
    String getStrategyName();
}
