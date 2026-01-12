package de.htwsaar.domain;

public class Employee {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String role;
    private final String status;
    private final String hireDate;
    private final int workingHours;


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

    public int getID() {
        return this.id;
    }


}
