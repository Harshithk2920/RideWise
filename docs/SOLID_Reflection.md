# RideWise — SOLID Principles Reflection

This document presents a comprehensive analysis of how the five **SOLID** principles of Object-Oriented Software Design are implemented throughout the **RideWise** architecture.

---

## 1. S — Single Responsibility Principle (SRP)

> *"A class should have one, and only one, reason to change."*

In **RideWise**, every class is assigned a single, well-defined responsibility:

| Class | Single Responsibility | Reason to Change |
| :--- | :--- | :--- |
| `Rider` | Encapsulates rider personal data and geographical position. | Changes in rider profile attributes. |
| `Driver` | Encapsulates driver data, vehicle info, spatial metrics, and availability state. | Changes in driver profile attributes or driver state rules. |
| `Ride` | Encapsulates state transitions and coordinates of a single journey. | Changes in ride lifecycle transition logic. |
| `FareReceipt` | Represents an immutable, itemized financial billing record. | Changes in formatting or required receipt line items. |
| `RideService` | Orchestrates ride bookings, transitions, and coordination between domains. | Changes in high-level trip workflow steps. |
| `DriverService` | Manages driver persistence and availability filtering. | Changes in driver management queries or storage. |
| `RiderService` | Manages rider persistence and registration. | Changes in rider management queries or storage. |
| `IdGenerator` | Thread-safe generation of domain identifiers. | Changes in ID format or sequence strategy. |

### Code Example:
Rather than letting `RideService` calculate fares or compute geometry, `RideService` delegates fare calculation to `FareStrategy`, distance calculation to mathematical utilities, and driver distance to `Driver.distanceTo(...)`.

---

## 2. O — Open/Closed Principle (OCP)

> *"Software entities should be open for extension, but closed for modification."*

RideWise achieves OCP by defining contracts via **Interfaces** and leveraging the **Strategy Pattern**:

1. **Ride Matching Openness**:
   - The contract `RideMatchingStrategy` specifies `matchDriver(...)`.
   - Existing implementations:
     - `NearestDriverStrategy`: Minimizes distance.
     - `LeastActiveDriverStrategy`: Balances trip load among drivers.
   - If tomorrow a new strategy is needed (e.g., `HighestRatedDriverStrategy` or `ElectricVehiclePriorityStrategy`), we simply create a new class implementing `RideMatchingStrategy`. **Zero lines of code in `RideService` need to be modified.**

2. **Fare Pricing Openness**:
   - The contract `FareStrategy` specifies `calculateFare(...)`.
   - Existing implementations:
     - `DefaultFareStrategy`: Standard base + distance rates.
     - `PeakHourFareStrategy`: Dynamic surge multiplier during peak demand.
   - If a new discount or subscription scheme is required (e.g., `CorporatePassFareStrategy`), we introduce it without modifying existing pricing classes or `RideService`.

---

## 3. L — Liskov Substitution Principle (LSP)

> *"Subtypes must be substitutable for their base types without altering the correctness of the program."*

In RideWise:
- Any implementation of `RideMatchingStrategy` can replace any other implementation without unexpected side-effects or contract violations. Both `NearestDriverStrategy` and `LeastActiveDriverStrategy` accept `(rider, availableDrivers, pickupX, pickupY)` and return `Optional<Driver>`. Neither throws unexpected unchecked exceptions or introduces preconditions that violate the interface.
- Similarly, `DefaultFareStrategy` and `PeakHourFareStrategy` both satisfy `calculateFare(rideId, distanceKm)` and return a fully populated, valid `FareReceipt` with non-negative total fares.

```java
// Liskov Substitution demonstrated in RideService:
rideService.setMatchingStrategy(new NearestDriverStrategy());
Ride rideA = rideService.bookRide(...); // Works flawlessly

rideService.setMatchingStrategy(new LeastActiveDriverStrategy());
Ride rideB = rideService.bookRide(...); // Works identically from caller's perspective!
```

---

## 4. I — Interface Segregation Principle (ISP)

> *"Clients should not be forced to depend on methods they do not use."*

RideWise uses lean, cohesive, client-specific interfaces rather than fat "god" interfaces:

- `RideMatchingStrategy` contains only methods relevant to driver selection:
  ```java
  public interface RideMatchingStrategy {
      Optional<Driver> matchDriver(Rider rider, List<Driver> availableDrivers, double pickupX, double pickupY);
      String getStrategyName();
  }
  ```
- `FareStrategy` contains only methods relevant to fare billing:
  ```java
  public interface FareStrategy {
      FareReceipt calculateFare(String rideId, double distanceKm);
      String getStrategyName();
  }
  ```

Because these concerns are cleanly segregated:
- A developer creating a new driver matching strategy is never forced to implement fare calculation methods.
- A pricing algorithm developer is never burdened with spatial driver matching routines.

---

## 5. D — Dependency Inversion Principle (DIP)

> *"High-level modules should not depend on low-level modules. Both should depend on abstractions."*
> *"Abstractions should not depend on details. Details should depend on abstractions."*

In `RideService`:
- `RideService` (high-level orchestration module) does **not** instantiate or hardcode concrete strategy classes (`NearestDriverStrategy`, `PeakHourFareStrategy`).
- Instead, `RideService` references the abstract interfaces:
  ```java
  public class RideService {
      private RideMatchingStrategy matchingStrategy;
      private FareStrategy fareStrategy;

      public RideService(RiderService riderService, DriverService driverService,
                         RideMatchingStrategy matchingStrategy, FareStrategy fareStrategy) {
          this.riderService = riderService;
          this.driverService = driverService;
          this.matchingStrategy = matchingStrategy;
          this.fareStrategy = fareStrategy;
      }
      ...
  }
  ```
- Concrete classes depend on the abstractions (`NearestDriverStrategy implements RideMatchingStrategy`).
- Dependencies are injected via constructors and setters, ensuring testability and modular runtime adaptability.
