package com.airtribe.ridewise.service;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.util.IdGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing Driver records, availability, and location updates.
 * Follows Single Responsibility Principle (SRP).
 */
public class DriverService {
    private final Map<String, Driver> driverRepository = new ConcurrentHashMap<>();

    /**
     * Registers a new driver in the system.
     */
    public Driver registerDriver(String name, String phoneNumber, String vehicleNumber, double initialX, double initialY) {
        String id = IdGenerator.generateDriverId();
        Driver driver = new Driver(id, name, phoneNumber, vehicleNumber, initialX, initialY);
        driverRepository.put(id, driver);
        return driver;
    }

    /**
     * Finds a driver by their ID.
     */
    public Optional<Driver> getDriverById(String driverId) {
        return Optional.ofNullable(driverRepository.get(driverId));
    }

    /**
     * Sets availability status for a driver.
     */
    public boolean setAvailability(String driverId, boolean available) {
        Driver driver = driverRepository.get(driverId);
        if (driver != null) {
            driver.setAvailable(available);
            return true;
        }
        return false;
    }

    /**
     * Updates driver's current coordinates.
     */
    public boolean updateLocation(String driverId, double newX, double newY) {
        Driver driver = driverRepository.get(driverId);
        if (driver != null) {
            driver.updateLocation(newX, newY);
            return true;
        }
        return false;
    }

    /**
     * Retrieves all drivers who are currently online and available for dispatch.
     */
    public List<Driver> getAvailableDrivers() {
        return driverRepository.values().stream()
                .filter(Driver::isAvailable)
                .collect(Collectors.toList());
    }

    /**
     * Returns an unmodifiable list of all registered drivers.
     */
    public List<Driver> getAllDrivers() {
        return Collections.unmodifiableList(new ArrayList<>(driverRepository.values()));
    }
}
