

package de.htwsaar;

import de.htwsaar.domain.Employee;
import de.htwsaar.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;

public class MockRepository extends EmployeeRepository {

    public MockRepository() {
        super(null);
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> list = new ArrayList<>();
        list.add(new Employee(1, "Mia", "Müller", "mia.müller1@firma.de", "QA", "Active", "2023-07-18", 35));
        list.add(new Employee(2, "Ben", "Schmidt", "ben.schmidt2@firma.de", "Developer", "Active", "2019-03-29", 30));
        return list;
    }

    @Override
    public Employee save(String fn, String ln, String e, String r, String s, String hd, int wh) {
        return new Employee(99, fn, ln, e, r, s, hd, wh);
    }
}