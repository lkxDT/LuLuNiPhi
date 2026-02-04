package de.htwsaar.domain;

/**
 * Represents an employee within the system.
 * <p>
 * An employee contains personal, organizational and employment-related
 * information and is used as a read-only domain object.
 * This class is immutable.
 */
public class Employee {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String role;
    private final String status;
    private final String hireDate;
    private final int workingHours;


    /**
     * Creates a new employee instance.
     *
     * @param id            unique employee ID
     * @param firstName     first name
     * @param lastName      last name
     * @param email         email address
     * @param role          job role
     * @param status        employment status
     * @param hireDate      hire date (yyyy-mm-dd)
     * @param workingHours  working hours per week
     */
    public Employee(int id, String firstName, String lastName,
                    String email, String role, String status,
                    String hireDate, int workingHours) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.status = status;
        this.hireDate = hireDate;
        this.workingHours = workingHours;
    }

    public int getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getRole() {
        return this.role;
    }

    public String getStatus() {
        return this.status;
    }

    public String getHireDate() {
        return this.hireDate;
    }

    public int getWorkingHours() {
        return this.workingHours;
    }

    /**
     * Returns a formatted string representation of the employee,
     * suitable for console output.
     *
     * @return formatted employee string
     */
    @Override
    public String toString() {
        return String.format("%5d | %15s | %15s | %35s | %10s | %8s | %11s | %2d",
                this.getId(),
                this.getFirstName(),
                this.getLastName(),
                this.getEmail(),
                this.getRole(),
                this.getStatus(),
                this.getHireDate(),
                this.getWorkingHours()
        );
    }
}
