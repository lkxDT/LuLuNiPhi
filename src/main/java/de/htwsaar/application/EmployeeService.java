package de.htwsaar.application;

import java.util.List;
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


        return this.repo.save(firstName, lastName, email, role, status, hireDate, workingHours);
    }

    public List<Employee> getAllEmployees() {
        return repo.findAll();
    }
}
