# Employee Management System (Console Application)

## Overview

This project is a console-based Employee Management System developed as part of a university programming assignment evaluated at exam level.

The application manages employees, skills, and certificates, including many-to-many assignments, using a clean, layered architecture and jOOQ for type-safe database access.

The focus of the project lies on correct database handling, clean architecture, and consistent user interaction, rather than UI frameworks.

# Features

## Employee Management

Create, update, delete, and search employees

Search employees by last name using partial matches

Display detailed employee information

## Skill Management

Create, update, delete, and search skills

Search skills by name using partial, case-insensitive matches

Assign skills to employees with configurable skill levels

Update and remove assigned skills

Display all skills assigned to a specific employee

## Certificate Management

Create, update, delete, and search certificates

Assign certificates to employees

Update certificate assignments (status, validity, metadata)

Remove certificate assignments

Display certificates assigned to an employee

## Assignments & Relationships

Employees ↔ Skills: many-to-many relationship

Employees ↔ Certificates: many-to-many relationship

All assignment operations are transaction-safe

Foreign key constraints with cascading deletes are enforced

## Database & Persistence

Uses SQLite as the underlying database

The database file (employees.db) is shipped with the project to ensure a reproducible runtime environment

jOOQ is used exclusively for database queries and mapping

JDBC is only used for connection handling

Foreign key constraints are enabled centrally (PRAGMA foreign_keys = ON)

No runtime schema creation; database presence is verified at startup

## Architecture

The application follows a clear layered structure:

UI (Console Menus)
    ↓
Service Layer (Business Logic, Validation, Transactions)
    ↓
Repository Layer (jOOQ-based Data Access)
    ↓
SQLite Database

## Design Principles

Separation of concerns (UI, logic, persistence)

Repository pattern for database access

Centralized validation and error handling

Consistent search and sorting behavior across the application

## Search Behavior

Employee search supports partial matches by last name

Skill search supports partial, case-insensitive matches by name

Sorting is handled centrally in repositories (e.g. skills sorted by ID)

## Technologies Used

- Java

- jOOQ

- SQLite

- Maven

# **How to Run**

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