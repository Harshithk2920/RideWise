# RideWise — Object Relationships & Architectural Design

This document details the object-oriented relationships, coupling/cohesion dynamics, and adherence to the **Law of Demeter** across the **RideWise** system.

---

## 1. Relationship Mapping

As specified in the system design requirements:

| Relationship | Relationship Type | Lifecycle / Ownership Semantics |
| :--- | :--- | :--- |
| **Rider $\rightarrow$ Ride** | **Association** | Loose association. Riders exist independently before a ride is created and persist after the ride completes. The Ride merely holds a reference to the participating Rider. |
| **Driver $\rightarrow$ Ride** | **Association** | Loose association. Drivers exist independently in the system pool. A Ride references the assigned Driver, but Driver life cycle is separate from any specific Ride. |
| **Ride $\rightarrow$ FareReceipt** | **Composition** | Strong composition. A `FareReceipt` is exclusively produced as an outcome of completing a `Ride`. It has no independent existence outside the context of its parent `Ride`. If a `Ride` ceases to exist, its associated `FareReceipt` has no distinct lifecycle. |
| **RideService $\rightarrow$ Strategies** | **Composition** | Behavioral composition. `RideService` holds references to interchangeable algorithmic strategies (`RideMatchingStrategy` and `FareStrategy`). It controls their invocations within the ride booking workflow. |

---

## 2. Composition Over Inheritance

One of the foundational tenets of clean Low-Level Design is **"Favor Object Composition over Class Inheritance"**.

### Why Not Inheritance?
If RideWise used inheritance to accommodate diverse matching and pricing schemes, we would face a combinatorial class explosion:
```
           RideService (Base)
           /                \
NearestDriverRideService   LeastActiveDriverRideService
      /           \                  /          \
NearestDefault  NearestSurge   LeastActiveDefault LeastActiveSurge
```
Adding $M$ matching algorithms and $N$ fare models with inheritance would require $M \times N$ subclasses, creating rigid code duplication and making runtime switching impossible.

### How RideWise Uses Composition:
In RideWise, `RideService` composes the behavior through interfaces:
```java
public class RideService {
    private RideMatchingStrategy matchingStrategy; // Composed interface
    private FareStrategy fareStrategy;             // Composed interface

    public void setMatchingStrategy(RideMatchingStrategy strategy) { ... }
    public void setFareStrategy(FareStrategy strategy) { ... }
}
```
This enables:
1. **Runtime Dynamism**: The system can switch from `NearestDriverStrategy` to `LeastActiveDriverStrategy` on the fly based on peak vs non-peak policies.
2. **Linear Scalability**: Adding a new strategy requires creating only 1 new class ($M + N$ instead of $M \times N$).
3. **Isolated Testability**: Each strategy can be unit-tested in complete isolation with mock data.

---

## 3. Law of Demeter (LoD — Principle of Least Knowledge)

> *"Each unit should have only limited knowledge about other units: only units closely related to the current unit. Talk only to your immediate friends; don't talk to strangers."*

A common anti-pattern in object-oriented code is "train-wreck" chaining:
`service.getRide().getDriver().getLocation().calculateDistance(x, y)`

RideWise strictly adheres to the **Law of Demeter**:

### 1. Spatial Encapsulation in `Driver`
Instead of having external strategies or services extract internal driver coordinates to perform geometry:
```java
// VIOLATION OF LoD:
double dx = driver.getCurrentX() - pickupX;
double dy = driver.getCurrentY() - pickupY;
double dist = Math.sqrt(dx * dx + dy * dy);

// RIDEWISE DEMETER-COMPLIANT DESIGN:
double dist = driver.distanceTo(pickupX, pickupY);
```
The `Driver` class encapsulates its own coordinate calculations and exposes `distanceTo(x, y)`. External callers never manipulate the driver's internal position structure directly.

### 2. Delegated Accessors in `Ride`
When callers or logging routines need rider or vehicle information from a `Ride`, `Ride` provides direct, convenient delegators:
```java
ride.getRiderName();    // Talks directly to immediate friend 'rider'
ride.getDriverName();   // Talks directly to immediate friend 'driver'
ride.getDriverVehicle();// Talks directly to immediate friend 'driver'
```
This protects clients from having to query null-checked nested object graphs.

---

## 4. Coupling and Cohesion Dynamics

### High Cohesion
- **`RiderService` & `DriverService`**: Handle entity CRUD, state queries, and location updates within their respective bounded domains.
- **`RideService`**: Focuses strictly on orchestrating the ride booking workflow, delegating algorithmic policies to strategies and entity state mutations to the entities themselves.
- **`DefaultFareStrategy` & `PeakHourFareStrategy`**: Focus purely on computing financial numbers and structuring line items.

### Low Coupling
- **Services communicate through clean interfaces and IDs**: `RideService` queries `DriverService.getAvailableDrivers()` rather than accessing internal driver stores.
- **Strategies do not know about `RideService`**: Strategies receive only the parameters they need (`Rider`, `List<Driver>`, coordinates, or distance) and return outcomes (`Optional<Driver>` or `FareReceipt`).
- **Domain models are POJOs**: Entities (`Rider`, `Driver`, `Ride`, `FareReceipt`) have zero dependency on UI, console I/O, or framework-specific annotations.
