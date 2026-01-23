package de.htwsaar.application;

import java.util.List;
import java.util.Optional;

import de.htwsaar.domain.Employee;
import de.htwsaar.repository.EmployeeRepository;


public class EmployeeService {
    private final EmployeeRepository repo;

    /**
     * Creates a new EmployeeService instance.
     *
     * @param repo EmployeeRepository used for employee persistence
     */
    public EmployeeService(EmployeeRepository repo) {
        this.repo = repo;
    }

    /**
     * Creates and persists a new employee.
     *
     * @param firstName    Employee first name
     * @param lastName     Employee last name
     * @param role         Employee role
     * @param status       Employment status
     * @param hireDate     Hire date of the employee
     * @param workingHours Weekly working hours
     * @return the created Employee
     * @throws IllegalArgumentException if input data is invalid
     */
    public Employee createEmployee(String firstName, String lastName,
                                   String role, String status,
                                   String hireDate, int workingHours) {

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

        // Automatically generates the email address
        String email = generateUniqueEmail(firstName, lastName, null);

        // Repository may throw runtime exceptions that are handled by the UI
        return this.repo.save(firstName, lastName, email, role, status, hireDate, workingHours);
    }

    /**
     * Checks whether a string value is null or blank.
     *
     * @param value the value to check
     * @return true if the value is null or blank
     */
    private boolean invalid(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Generates a unique company email address for an employee.
     *
     * @param firstName             Employee first name
     * @param lastName              Employee last name
     * @param employeeIdToIgnore    Employee ID to exclude from uniqueness check (used for updates)
     * @return a unique email address
     */
    private String generateUniqueEmail(String firstName, String lastName, Integer employeeIdToIgnore) {
        String base = normalize(firstName) + "." + normalize(lastName);
        String domain = "@medtech.de";

        String email = base + domain;
        int counter = 2;

        while (emailExists(email, employeeIdToIgnore)) {
            email = base + counter + domain;
            counter++;
        }

        return email;
    }

    /**
     * Checks whether an email address already exists.
     *
     * @param email                Email address to check
     * @param employeeIdToIgnore   Employee ID to exclude from the check (update case)
     * @return true if the email already exists
     */
    private boolean emailExists(String email, Integer employeeIdToIgnore) {
        if (employeeIdToIgnore == null) {
            return repo.existsByEmail(email); // create-case
        }
        return repo.existsByEmailForOtherEmployee(email, employeeIdToIgnore); // update-case
    }

    /**
     * Generates a unique email address for an existing employee.
     *
     * @param firstName   Employee first name
     * @param lastName    Employee last name
     * @param employeeId  ID of the employee being updated
     * @return a unique email address
     */
    public String generateEmailForUpdate(String firstName, String lastName, int employeeId) {
        return generateUniqueEmail(firstName, lastName, employeeId);
    }

    /**
     * Normalizes a string for email usage.
     * <p>
     * - Trims whitespace<br>
     * - Converts to lowercase<br>
     * - Replaces German umlauts and ß<br>
     * - Removes remaining spaces
     *
     * @param value the string to normalize
     * @return the normalized string
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

    /**
     * Retrieves all employees from the repository.
     *
     * @return a list of all employees
     */
    public List<Employee> findAllEmployees() {
        return repo.findAll();
    }

    /**
     * Finds employees by their last name.
     *
     * @param lastName last name to search for
     * @return a list of employees with the given last name
     */
    public List<Employee> findEmployeesByLastName(String lastName) {
        return repo.findByLastName(lastName);
    }

    /**
     * Finds an employee by their ID.
     *
     * @param id the employee's ID
     * @return an Optional containing the employee if found, or empty if not
     */
    public Optional<Employee> findEmployeeByID(int id) {
        return repo.findByID(id);
    }

    /**
     * Updates an existing employee in the repository.
     *
     * @param employee the employee object with updated data
     */
    public void updateEmployee(Employee employee) {
        repo.update(employee);
    }

    /**
     * Deletes an employee by their ID.
     *
     * @param employeeId the ID of the employee to delete
     * @throws IllegalArgumentException if the ID is invalid or the employee is not found
     */
    public void deleteEmployeeById(int employeeId) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Invalid employee ID.");
        }

        boolean deleted = repo.deleteById(employeeId);
        if (!deleted) {
            throw new IllegalArgumentException("Employee with ID " + employeeId + " not found.");
        }
    }
}
