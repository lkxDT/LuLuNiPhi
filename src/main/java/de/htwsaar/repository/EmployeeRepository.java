package de.htwsaar.repository;

import de.htwsaar.domain.Employee;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static de.htwsaar.jooq.tables.Employee.EMPLOYEE;
import static de.htwsaar.jooq.tables.EmployeeCertificate.EMPLOYEE_CERTIFICATE;
import static de.htwsaar.jooq.tables.EmployeeSkill.EMPLOYEE_SKILL;
import static de.htwsaar.util.JooqContext.using;

public class EmployeeRepository {

    private final DSLContext dsl;

    public EmployeeRepository(Connection conn) {
        this.dsl = using(conn);
    }

    public Employee save(String firstName, String lastName,
                         String email, String role, String status,
                         String hireDate, int workingHours) {

        try {
            // Insert + generated ID (SQLite)
            Integer newId = dsl.insertInto(EMPLOYEE)
                    .set(EMPLOYEE.FIRST_NAME, firstName)
                    .set(EMPLOYEE.LAST_NAME, lastName)
                    .set(EMPLOYEE.MAIL, email)
                    .set(EMPLOYEE.ROLE, role)
                    .set(EMPLOYEE.STATUS, status)
                    .set(EMPLOYEE.HIRE_DATE, hireDate)
                    .set(EMPLOYEE.WORKING_HOURS, (float) workingHours)
                    .returning(EMPLOYEE.ID)
                    .fetchOne(EMPLOYEE.ID);

            if (newId == null) {
                throw new IllegalStateException("No generated key returned for employee insert.");
            }

            return new Employee(newId, firstName, lastName, email, role, status, hireDate, workingHours);

        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while saving employee", e);
        }
    }

    public Optional<Employee> findByID(int id) {
        try {
            return dsl.selectFrom(EMPLOYEE)
                    .where(EMPLOYEE.ID.eq(id))
                    .fetchOptional(this::mapEmployee);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while finding employee by id", e);
        }
    }

    public List<Employee> findByLastName(String lastName) {
        try {
            String pattern = "%" + lastName.trim() + "%";
            return dsl.selectFrom(EMPLOYEE)
                    .where(EMPLOYEE.LAST_NAME.likeIgnoreCase(pattern))
                    .orderBy(EMPLOYEE.LAST_NAME.asc())
                    .fetch(this::mapEmployee);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while searching employees by last name", e);
        }
    }

    public List<Employee> findAll() {
        try {
            return dsl.selectFrom(EMPLOYEE)
                    .orderBy(EMPLOYEE.ID.asc())
                    .fetch(this::mapEmployee);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while loading all employees", e);
        }
    }

    public boolean existsByEmail(String email) {
        try {
            return dsl.fetchExists(
                    dsl.selectOne()
                            .from(EMPLOYEE)
                            .where(EMPLOYEE.MAIL.eq(email))
            );
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while checking email existence", e);
        }
    }

    public boolean existsByEmailForOtherEmployee(String email, int employeeId) {
        try {
            return dsl.fetchExists(
                    dsl.selectOne()
                            .from(EMPLOYEE)
                            .where(EMPLOYEE.MAIL.eq(email))
                            .and(EMPLOYEE.ID.ne(employeeId))
            );
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while checking email existence for other employee", e);
        }
    }

    public void update(Employee emp) {
        try {
            dsl.update(EMPLOYEE)
                    .set(EMPLOYEE.FIRST_NAME, emp.getFirstName())
                    .set(EMPLOYEE.LAST_NAME, emp.getLastName())
                    .set(EMPLOYEE.MAIL, emp.getEmail())
                    .set(EMPLOYEE.ROLE, emp.getRole())
                    .set(EMPLOYEE.STATUS, emp.getStatus())
                    .set(EMPLOYEE.HIRE_DATE, emp.getHireDate())
                    .set(EMPLOYEE.WORKING_HOURS, (float) emp.getWorkingHours())
                    .where(EMPLOYEE.ID.eq(emp.getId()))
                    .execute();
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while updating employee", e);
        }
    }

    public boolean deleteById(int employeeId) {
        try {
            return dsl.transactionResult(cfg -> {
                DSLContext tx = org.jooq.impl.DSL.using(cfg);

                // robust: erst Join-Tabellen löschen
                tx.deleteFrom(EMPLOYEE_SKILL)
                        .where(EMPLOYEE_SKILL.EMPLOYEE_ID.eq(employeeId))
                        .execute();

                tx.deleteFrom(EMPLOYEE_CERTIFICATE)
                        .where(EMPLOYEE_CERTIFICATE.EMPLOYEE_ID.eq(employeeId))
                        .execute();

                int affected = tx.deleteFrom(EMPLOYEE)
                        .where(EMPLOYEE.ID.eq(employeeId))
                        .execute();

                return affected > 0;
            });
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while deleting employee: " + e.getMessage(), e);
        }
    }

    private Employee mapEmployee(Record r) {
        return new Employee(
                r.get(EMPLOYEE.ID),
                r.get(EMPLOYEE.FIRST_NAME),
                r.get(EMPLOYEE.LAST_NAME),
                r.get(EMPLOYEE.MAIL),
                r.get(EMPLOYEE.ROLE),
                r.get(EMPLOYEE.STATUS),
                r.get(EMPLOYEE.HIRE_DATE),
                r.get(EMPLOYEE.WORKING_HOURS).intValue()
        );
    }
}