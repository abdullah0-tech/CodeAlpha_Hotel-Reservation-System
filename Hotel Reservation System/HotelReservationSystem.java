 import java.io.*;
import java.util.*;

class Room {
    private int roomNumber;
    private String category; // Standard, Deluxe, Suite
    private double price;
    private boolean available;

    public Room(int roomNumber, String category, double price) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.price = price;
        this.available = true;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " [" + category + "] - $" + price + " - " + (available ? "Available" : "Booked");
    }
}

class Reservation {
    private String guestName;
    private Room room;
    private String paymentStatus;

    public Reservation(String guestName, Room room, String paymentStatus) {
        this.guestName = guestName;
        this.room = room;
        this.paymentStatus = paymentStatus;
    }

    public String getGuestName() { return guestName; }
    public Room getRoom() { return room; }
    public String getPaymentStatus() { return paymentStatus; }

    @Override
    public String toString() {
        return "Reservation: " + guestName + " -> Room " + room.getRoomNumber() + " (" + room.getCategory() + ") - $" + room.getPrice() + " | Payment: " + paymentStatus;
    }
}

class HotelSystem {
    private List<Room> rooms;
    private List<Reservation> reservations;

    public HotelSystem() {
        rooms = new ArrayList<>();
        reservations = new ArrayList<>();
        loadData();
    }

    // Add some rooms
    public void initRooms() {
        rooms.add(new Room(101, "Standard", 100));
        rooms.add(new Room(102, "Standard", 100));
        rooms.add(new Room(201, "Deluxe", 200));
        rooms.add(new Room(202, "Deluxe", 200));
        rooms.add(new Room(301, "Suite", 400));
    }

    // Search available rooms by category
    public void searchRooms(String category) {
        System.out.println("Available rooms in category: " + category);
        for (Room r : rooms) {
            if (r.getCategory().equalsIgnoreCase(category) && r.isAvailable()) {
                System.out.println(r);
            }
        }
    }

    // Make reservation
    public void bookRoom(String guestName, int roomNumber) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber && r.isAvailable()) {
                r.setAvailable(false);
                Reservation res = new Reservation(guestName, r, simulatePayment(r.getPrice()));
                reservations.add(res);
                System.out.println("Booking successful: " + res);
                saveData();
                return;
            }
        }
        System.out.println("Room not available.");
    }

    // Cancel reservation
    public void cancelReservation(String guestName, int roomNumber) {
        Reservation toRemove = null;
        for (Reservation res : reservations) {
            if (res.getGuestName().equalsIgnoreCase(guestName) && res.getRoom().getRoomNumber() == roomNumber) {
                res.getRoom().setAvailable(true);
                toRemove = res;
                break;
            }
        }
        if (toRemove != null) {
            reservations.remove(toRemove);
            System.out.println("Reservation cancelled for " + guestName);
            saveData();
        } else {
            System.out.println("No reservation found.");
        }
    }

    // View all reservations
    public void viewReservations() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }
        for (Reservation res : reservations) {
            System.out.println(res);
        }
    }

    // Simulate payment
    private String simulatePayment(double amount) {
        return (Math.random() > 0.1) ? "Paid" : "Failed"; // 90% chance success
    }

    // Save to file
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("hotelData.dat"))) {
            oos.writeObject(rooms.size());
            for (Room r : rooms) {
                oos.writeObject(r.getRoomNumber());
                oos.writeObject(r.getCategory());
                oos.writeObject(r.getPrice());
                oos.writeObject(r.isAvailable());
            }
            oos.writeObject(reservations.size());
            for (Reservation res : reservations) {
                oos.writeObject(res.getGuestName());
                oos.writeObject(res.getRoom().getRoomNumber());
                oos.writeObject(res.getPaymentStatus());
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // Load from file
    private void loadData() {
        File file = new File("hotelData.dat");
        if (!file.exists()) {
            initRooms();
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            int roomCount = (Integer) ois.readObject();
            rooms.clear();
            for (int i = 0; i < roomCount; i++) {
                int number = (Integer) ois.readObject();
                String category = (String) ois.readObject();
                double price = (Double) ois.readObject();
                boolean available = (Boolean) ois.readObject();
                Room r = new Room(number, category, price);
                r.setAvailable(available);
                rooms.add(r);
            }
            int resCount = (Integer) ois.readObject();
            reservations.clear();
            for (int i = 0; i < resCount; i++) {
                String guest = (String) ois.readObject();
                int roomNo = (Integer) ois.readObject();
                String status = (String) ois.readObject();
                Room room = findRoomByNumber(roomNo);
                if (room != null) {
                    reservations.add(new Reservation(guest, room, status));
                    room.setAvailable(false);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
            initRooms();
        }
    }

    private Room findRoomByNumber(int number) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == number) return r;
        }
        return null;
    }
}

public class HotelReservationSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HotelSystem system = new HotelSystem();

        while (true) {
            System.out.println("\n=== Hotel Reservation System ===");
            System.out.println("1. Search Rooms");
            System.out.println("2. Book Room");
            System.out.println("3. Cancel Reservation");
            System.out.println("4. View Reservations");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter category (Standard/Deluxe/Suite): ");
                    String category = sc.nextLine();
                    system.searchRooms(category);
                    break;
                case 2:
                    System.out.print("Enter guest name: ");
                    String guest = sc.nextLine();
                    System.out.print("Enter room number: ");
                    int roomNo = sc.nextInt();
                    system.bookRoom(guest, roomNo);
                    break;
                case 3:
                    System.out.print("Enter guest name: ");
                    String gName = sc.nextLine();
                    System.out.print("Enter room number: ");
                    int rNo = sc.nextInt();
                    system.cancelReservation(gName, rNo);
                    break;
                case 4:
                    system.viewReservations();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
