# Employee Management System (Console Application)

## Overview

This project is a console-based Employee Management System developed as part of a university programming assignment evaluated at exam level.

The application manages employees, skills, and certificates, including many-to-many assignments, using a clean, layered architecture and jOOQ for type-safe database access.

The focus of the project lies on correct database handling, clean architecture, and consistent user interaction, rather than UI frameworks.

# Features

## Employee Management

Create, update, delete, and search employees<br>
Search employees by last name using partial matches<br>
Display detailed employee information

## Skill Management

Create, update, delete, and search skills<br>
Search skills by name using partial, case-insensitive matches<br>
Assign skills to employees with configurable skill levels<br>
Update and remove assigned skills<br>
Display all skills assigned to a specific employee<br>

## Certificate Management

Create, update, delete, and search certificates<br>
Assign certificates to employees<br>
Update certificate assignments (status, validity, metadata)<br>
Remove certificate assignments<br>
Display certificates assigned to an employee<br>

## Assignments & Relationships

Employees ↔ Skills: many-to-many relationship<br>
Employees ↔ Certificates: many-to-many relationship<br>
All assignment operations are transaction-safe<br>
Foreign key constraints with cascading deletes are enforced<br>

## Database & Persistence

Uses SQLite as the underlying database<br>
The database file (employees.db) is shipped with the project to ensure a reproducible runtime environment<br>
jOOQ is used exclusively for database queries and mapping<br>
JDBC is only used for connection handling<br>
Foreign key constraints are enabled centrally (PRAGMA foreign_keys = ON)<br>
No runtime schema creation; database presence is verified at startup<br>

## Architecture

The application follows a clear layered structure:<br>

UI (Console Menus)<br>
    ↓<br>
Service Layer (Business Logic, Validation, Transactions)<br>
    ↓<br>
Repository Layer (jOOQ-based Data Access)<br>
    ↓<br>
SQLite Database<br>

## Design Principles

Separation of concerns (UI, logic, persistence)<br>
Repository pattern for database access<br>
Centralized validation and error handling<br>
Consistent search and sorting behavior across the application<br>

## Search Behavior

Employee search supports partial matches by last name<br>
Skill search supports partial, case-insensitive matches by name<br>
Sorting is handled centrally in repositories (e.g. skills sorted by ID)<br>

## Technologies Used

- Java

- jOOQ

- SQLite

- Maven

# **How to Run** ZU BEARBEITEN

1. Ensure Java and Maven are installed

2. Clone the repository

3. Make sure the database file exists at:

    data/employees.db


4. Run the application via the main class:

        App.java

# **Notes**

The project deliberately does not use migration tools such as Flyway or Liquibase
Database handling focuses on type-safe access and consistency, not schema evolution
The shipped database ensures predictable behavior in exam or review environments

## Status

Application compiles without errors
Core features (CRUD, search, assignments) tested and functional
Codebase cleaned up and ready for submission