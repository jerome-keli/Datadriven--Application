package com.example.pharmastock.seed;

import com.example.pharmastock.model.*;
import com.example.pharmastock.repository.*;
import com.example.pharmastock.security.PasswordUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Populates realistic sample data so the app is demo-ready and satisfies the
 * "minimum 100 records per collection" requirement (Task 2).
 * Toggle with app.seed.enabled in application.properties.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final PharmacyRepository pharmacyRepository;
    private final DrugRepository drugRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final StockLogRepository stockLogRepository;
    private final PaymentRepository paymentRepository;
    private final boolean seedEnabled;

    private final Random random = new Random(42);

    private static final String[] CITIES = {
            "Accra", "Kumasi", "Takoradi", "Tamale", "Cape Coast",
            "Sunyani", "Koforidua", "Ho", "Bolgatanga", "Wa"
    };

    private static final String[] PHARMACY_PREFIXES = {
            "Hope", "Grace", "Unity", "Zion", "Life", "Trust", "Bethel", "Faith", "Shalom",
            "Mercy", "Legacy", "Crown", "Golden", "Rock", "Palm", "Sunrise", "Heritage",
            "Victory", "Peace", "Harmony"
    };
    private static final String[] PHARMACY_SUFFIXES = {"Pharmacy", "Chemist", "Drugstore", "Health Store"};

    private record DrugTemplate(String name, String generic, String category, String form,
                                 String strength, boolean rx) {}

    private static final List<DrugTemplate> DRUG_TEMPLATES = List.of(
            new DrugTemplate("Paracetamol", "Paracetamol", "Analgesic", "TABLET", "500mg", false),
            new DrugTemplate("Amoxicillin", "Amoxicillin", "Antibiotic", "CAPSULE", "500mg", true),
            new DrugTemplate("Artemether-Lumefantrine", "Artemether/Lumefantrine", "Antimalarial", "TABLET", "20/120mg", true),
            new DrugTemplate("Metformin", "Metformin HCl", "Antidiabetic", "TABLET", "500mg", true),
            new DrugTemplate("Amlodipine", "Amlodipine Besylate", "Antihypertensive", "TABLET", "5mg", true),
            new DrugTemplate("Ibuprofen", "Ibuprofen", "NSAID", "TABLET", "400mg", false),
            new DrugTemplate("Cetirizine", "Cetirizine HCl", "Antihistamine", "TABLET", "10mg", false),
            new DrugTemplate("Omeprazole", "Omeprazole", "Antacid", "CAPSULE", "20mg", false),
            new DrugTemplate("Diclofenac", "Diclofenac Sodium", "NSAID", "TABLET", "50mg", true),
            new DrugTemplate("Ciprofloxacin", "Ciprofloxacin", "Antibiotic", "TABLET", "500mg", true),
            new DrugTemplate("ORS", "Oral Rehydration Salts", "Rehydration", "SACHET", "20.5g", false),
            new DrugTemplate("Vitamin C", "Ascorbic Acid", "Supplement", "TABLET", "1000mg", false),
            new DrugTemplate("Folic Acid", "Folic Acid", "Supplement", "TABLET", "5mg", false),
            new DrugTemplate("Multivitamin Syrup", "Multivitamin", "Supplement", "SYRUP", "100ml", false),
            new DrugTemplate("Amoxiclav", "Amoxicillin/Clavulanate", "Antibiotic", "TABLET", "625mg", true),
            new DrugTemplate("Loratadine", "Loratadine", "Antihistamine", "TABLET", "10mg", false),
            new DrugTemplate("Metronidazole", "Metronidazole", "Antibiotic", "TABLET", "400mg", true),
            new DrugTemplate("Salbutamol Inhaler", "Salbutamol", "Bronchodilator", "INHALER", "100mcg", true),
            new DrugTemplate("Insulin Glargine", "Insulin Glargine", "Antidiabetic", "INJECTION", "100IU/ml", true),
            new DrugTemplate("Paracetamol Syrup", "Paracetamol", "Analgesic", "SYRUP", "120mg/5ml", false),
            new DrugTemplate("Hydrocortisone Cream", "Hydrocortisone", "Corticosteroid", "CREAM", "1%", false),
            new DrugTemplate("Losartan", "Losartan Potassium", "Antihypertensive", "TABLET", "50mg", true),
            new DrugTemplate("Atorvastatin", "Atorvastatin", "Statin", "TABLET", "20mg", true),
            new DrugTemplate("Doxycycline", "Doxycycline", "Antibiotic", "CAPSULE", "100mg", true),
            new DrugTemplate("Chloramphenicol Eye Drops", "Chloramphenicol", "Antibiotic", "DROPS", "0.5%", false)
    );

    private static final String[] MANUFACTURERS = {
            "Ernest Chemists", "Kinapharma", "Danadams Pharmaceuticals", "Tobinco Pharmaceuticals",
            "M&G Pharmaceuticals", "LaGray Chemical Company", "Phyto-Riker", "GSK Ghana", "Cipla"
    };

    private static final String[] FIRST_NAMES = {
            "Kwame", "Ama", "Kofi", "Akosua", "Yaw", "Efua", "Kwabena", "Abena", "Kwesi", "Adjoa",
            "Kojo", "Afia", "Yaa", "Kwaku", "Esi", "Fiifi", "Nana", "Akua", "Kwadwo", "Araba"
    };
    private static final String[] LAST_NAMES = {
            "Mensah", "Owusu", "Boateng", "Asante", "Agyeman", "Appiah", "Osei", "Amoah",
            "Darko", "Frimpong", "Acheampong", "Adjei", "Tetteh", "Ansah", "Nkrumah"
    };

    public DataSeeder(PharmacyRepository pharmacyRepository, DrugRepository drugRepository,
                       StockRepository stockRepository, UserRepository userRepository,
                       ReservationRepository reservationRepository, StockLogRepository stockLogRepository,
                       PaymentRepository paymentRepository,
                       @Value("${app.seed.enabled:false}") boolean seedEnabled) {
        this.pharmacyRepository = pharmacyRepository;
        this.drugRepository = drugRepository;
        this.stockRepository = stockRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.stockLogRepository = stockLogRepository;
        this.paymentRepository = paymentRepository;
        this.seedEnabled = seedEnabled;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled) return;

        List<Reservation> reservations = List.of();
        if (pharmacyRepository.count() == 0) {
            List<Pharmacy> pharmacies = seedPharmacies(110);
            List<Drug> drugs = seedDrugs();
            List<User> pharmacistUsers = seedPharmacistUsers(pharmacies);
            List<User> customerUsers = seedCustomerUsers(90);
            List<Stock> stockEntries = seedStock(pharmacies, drugs, pharmacistUsers);
            reservations = seedReservations(pharmacies, customerUsers, stockEntries, 150);

            System.out.println("[DataSeeder] Seeded " + pharmacies.size() + " pharmacies, " + drugs.size()
                    + " drugs, " + stockEntries.size() + " stock entries, "
                    + (pharmacistUsers.size() + customerUsers.size()) + " users, "
                    + reservations.size() + " reservations (MongoDB).");
        } else {
            System.out.println("[DataSeeder] MongoDB data already present - skipping Mongo seed.");
            reservations = reservationRepository.findAll();
        }

        // SQL side is seeded independently since it's a separate database that
        // may be reset/available on its own schedule.
        if (paymentRepository.count() == 0) {
            int created = seedPayments(reservations);
            System.out.println("[DataSeeder] Seeded " + created + " payments (MySQL).");
        } else {
            System.out.println("[DataSeeder] MySQL payments already present - skipping payment seed.");
        }
    }

    private List<Pharmacy> seedPharmacies(int count) {
        List<Pharmacy> pharmacies = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String name = PHARMACY_PREFIXES[random.nextInt(PHARMACY_PREFIXES.length)] + " "
                    + PHARMACY_SUFFIXES[random.nextInt(PHARMACY_SUFFIXES.length)];
            String city = CITIES[random.nextInt(CITIES.length)];
            Pharmacy.Location location = new Pharmacy.Location(
                    "P.O. Box " + (100 + i) + ", " + city,
                    city,
                    5.0 + random.nextDouble() * 5,   // rough Ghana latitude band
                    -3.0 + random.nextDouble() * 4    // rough Ghana longitude band
            );
            Pharmacy pharmacy = new Pharmacy(name + " " + (i + 1), location,
                    "+2332" + (10000000 + random.nextInt(89999999)),
                    "PHC-" + (1000 + i), random.nextDouble() > 0.15);
            pharmacies.add(pharmacy);
        }
        return pharmacyRepository.saveAll(pharmacies);
    }

    private List<Drug> seedDrugs() {
        List<Drug> drugs = new ArrayList<>();
        // Repeat templates with different manufacturers to comfortably exceed 100 catalog entries.
        for (int i = 0; i < 5; i++) {
            for (DrugTemplate t : DRUG_TEMPLATES) {
                String manufacturer = MANUFACTURERS[random.nextInt(MANUFACTURERS.length)];
                drugs.add(new Drug(t.name(), t.generic(), t.category(), t.form(), t.strength(),
                        manufacturer, t.rx(),
                        t.name() + " (" + t.strength() + ") manufactured by " + manufacturer + "."));
            }
        }
        return drugRepository.saveAll(drugs);
    }

    private List<User> seedPharmacistUsers(List<Pharmacy> pharmacies) {
        List<User> users = new ArrayList<>();
        for (Pharmacy pharmacy : pharmacies) {
            String first = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String last = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String email = (first + "." + last + pharmacy.getId().substring(0, 4) + "@example.test").toLowerCase();
            User user = new User(first + " " + last, email,
                    "+2332" + (10000000 + random.nextInt(89999999)),
                    PasswordUtil.hash("password123"), User.Role.PHARMACIST, pharmacy.getId());
            users.add(user);
        }
        return userRepository.saveAll(users);
    }

    private List<User> seedCustomerUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String first = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String last = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String email = (first + "." + last + i + "@example.test").toLowerCase();
            User user = new User(first + " " + last, email,
                    "+2332" + (10000000 + random.nextInt(89999999)),
                    PasswordUtil.hash("password123"), User.Role.CUSTOMER, null);
            users.add(user);
        }
        return userRepository.saveAll(users);
    }

    private List<Stock> seedStock(List<Pharmacy> pharmacies, List<Drug> drugs, List<User> pharmacistUsers) {
        List<Stock> stockEntries = new ArrayList<>();
        Map<String, String> pharmacyToPharmacist = new HashMap<>();
        for (User u : pharmacistUsers) pharmacyToPharmacist.put(u.getPharmacyId(), u.getId());

        for (Pharmacy pharmacy : pharmacies) {
            int drugCount = 15 + random.nextInt(16); // each pharmacy stocks 15-30 distinct drugs
            Set<Integer> chosenIndices = new HashSet<>();
            while (chosenIndices.size() < drugCount) {
                chosenIndices.add(random.nextInt(drugs.size()));
            }
            for (int idx : chosenIndices) {
                Drug drug = drugs.get(idx);
                int qty = random.nextInt(200);
                double price = 5 + random.nextDouble() * 95; // GHS 5 - 100
                LocalDate expiry = LocalDate.now().plusMonths(3 + random.nextInt(24));
                Stock stock = new Stock(pharmacy.getId(), drug.getId(), qty,
                        Math.round(price * 100.0) / 100.0,
                        "BATCH-" + (10000 + random.nextInt(89999)), expiry,
                        pharmacyToPharmacist.get(pharmacy.getId()));
                stockEntries.add(stock);
            }
        }
        return stockRepository.saveAll(stockEntries);
    }

    private List<Reservation> seedReservations(List<Pharmacy> pharmacies, List<User> customers,
                                                List<Stock> stockEntries, int count) {
        Map<String, List<Stock>> stockByPharmacy = new HashMap<>();
        for (Stock s : stockEntries) {
            stockByPharmacy.computeIfAbsent(s.getPharmacyId(), k -> new ArrayList<>()).add(s);
        }
        Map<String, Drug> drugCache = new HashMap<>();
        // Weighted so most reservations reach a paid state - gives the SQL
        // payments table a realistic, well-populated sample too.
        Reservation.Status[] weightedStatuses = {
                Reservation.Status.PENDING, Reservation.Status.CANCELLED,
                Reservation.Status.CONFIRMED, Reservation.Status.CONFIRMED,
                Reservation.Status.READY_FOR_PICKUP, Reservation.Status.READY_FOR_PICKUP,
                Reservation.Status.COMPLETED, Reservation.Status.COMPLETED, Reservation.Status.COMPLETED
        };

        List<Reservation> reservations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Pharmacy pharmacy = pharmacies.get(random.nextInt(pharmacies.size()));
            List<Stock> pharmacyStock = stockByPharmacy.get(pharmacy.getId());
            if (pharmacyStock == null || pharmacyStock.isEmpty()) continue;

            Stock chosen = pharmacyStock.get(random.nextInt(pharmacyStock.size()));
            Drug drug = drugCache.computeIfAbsent(chosen.getDrugId(),
                    id -> drugRepository.findById(id).orElse(null));
            if (drug == null) continue;

            int qty = 1 + random.nextInt(3);
            Reservation.Item item = new Reservation.Item(chosen.getDrugId(), drug.getName(), qty, chosen.getPrice());
            User customer = customers.get(random.nextInt(customers.size()));

            Reservation reservation = new Reservation(customer.getId(), pharmacy.getId(),
                    List.of(item), Math.round(qty * chosen.getPrice() * 100.0) / 100.0);
            reservation.setStatus(weightedStatuses[random.nextInt(weightedStatuses.length)]);
            reservation.setCreatedAt(Instant.now().minus(random.nextInt(60), ChronoUnit.DAYS));
            reservation.setUpdatedAt(reservation.getCreatedAt());
            reservations.add(reservation);
        }
        return reservationRepository.saveAll(reservations);
    }

    /** Every reservation that reached CONFIRMED/READY/COMPLETED gets a matching SQL payment row. */
    private int seedPayments(List<Reservation> reservations) {
        Payment.Method[] methods = Payment.Method.values();
        List<Payment> payments = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getStatus() == Reservation.Status.PENDING || r.getStatus() == Reservation.Status.CANCELLED) {
                continue;
            }
            Payment payment = new Payment(r.getId(), r.getCustomerId(), r.getPharmacyId(),
                    r.getTotalAmount(), methods[random.nextInt(methods.length)], Payment.Status.COMPLETED);
            payment.setCreatedAt(r.getCreatedAt());
            payment.setUpdatedAt(r.getUpdatedAt());
            payments.add(payment);
        }
        paymentRepository.saveAll(payments);
        return payments.size();
    }
}
