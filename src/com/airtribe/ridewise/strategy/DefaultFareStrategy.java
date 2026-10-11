package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.FareReceipt;
import com.airtribe.ridewise.util.IdGenerator;

/**
 * Default standard pricing strategy using a fixed base fare and per-kilometer rate.
 */
public class DefaultFareStrategy implements FareStrategy {
    private static final double DEFAULT_BASE_FARE = 5.00;
    private static final double DEFAULT_RATE_PER_KM = 2.00;

    private final double baseFare;
    private final double ratePerKm;

    public DefaultFareStrategy() {
        this(DEFAULT_BASE_FARE, DEFAULT_RATE_PER_KM);
    }

    public DefaultFareStrategy(double baseFare, double ratePerKm) {
        this.baseFare = baseFare;
        this.ratePerKm = ratePerKm;
    }

    @Override
    public FareReceipt calculateFare(String rideId, double distanceKm) {
        double distanceFare = Math.round(distanceKm * ratePerKm * 100.0) / 100.0;
        double surgeFare = 0.0;
        double totalFare = Math.round((baseFare + distanceFare) * 100.0) / 100.0;

        String receiptId = IdGenerator.generateReceiptId();
        return new FareReceipt(
                receiptId,
                rideId,
                distanceKm,
                baseFare,
                distanceFare,
                surgeFare,
                totalFare,
                getStrategyName()
        );
    }

    @Override
    public String getStrategyName() {
        return String.format("Standard Default Fare (Base: $%.2f, Rate: $%.2f/km)", baseFare, ratePerKm);
    }
}
