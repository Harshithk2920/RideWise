package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.FareReceipt;
import com.airtribe.ridewise.util.IdGenerator;

/**
 * Peak hour / surge pricing strategy that applies dynamic multipliers
 * during high demand periods.
 */
public class PeakHourFareStrategy implements FareStrategy {
    private static final double DEFAULT_BASE_FARE = 5.00;
    private static final double DEFAULT_RATE_PER_KM = 2.00;
    private static final double DEFAULT_SURGE_MULTIPLIER = 1.50; // 50% surge

    private final double baseFare;
    private final double ratePerKm;
    private final double surgeMultiplier;

    public PeakHourFareStrategy() {
        this(DEFAULT_BASE_FARE, DEFAULT_RATE_PER_KM, DEFAULT_SURGE_MULTIPLIER);
    }

    public PeakHourFareStrategy(double surgeMultiplier) {
        this(DEFAULT_BASE_FARE, DEFAULT_RATE_PER_KM, surgeMultiplier);
    }

    public PeakHourFareStrategy(double baseFare, double ratePerKm, double surgeMultiplier) {
        if (surgeMultiplier < 1.0) {
            throw new IllegalArgumentException("Surge multiplier cannot be less than 1.0");
        }
        this.baseFare = baseFare;
        this.ratePerKm = ratePerKm;
        this.surgeMultiplier = surgeMultiplier;
    }

    @Override
    public FareReceipt calculateFare(String rideId, double distanceKm) {
        double distanceFare = Math.round(distanceKm * ratePerKm * 100.0) / 100.0;
        double regularTotal = baseFare + distanceFare;
        double totalWithSurge = Math.round(regularTotal * surgeMultiplier * 100.0) / 100.0;
        double surgeFare = Math.round((totalWithSurge - regularTotal) * 100.0) / 100.0;

        String receiptId = IdGenerator.generateReceiptId();
        return new FareReceipt(
                receiptId,
                rideId,
                distanceKm,
                baseFare,
                distanceFare,
                surgeFare,
                totalWithSurge,
                getStrategyName()
        );
    }

    @Override
    public String getStrategyName() {
        return String.format("Peak Hour Surge Pricing (Base: $%.2f, Rate: $%.2f/km, Multiplier: %.1fx)",
                baseFare, ratePerKm, surgeMultiplier);
    }
}
