# RideWise — Console-Based Ride-Sharing System (LLD Assessment)

RideWise is a clean, modular, console-based ride-sharing application (Uber/Ola style) engineered to demonstrate high-quality Low-Level Design (LLD), design patterns, and architectural principles.

---

## Key Highlights

- **Composition over Inheritance**: Flexible behavioral composition without class explosions.
- **Strategy Pattern using Interfaces**: Pluggable ride matching and dynamic fare calculation algorithms.
- **SOLID Principles**: Strictly applied across all packages and classes.
- **Low Coupling & High Cohesion**: Clean boundaries between domain models, services, strategies, exceptions, and utilities.
- **Law of Demeter**: Objects interact only with immediate collaborators, preserving strong encapsulation.

---

## Project Structure

```
d:/RideWise/
├── docs/
│   ├── Requirements.md
│   ├── Class_Model.md
│   ├── SOLID_Reflection.md
│   └── Object_Relationships.md
├── src/
│   └── com/
│       └── airtribe/
│           └── ridewise/
│               ├── Main.java
│               ├── model/
│               │   ├── Rider.java
│               │   ├── Driver.java
│               │   ├── Ride.java
│               │   ├── FareReceipt.java
│               │   └── RideStatus.java
│               ├── strategy/
│               │   ├── RideMatchingStrategy.java
│               │   ├── NearestDriverStrategy.java
│               │   ├── LeastActiveDriverStrategy.java
│               │   ├── FareStrategy.java
│               │   ├── DefaultFareStrategy.java
│               │   └── PeakHourFareStrategy.java
│               ├── service/
│               │   ├── RiderService.java
│               │   ├── DriverService.java
│               │   └── RideService.java
│               ├── exception/
│               │   └── NoDriverAvailableException.java
│               └── util/
│                   └── IdGenerator.java
└── README.md
```

---

## How to Build and Run

### 1. Compile the Project
From the project root (`d:\RideWise`):

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

### 2. Run the Automated LLD Demonstration
To immediately verify all scenarios (Nearest driver, Least active driver, Surge pricing, Fare receipts, Lifecycle transitions, and Exception handling):

```powershell
java -cp out com.airtribe.ridewise.Main --demo
```

### 3. Run the Interactive Menu
To test with interactive input:

```powershell
java -cp out com.airtribe.ridewise.Main
```

---

## Architectural Documentation
Detailed architectural documents are located in the `docs/` folder:
- [Requirements.md](file:///d:/RideWise/docs/Requirements.md): Functional & Non-Functional requirements and state transitions.
- [Class_Model.md](file:///d:/RideWise/docs/Class_Model.md): Mermaid class diagrams and component responsibilities.
- [SOLID_Reflection.md](file:///d:/RideWise/docs/SOLID_Reflection.md): Deep-dive into how S-O-L-I-D is reflected in the codebase.
- [Object_Relationships.md](file:///d:/RideWise/docs/Object_Relationships.md): Associations, Compositions, Law of Demeter, and Coupling/Cohesion dynamics.
