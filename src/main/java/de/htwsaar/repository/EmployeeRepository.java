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

/**
 * Repository for managing {@link Employee} persistence using jOOQ.
 * <p>
 * Provides CRUD operations and helper queries such as searching
 * by last name and checking email uniqueness.
 */
public class EmployeeRepository {

    private final DSLContext dsl;

    /**
     * Creates a repository using a JDBC connection.
     *
     * @param conn database connection
     */
    public EmployeeRepository(Connection conn) {
        this.dsl = using(conn);
    }

    /**
     * Saves a new employee in the database.
     *
     * @param firstName     employee first name
     * @param lastName      employee last name
     * @param email         employee email address
     * @param role          employee role
     * @param status        employee status
     * @param hireDate      hire date (yyyy-mm-dd)
     * @param workingHours  working hours per week
     * @return the created Employee including generated ID
     * @throws RuntimeException if a database error occurs
     */
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

    /**
     * Finds an employee by ID.
     *
     * @param id employee ID
     * @return an Optional containing the employee if found, otherwise empty
     * @throws RuntimeException if a database error occurs
     */
    public Optional<Employee> findByID(int id) {
        try {
            return dsl.selectFrom(EMPLOYEE)
                    .where(EMPLOYEE.ID.eq(id))
                    .fetchOptional(this::mapEmployee);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while finding employee by id", e);
        }
    }

    /**
     * Searches employees by last name using a case-insensitive LIKE query.
     *
     * @param lastName last name (or part of it) to search for
     * @return list of matching employees
     * @throws RuntimeException if a database error occurs
     */
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

    /**
     * Loads all employees ordered by ID.
     *
     * @return list of all employees
     * @throws RuntimeException if a database error occurs
     */
    public List<Employee> findAll() {
        try {
            return dsl.selectFrom(EMPLOYEE)
                    .orderBy(EMPLOYEE.ID.asc())
                    .fetch(this::mapEmployee);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while loading all employees", e);
        }
    }

    /**
     * Checks whether an employee with the given email exists.
     *
     * @param email email address to check
     * @return true if the email exists
     * @throws RuntimeException if a database error occurs
     */
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

    /**
     * Checks whether an email exists for a different employee than the given ID.
     * Used to validate uniqueness during updates.
     *
     * @param email       email address to check
     * @param employeeId  employee ID to exclude
     * @return true if the email exists for another employee
     * @throws RuntimeException if a database error occurs
     */
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

    /**
     * Updates an existing employee in the database.
     *
     * @param emp employee with updated values
     * @throws RuntimeException if a database error occurs
     */
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

    /**
     * Deletes an employee by ID.
     * <p>
     * Also removes related entries from join tables (employee_skill
     * and employee_certificate) in a transaction.
     *
     * @param employeeId employee ID
     * @return true if the employee was deleted, otherwise false
     * @throws RuntimeException if a database error occurs
     */
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

    /**
     * Maps a jOOQ {@link Record} to an {@link Employee}.
     *
     * @param r database record
     * @return mapped Employee instance
     */
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