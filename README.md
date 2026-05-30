# Student Management System

A JavaFX + JDBC + PostgreSQL desktop CRUD application built as an activity for learning database-integrated GUI development.

---

## Features

| Feature | Description |
|---|---|
| Add | Insert a new student record into the database |
| View | All records displayed in a TableView on startup |
| Update | Click a row, edit fields, then click Update |
| Delete | Remove a selected student (with confirmation dialog) |
| Clear | Reset all input fields |
| Validation | Prevents empty fields from being submitted |
| Status bar | Shows live feedback after every operation |

---

## Tech Stack

- Java 17
- JavaFX 21 - GUI framework
- SceneBuilder - FXML visual design
- JDBC - Java Database Connectivity
- Supabase (PostgreSQL) - Cloud-hosted relational database
- Maven - Dependency and build management

---

## Project Structure
StudManagementSys/
├── pom.xml
├── database/
│   └── setup.sql
└── src/
└── main/
├── java/com/studentms/
│   ├── MainApp.java
│   ├── Controller.java
│   ├── Student.java
│   ├── YearLevel.java
│   └── DBConnection.java
└── resources/com/studentms/
└── main.fxml
