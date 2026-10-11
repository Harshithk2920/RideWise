# RideWise — System Requirements Document

## 1. Executive Summary
**RideWise** is a modular, console-based Ride-Sharing System (inspired by Uber/Ola architectures) developed as part of the Airtribe Low-Level Design (LLD) Project Assessment. The system is designed not simply as an application, but as a demonstration of production-grade Object-Oriented Design principles, including:
- **Composition over Inheritance**
- **Strategy Design Pattern** via well-defined interfaces
- **SOLID Principles** applied across all architectural layers
- **Low Coupling and High Cohesion**
- **Law of Demeter (Principle of Least Knowledge)**

---

## 2. Problem Statement
Urban ride-hailing platforms require dynamic dispatching mechanisms, transparent and extensible fare calculations, and robust lifecycle tracking of trips. A monolithic or tightly coupled design creates significant challenges when introducing new matching algorithms (e.g., matching nearest drivers vs. promoting driver fairness) or dynamic pricing models (e.g., flat rate vs. peak-hour surge).

RideWise solves this by decoupling algorithmic variations into pluggable strategies while isolating data models, business rules, and lifecycle state management.

---

## 3. Functional Requirements (FR)

### FR-1: Rider Management
- **FR-1.1**: The system must allow riders to be registered with a unique ID, full name, phone number, and initial 2D location coordinates `(x, y)`.
- **FR-1.2**: Riders can query their profiles and track trip histories.
- **FR-1.3**: Upon ride completion, the rider's physical location is automatically updated to the ride's destination coordinates.

### FR-2: Driver Management & Dispatch Availability
- **FR-2.1**: The system must register drivers with unique IDs, names, contact numbers, vehicle registration identifiers, and current `(x, y)` coordinates.
- **FR-2.2**: The system must track driver real-time availability (`available = true/false`).
- **FR-2.3**: The system must maintain metrics on driver activity, specifically `completedRidesCount`, to support driver workload distribution.
- **FR-2.4**: Upon ride booking, the assigned driver is marked as unavailable. When the ride completes or is cancelled, the driver is restored to available status.
- **FR-2.5**: Upon ride completion, the driver's location is updated to the destination drop-off coordinates.

### FR-3: Ride Request & Matching with Pluggable Strategies
- **FR-3.1**: A registered rider can request a ride by providing pickup `(px, py)` and destination `(dx, dy)` coordinates.
- **FR-3.2**: Ride distance is computed using Euclidean distance geometry between pickup and drop points.
- **FR-3.3**: The matching logic is delegated to an interchangeable `RideMatchingStrategy` interface:
  - **Strategy A: Nearest Driver Strategy** matches the geographically closest available driver.
  - **Strategy B: Least Active Driver Strategy** matches the available driver who has completed the fewest trips, breaking ties using Euclidean proximity.
- **FR-3.4**: If no drivers are online or available, the system must cleanly throw and handle `NoDriverAvailableException`.
- **FR-3.5**: Ride matching strategies can be swapped dynamically at runtime without restarting the application.

### FR-4: Trip Life-Cycle State Machine
- **FR-4.1**: Each ride follows an explicit, guarded lifecycle:
  $$\text{REQUESTED} \longrightarrow \text{ACCEPTED} \longrightarrow \text{STARTED} \longrightarrow \text{COMPLETED}$$
  $$\text{or } \text{CANCELLED}$$
- **FR-4.2**: Invalid state transitions (e.g., completing an unstarted ride, starting a completed ride, cancelling a completed ride) are prevented with exceptions.
- **FR-4.3**: Cancelling an accepted ride releases the driver back into the available driver pool.

### FR-5: Fare Calculation & Itemized Receipts
- **FR-5.1**: Upon trip completion, pricing calculation is executed by a pluggable `FareStrategy` interface:
  - **Strategy A: Default Fare Strategy**: Fixed base fare plus linear per-kilometer rate.
  - **Strategy B: Peak Hour Surge Fare Strategy**: Applies configurable surge pricing multipliers (e.g., 1.5x) to model high-demand periods.
- **FR-5.2**: The fare computation produces an immutable, itemized `FareReceipt` detailing:
  - Receipt ID & Ride ID
  - Issue timestamp
  - Total distance traveled
  - Pricing scheme description
  - Base fare, distance fare, surge component, and grand total.
- **FR-5.3**: Fare strategies can be toggled dynamically at runtime.

---

## 4. Non-Functional Requirements (NFR)

1. **Extensibility (Open/Closed Principle)**:
   - New driver matching rules (e.g., Highest Rated Driver, EV-Only, Batched Matching) can be plugged in without modifying `RideService`.
   - New fare schemes (e.g., Subscription discount, Night-time surcharge, Shared ride split) can be added seamlessly.
2. **Modularity & High Cohesion**:
   - Each package and class encapsulates a cohesive responsibility: `model` for state/domain logic, `strategy` for algorithms, `service` for business orchestration, `exception` for domain failures, and `util` for cross-cutting helpers.
3. **Encapsulation & Low Coupling**:
   - Direct field manipulations across domain boundaries are forbidden.
   - Objects interact via contracts (interfaces) and well-defined methods.
4. **Law of Demeter**:
   - Domain objects refrain from navigating object dependency chains (e.g., no chained getter expressions across foreign boundaries).
5. **Thread Safety & Data Integrity**:
   - Core repositories use concurrent collections (`ConcurrentHashMap`) and atomic identifiers (`AtomicInteger`) for thread-safe state mutations.
6. **Self-Contained Executable**:
   - Includes both an automated end-to-end verification demo and an interactive command-line menu.

---

## 5. Scope & Boundary Conditions
- **In Scope**:
  - In-memory data store with thread-safe data structures.
  - Pluggable allocation and pricing strategies.
  - Coordinate-based 2D Euclidean spatial modeling.
  - Comprehensive CLI demonstration and interactive test interface.
- **Out of Scope (By Assessment Specification)**:
  - Persistent SQL/NoSQL databases (deliberately omitted in favor of pure LLD focus).
  - External HTTP network servers or frontend UI frameworks.
