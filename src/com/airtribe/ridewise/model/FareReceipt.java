package com.airtribe.ridewise.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents the itemized financial receipt for a completed ride.
 * Exists in a strict Composition relationship with Ride.
 */
public class FareReceipt {
    private final String receiptId;
    private final String rideId;
    private final double distanceKm;
    private final double baseFare;
    private final double distanceFare;
    private final double surgeFare;
    private final double totalFare;
    private final String fareStrategyName;
    private final LocalDateTime issuedAt;

    public FareReceipt(String receiptId, String rideId, double distanceKm, double baseFare,
                       double distanceFare, double surgeFare, double totalFare, String fareStrategyName) {
        this.receiptId = receiptId;
        this.rideId = rideId;
        this.distanceKm = distanceKm;
        this.baseFare = baseFare;
        this.distanceFare = distanceFare;
        this.surgeFare = surgeFare;
        this.totalFare = totalFare;
        this.fareStrategyName = fareStrategyName;
        this.issuedAt = LocalDateTime.now();
    }

    public String getReceiptId() {
        return receiptId;
    }

    public String getRideId() {
        return rideId;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public double getDistanceFare() {
        return distanceFare;
    }

    public double getSurgeFare() {
        return surgeFare;
    }

    public double getTotalFare() {
        return totalFare;
    }

    public String getFareStrategyName() {
        return fareStrategyName;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public String formatReceipt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("                 RIDEWISE FARE RECEIPT              \n");
        sb.append("====================================================\n");
        sb.append(String.format(" Receipt ID     : %s\n", receiptId));
        sb.append(String.format(" Ride ID        : %s\n", rideId));
        sb.append(String.format(" Date & Time    : %s\n", issuedAt.format(formatter)));
        sb.append(String.format(" Pricing Scheme : %s\n", fareStrategyName));
        sb.append(String.format(" Total Distance : %.2f km\n", distanceKm));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format(" Base Fare      : $%.2f\n", baseFare));
        sb.append(String.format(" Distance Fare  : $%.2f\n", distanceFare));
        if (surgeFare > 0) {
            sb.append(String.format(" Surge Pricing  : $%.2f\n", surgeFare));
        }
        sb.append("----------------------------------------------------\n");
        sb.append(String.format(" TOTAL AMOUNT   : $%.2f\n", totalFare));
        sb.append("====================================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("FareReceipt[id='%s', rideId='%s', total=$%.2f, strategy='%s']",
                receiptId, rideId, totalFare, fareStrategyName);
    }
}
