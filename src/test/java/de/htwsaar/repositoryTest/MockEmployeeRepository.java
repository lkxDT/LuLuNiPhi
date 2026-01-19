package de.htwsaar.repositoryTest;

import de.htwsaar.domain.Employee;
import de.htwsaar.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MockEmployeeRepository extends EmployeeRepository {
    private final List<Employee> database = new ArrayList<>();
    public MockEmployeeRepository() {
        super(null);
        database.add(new Employee(1, "Mia", "Müller", "mia.müller1@firma.de", "QA", "Active", "2023-07-18", 35));
        database.add(new Employee(2, "Ben", "Schmidt", "ben.schmidt2@firma.de", "Developer", "Active", "2019-03-29", 30));
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(database);
    }

    @Override
    public boolean existsByEmail(String email) {
        return database.stream().anyMatch(e -> e.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public Employee save(String fn, String ln, String e, String r, String s, String hd, int wh) {
        Employee newEmp = new Employee(database.size() + 1, fn, ln, e, r, s, hd, wh);
        database.add(newEmp);
        return newEmp;
    }

    @Override
    public List<Employee> findByLastName(String lastName) {
        return database.stream()
                .filter(e -> e.getLastName().equalsIgnoreCase(lastName.trim()))
                .collect(Collectors.toList());
    }

    public void clear() {
        this.database.clear();
    }

}