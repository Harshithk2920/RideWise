package com.airtribe.ridewise.service;

import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.util.IdGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service class responsible for managing Rider lifecycle and queries.
 * Follows Single Responsibility Principle (SRP).
 */
public class RiderService {
    private final Map<String, Rider> riderRepository = new ConcurrentHashMap<>();

    /**
     * Registers a new rider in the system.
     */
    public Rider registerRider(String name, String phoneNumber, double initialX, double initialY) {
        String id = IdGenerator.generateRiderId();
        Rider rider = new Rider(id, name, phoneNumber, initialX, initialY);
        riderRepository.put(id, rider);
        return rider;
    }

    /**
     * Retrieves a rider by their unique identifier.
     */
    public Optional<Rider> getRiderById(String riderId) {
        return Optional.ofNullable(riderRepository.get(riderId));
    }

    /**
     * Updates rider location coordinates.
     */
    public boolean updateLocation(String riderId, double newX, double newY) {
        Rider rider = riderRepository.get(riderId);
        if (rider != null) {
            rider.updateLocation(newX, newY);
            return true;
        }
        return false;
    }

    /**
     * Returns an unmodifiable list of all registered riders.
     */
    public List<Rider> getAllRiders() {
        return Collections.unmodifiableList(new ArrayList<>(riderRepository.values()));
    }
}
