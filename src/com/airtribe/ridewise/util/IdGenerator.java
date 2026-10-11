package com.airtribe.ridewise.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for generating unique sequential identifiers.
 */
public class IdGenerator {
    private static final AtomicInteger RIDER_COUNTER = new AtomicInteger(100);
    private static final AtomicInteger DRIVER_COUNTER = new AtomicInteger(200);
    private static final AtomicInteger RIDE_COUNTER = new AtomicInteger(1000);
    private static final AtomicInteger RECEIPT_COUNTER = new AtomicInteger(5000);

    private IdGenerator() {
        // Private constructor to prevent instantiation
    }

    public static String generateRiderId() {
        return "RIDER-" + RIDER_COUNTER.incrementAndGet();
    }

    public static String generateDriverId() {
        return "DRIVER-" + DRIVER_COUNTER.incrementAndGet();
    }

    public static String generateRideId() {
        return "RIDE-" + RIDE_COUNTER.incrementAndGet();
    }

    public static String generateReceiptId() {
        return "RCPT-" + RECEIPT_COUNTER.incrementAndGet();
    }
}
