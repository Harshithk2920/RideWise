package com.airtribe.ridewise;

import com.airtribe.ridewise.exception.NoDriverAvailableException;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.FareReceipt;
import com.airtribe.ridewise.model.Ride;
import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.service.DriverService;
import com.airtribe.ridewise.service.RideService;
import com.airtribe.ridewise.service.RiderService;
import com.airtribe.ridewise.strategy.DefaultFareStrategy;
import com.airtribe.ridewise.strategy.LeastActiveDriverStrategy;
import com.airtribe.ridewise.strategy.NearestDriverStrategy;
import com.airtribe.ridewise.strategy.PeakHourFareStrategy;

import java.util.List;
import java.util.Scanner;

/**
 * RideWise Console Application Entry Point.
 * Demonstrates:
 * - Strategy Pattern (Matching and Fare calculation)
 * - Composition over Inheritance
 * - SOLID Principles
 * - Low Coupling & High Cohesion
 * - Law of Demeter
 */
public class Main {
    private final RiderService riderService;
    private final DriverService driverService;
    private final RideService rideService;
    private final Scanner scanner;

    public Main() {
        this.riderService = new RiderService();
        this.driverService = new DriverService();
        this.rideService = new RideService(riderService, driverService);
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Main app = new Main();

        if (args.length > 0 && args[0].equalsIgnoreCase("--demo")) {
            app.runAutomatedDemo();
            return;
        }

        System.out.println("======================================================================");
        System.out.println("             WELCOME TO RIDEWISE (LLD DEMONSTRATION SYSTEM)           ");
        System.out.println("======================================================================");
        System.out.println("1. Run Automated Comprehensive Demonstration (Showcases all requirements)");
        System.out.println("2. Interactive Console Mode");
        System.out.println("3. Exit");
        System.out.print("Select an option [1-3]: ");

        if (app.scanner.hasNextLine()) {
            String choice = app.scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    app.runAutomatedDemo();
                    break;
                case "2":
                    app.seedInitialData();
                    app.runInteractiveMenu();
                    break;
                case "3":
                    System.out.println("Exiting RideWise. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid selection. Executing Automated Comprehensive Demonstration by default.\n");
                    app.runAutomatedDemo();
                    break;
            }
        } else {
            app.runAutomatedDemo();
        }
    }

    /**
     * Preloads realistic sample data for demo and testing.
     */
    public void seedInitialData() {
        // Register Riders
        riderService.registerRider("Alice Johnson", "+1-555-0101", 10.0, 10.0);
        riderService.registerRider("Bob Smith", "+1-555-0102", 50.0, 50.0);
        riderService.registerRider("Charlie Brown", "+1-555-0103", 25.0, 30.0);

        // Register Drivers at different locations
        driverService.registerDriver("David Miller", "+1-555-0201", "KA-01-AB-1234", 12.0, 11.0); // Close to Alice
        driverService.registerDriver("Eva Green", "+1-555-0202", "KA-02-CD-5678", 22.0, 20.0);   // Moderate distance
        driverService.registerDriver("Frank Castle", "+1-555-0203", "KA-03-EF-9012", 48.0, 49.0); // Close to Bob
    }

    /**
     * Runs an automated end-to-end demonstration proving all functional and design requirements.
     */
    public void runAutomatedDemo() {
        seedInitialData();
        System.out.println("\n======================================================================");
        System.out.println("        STARTING RIDEWISE AUTOMATED LLD ASSESSMENT DEMONSTRATION       ");
        System.out.println("======================================================================");

        List<Rider> riders = riderService.getAllRiders();
        List<Driver> drivers = driverService.getAllDrivers();

        System.out.println("\n[STEP 1] Registered Riders:");
        for (Rider r : riders) {
            System.out.printf("  - %s | Name: %s | Phone: %s | Coordinates: (%.1f, %.1f)%n",
                    r.getId(), r.getName(), r.getPhoneNumber(), r.getCurrentX(), r.getCurrentY());
        }

        System.out.println("\n[STEP 2] Registered Drivers:");
        for (Driver d : drivers) {
            System.out.printf("  - %s | Name: %s | Cab: %s | Coordinates: (%.1f, %.1f) | Available: %b | Trips Done: %d%n",
                    d.getId(), d.getName(), d.getVehicleNumber(), d.getCurrentX(), d.getCurrentY(), d.isAvailable(), d.getCompletedRidesCount());
        }

        // Scenario 1: Nearest Driver Strategy & Default Fare
        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("[SCENARIO 1] Booking Ride with 'NearestDriverStrategy' & 'DefaultFareStrategy'");
        System.out.println("----------------------------------------------------------------------");
        rideService.setMatchingStrategy(new NearestDriverStrategy());
        rideService.setFareStrategy(new DefaultFareStrategy(5.00, 2.00));
        System.out.println("Current Matching Strategy : " + rideService.getMatchingStrategy().getStrategyName());
        System.out.println("Current Fare Strategy     : " + rideService.getFareStrategy().getStrategyName());

        Rider alice = riderService.getAllRiders().stream()
                .filter(r -> r.getName().equals("Alice Johnson")).findFirst().orElse(riders.get(0));
        System.out.printf("Rider '%s' requests ride from (10.0, 10.0) to (18.0, 22.0)%n", alice.getName());

        try {
            Ride ride1 = rideService.bookRide(alice.getId(), 10.0, 10.0, 18.0, 22.0);
            System.out.printf(" Ride Booked successfully! Ride ID: %s%n", ride1.getRideId());
            System.out.printf("  -> Matched Driver : %s (%s, Car: %s)%n",
                    ride1.getDriver().getName(), ride1.getDriver().getId(), ride1.getDriver().getVehicleNumber());
            System.out.printf("  -> Trip Distance  : %.2f km%n", ride1.getDistanceKm());
            System.out.printf("  -> Status         : %s%n", ride1.getStatus());

            // Trip lifecycle: start -> complete
            System.out.println("\n Trip Life-cycle Progression:");
            rideService.startRide(ride1.getRideId());
            System.out.printf("  -> Status after startRide()    : %s%n", ride1.getStatus());

            FareReceipt receipt1 = rideService.completeRide(ride1.getRideId());
            System.out.printf("  -> Status after completeRide() : %s%n", ride1.getStatus());
            System.out.println("\nGenerated Itemized Fare Receipt (Ride -> FareReceipt Composition):");
            System.out.println(receipt1.formatReceipt());

            System.out.printf("Driver '%s' updated state: Completed Trips = %d, Current Location = (%.1f, %.1f), Available = %b%n",
                    ride1.getDriver().getName(), ride1.getDriver().getCompletedRidesCount(),
                    ride1.getDriver().getCurrentX(), ride1.getDriver().getCurrentY(), ride1.getDriver().isAvailable());

        } catch (NoDriverAvailableException e) {
            System.err.println("Booking failed: " + e.getMessage());
        }

        // Scenario 2: Dynamic Strategy Switch - Least Active Driver & Peak Hour Surge Fare
        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("[SCENARIO 2] Dynamic Strategy Switch: 'LeastActiveDriverStrategy' & 'PeakHourFareStrategy'");
        System.out.println("----------------------------------------------------------------------");
        rideService.setMatchingStrategy(new LeastActiveDriverStrategy());
        rideService.setFareStrategy(new PeakHourFareStrategy(5.00, 2.00, 1.50)); // 1.5x surge

        System.out.println("Switched Matching Strategy : " + rideService.getMatchingStrategy().getStrategyName());
        System.out.println("Switched Fare Strategy     : " + rideService.getFareStrategy().getStrategyName());

        Rider charlie = riderService.getAllRiders().stream()
                .filter(r -> r.getName().equals("Charlie Brown")).findFirst().orElse(riders.get(1));
        System.out.printf("Rider '%s' requests ride from (17.0, 21.0) to (30.0, 35.0)%n", charlie.getName());
        System.out.println("Note: David Miller is physically closest at (18.0, 22.0), but has 1 completed ride.");
        System.out.println("LeastActiveDriverStrategy will favor drivers with 0 completed rides (e.g. Eva Green).");

        try {
            Ride ride2 = rideService.bookRide(charlie.getId(), 17.0, 21.0, 30.0, 35.0);
            System.out.printf(" Ride Booked successfully! Ride ID: %s%n", ride2.getRideId());
            System.out.printf("  -> Matched Driver : %s (%s, Car: %s) with previous completed rides: %d%n",
                    ride2.getDriver().getName(), ride2.getDriver().getId(),
                    ride2.getDriver().getVehicleNumber(), ride2.getDriver().getCompletedRidesCount());
            System.out.printf("  -> Trip Distance  : %.2f km%n", ride2.getDistanceKm());

            rideService.startRide(ride2.getRideId());
            FareReceipt receipt2 = rideService.completeRide(ride2.getRideId());
            System.out.println("\nGenerated Itemized Surge Fare Receipt:");
            System.out.println(receipt2.formatReceipt());

        } catch (NoDriverAvailableException e) {
            System.err.println("Booking failed: " + e.getMessage());
        }

        // Scenario 3: Exception Handling - NoDriverAvailableException
        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("[SCENARIO 3] Exception Handling: NoDriverAvailableException when no cabs are free");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Simulating surge with all drivers marked offline/busy...");
        driverService.getAllDrivers().forEach(d -> d.setAvailable(false));

        try {
            rideService.bookRide(alice.getId(), 5.0, 5.0, 15.0, 15.0);
            System.out.println("Booking unexpectedly succeeded!");
        } catch (NoDriverAvailableException e) {
            System.out.println(" Handled Expected Exception Successfully: " + e.getMessage());
        }

        // Restore drivers
        driverService.getAllDrivers().forEach(d -> d.setAvailable(true));

        // Scenario 4: Trip Cancellation Life-cycle
        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("[SCENARIO 4] Trip Life-cycle: Ride Cancellation");
        System.out.println("----------------------------------------------------------------------");
        try {
            Ride ride3 = rideService.bookRide(alice.getId(), 10.0, 10.0, 12.0, 14.0);
            System.out.printf("Ride booked: %s with driver %s. Driver availability: %b%n",
                    ride3.getRideId(), ride3.getDriver().getName(), ride3.getDriver().isAvailable());
            System.out.println("Rider initiates cancellation...");
            rideService.cancelRide(ride3.getRideId());
            System.out.printf("Ride status after cancellation: %s%n", ride3.getStatus());
            System.out.printf("Driver '%s' is released and back online: %b%n",
                    ride3.getDriver().getName(), ride3.getDriver().isAvailable());
        } catch (NoDriverAvailableException e) {
            System.err.println("Booking failed: " + e.getMessage());
        }

        System.out.println("\n======================================================================");
        System.out.println("             RIDEWISE AUTOMATED DEMONSTRATION COMPLETE                ");
        System.out.println("======================================================================\n");
    }

    /**
     * Interactive console menu allowing users to play with RideWise.
     */
    public void runInteractiveMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n----- RIDEWISE INTERACTIVE CONSOLE MENU -----");
            System.out.println("1. Book a Ride");
            System.out.println("2. Start a Ride");
            System.out.println("3. Complete a Ride & Generate Receipt");
            System.out.println("4. Cancel a Ride");
            System.out.println("5. Change Ride Matching Strategy");
            System.out.println("6. Change Fare Calculation Strategy");
            System.out.println("7. View All Drivers");
            System.out.println("8. View All Riders");
            System.out.println("9. View All Rides");
            System.out.println("10. Register New Driver");
            System.out.println("11. Register New Rider");
            System.out.println("12. Exit");
            System.out.print("Enter choice [1-12]: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleBookRide();
                    break;
                case "2":
                    handleStartRide();
                    break;
                case "3":
                    handleCompleteRide();
                    break;
                case "4":
                    handleCancelRide();
                    break;
                case "5":
                    handleChangeMatchingStrategy();
                    break;
                case "6":
                    handleChangeFareStrategy();
                    break;
                case "7":
                    driverService.getAllDrivers().forEach(System.out::println);
                    break;
                case "8":
                    riderService.getAllRiders().forEach(System.out::println);
                    break;
                case "9":
                    rideService.getAllRides().forEach(System.out::println);
                    break;
                case "10":
                    handleRegisterDriver();
                    break;
                case "11":
                    handleRegisterRider();
                    break;
                case "12":
                    running = false;
                    System.out.println("Exiting RideWise. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please select 1-12.");
            }
        }
    }

    private void handleBookRide() {
        try {
            System.out.print("Enter Rider ID: ");
            String riderId = scanner.nextLine().trim();
            System.out.print("Pickup X: ");
            double px = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Pickup Y: ");
            double py = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Drop X: ");
            double dx = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Drop Y: ");
            double dy = Double.parseDouble(scanner.nextLine().trim());

            Ride ride = rideService.bookRide(riderId, px, py, dx, dy);
            System.out.printf(" Ride booked successfully! Ride ID: %s | Driver: %s (%s) | Distance: %.2f km%n",
                    ride.getRideId(), ride.getDriver().getName(), ride.getDriver().getVehicleNumber(), ride.getDistanceKm());
        } catch (NoDriverAvailableException e) {
            System.out.println(" [Error] " + e.getMessage());
        } catch (Exception e) {
            System.out.println(" [Error] Failed to book ride: " + e.getMessage());
        }
    }

    private void handleStartRide() {
        try {
            System.out.print("Enter Ride ID to start: ");
            String rideId = scanner.nextLine().trim();
            Ride ride = rideService.startRide(rideId);
            System.out.printf(" Ride %s started! Status: %s%n", ride.getRideId(), ride.getStatus());
        } catch (Exception e) {
            System.out.println(" [Error] " + e.getMessage());
        }
    }

    private void handleCompleteRide() {
        try {
            System.out.print("Enter Ride ID to complete: ");
            String rideId = scanner.nextLine().trim();
            FareReceipt receipt = rideService.completeRide(rideId);
            System.out.println("\n" + receipt.formatReceipt());
        } catch (Exception e) {
            System.out.println(" [Error] " + e.getMessage());
        }
    }

    private void handleCancelRide() {
        try {
            System.out.print("Enter Ride ID to cancel: ");
            String rideId = scanner.nextLine().trim();
            rideService.cancelRide(rideId);
            System.out.printf(" Ride %s cancelled successfully.%n", rideId);
        } catch (Exception e) {
            System.out.println(" [Error] " + e.getMessage());
        }
    }

    private void handleChangeMatchingStrategy() {
        System.out.println("Select Ride Matching Strategy:");
        System.out.println("1. Nearest Driver Strategy");
        System.out.println("2. Least Active Driver Strategy");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();
        if ("1".equals(choice)) {
            rideService.setMatchingStrategy(new NearestDriverStrategy());
            System.out.println(" Matching strategy set to Nearest Driver.");
        } else if ("2".equals(choice)) {
            rideService.setMatchingStrategy(new LeastActiveDriverStrategy());
            System.out.println(" Matching strategy set to Least Active Driver.");
        } else {
            System.out.println("Invalid selection.");
        }
    }

    private void handleChangeFareStrategy() {
        System.out.println("Select Fare Strategy:");
        System.out.println("1. Default Fare Strategy ($5 base + $2/km)");
        System.out.println("2. Peak Hour Surge Strategy (1.5x surge)");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();
        if ("1".equals(choice)) {
            rideService.setFareStrategy(new DefaultFareStrategy());
            System.out.println(" Fare strategy set to Default Fare.");
        } else if ("2".equals(choice)) {
            rideService.setFareStrategy(new PeakHourFareStrategy());
            System.out.println(" Fare strategy set to Peak Hour Surge.");
        } else {
            System.out.println("Invalid selection.");
        }
    }

    private void handleRegisterDriver() {
        try {
            System.out.print("Enter Driver Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Phone Number: ");
            String phone = scanner.nextLine().trim();
            System.out.print("Enter Vehicle Registration: ");
            String vehicle = scanner.nextLine().trim();
            System.out.print("Initial X: ");
            double x = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Initial Y: ");
            double y = Double.parseDouble(scanner.nextLine().trim());

            Driver driver = driverService.registerDriver(name, phone, vehicle, x, y);
            System.out.printf(" Driver registered successfully: %s (ID: %s)%n", driver.getName(), driver.getId());
        } catch (Exception e) {
            System.out.println(" [Error] Failed to register driver: " + e.getMessage());
        }
    }

    private void handleRegisterRider() {
        try {
            System.out.print("Enter Rider Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Phone Number: ");
            String phone = scanner.nextLine().trim();
            System.out.print("Initial X: ");
            double x = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Initial Y: ");
            double y = Double.parseDouble(scanner.nextLine().trim());

            Rider rider = riderService.registerRider(name, phone, x, y);
            System.out.printf(" Rider registered successfully: %s (ID: %s)%n", rider.getName(), rider.getId());
        } catch (Exception e) {
            System.out.println(" [Error] Failed to register rider: " + e.getMessage());
        }
    }
}
