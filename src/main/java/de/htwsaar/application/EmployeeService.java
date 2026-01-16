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
                                   String email, String role, String status,
                                   String hireDate, int workingHours) {
        try {

            if (invalid(firstName) || invalid(lastName) || invalid(email) ||
                    invalid(role) || invalid(status) || invalid(hireDate)) {
                throw new IllegalArgumentException("All fields must be filled out.");
            }

            if (workingHours <= 0) {
                throw new IllegalArgumentException("Working hours must be greater than 0.");
            }

            if (workingHours > 60) {
                throw new IllegalArgumentException("Working hours must not exceed 60 per week.");
            }


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

    public List<Employee> findAllEmployees() {
        return repo.findAll();
    }

    public Optional<Employee> findEmployeeByID(int id) {
        return repo.findByID(id);
    }
}
