package de.htwsaar.application;

import java.util.List;
import java.util.Optional;

import de.htwsaar.domain.Employee;
import de.htwsaar.repository.EmployeeRepository;


public class EmployeeService {
    private final EmployeeRepository repo;

    public EmployeeService(EmployeeRepository repo) {
        this.repo = repo;
    }

    public Employee createEmployee(String firstName, String lastName,
                                   String role, String status,
                                   String hireDate, int workingHours) {
        try {
            if (invalid(firstName) || invalid(lastName) ||
                    invalid(role) || invalid(status) || invalid(hireDate)) {
                throw new IllegalArgumentException("All fields must be filled out.");
            }

            if (workingHours <= 0) {
                throw new IllegalArgumentException("Working hours must be greater than 0.");
            }

            if (workingHours > 60) {
                throw new IllegalArgumentException("Working hours must not exceed 60 per week.");
            }

            // Email automatisch erzeugen
            String email = generateUniqueEmail(firstName, lastName);

            Employee employee = this.repo.save(firstName, lastName, email, role, status, hireDate, workingHours);
            System.out.println("Employee successfully created: " + employee);
            return employee;

        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message.contains("All fields")) {
                System.out.println("Error: All fields must be filled out.");
            } else if (message.contains("greater than 0")) {
                System.out.println("Error: Working hours must be greater than 0.");
            } else if (message.contains("not exceed 60")) {
                System.out.println("Error: Working hours must not exceed 60 per week.");
            } else {
                System.out.println("Error: " + message);
            }
            throw e;
        }
    }

    private boolean invalid(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Erzeugt eine standardisierte Firmen-Email:
     * firstName.lastName@medtech.de
     */
    private String generateUniqueEmail(String firstName, String lastName) {
        String base = normalize(firstName) + "." + normalize(lastName);
        String domain = "@medtech.de";

        String email = base + domain;
        int counter = 2;

        while (repo.existsByEmail(email)) {
            email = base + counter + domain;
            counter++;
        }

        return email;
    }
    private String generateUniqueEmailForUpdate(String firstName, String lastName, int employeeId) {
        String base = normalize(firstName) + "." + normalize(lastName);
        String domain = "@medtech.de";

        String email = base + domain;
        int counter = 2;

        while (repo.existsByEmailForOtherEmployee(email, employeeId)) {
            email = base + counter + domain;
            counter++;
        }

        return email;
    }
    public String generateEmailForUpdate(String firstName, String lastName, int employeeId) {
        return generateUniqueEmailForUpdate(firstName, lastName, employeeId);
    }

    /**
     * Normalisiert Namen für Email-Adressen:
     * - trim
     * - lowercase
     * - entfernt Leerzeichen
     * - ersetzt deutsche Umlaute/ß
     */
    private String normalize(String value) {
        return value.trim()
                .toLowerCase()
                .replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace("ß", "ss")
                .replaceAll("\\s+", "");
    }

    public List<Employee> findAllEmployees() {
        return repo.findAll();
    }

    public List<Employee> findEmployeesByLastName(String lastName) {
        return repo.findByLastName(lastName);
    }

    public Optional<Employee> findEmployeeByID(int id) {
        return repo.findByID(id);
    }

    public void updateEmployee(Employee employee) {
        repo.update(employee);
    }
}