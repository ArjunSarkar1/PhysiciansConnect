# VISION STATEMENT

For Physicians who need a clear and simple way to manage their day
PhysicianConnect is a tool that helps them stay on top of appointments, prepare for visits, and keep track of what happens with each patient.
Unlike messy paper notes or complicated software,
PhysicianConnect keeps everything organized in one place so Physicians can spend less time planning and more time caring.
Physicians use PhysicianConnect to set when they’re available, see who they’re meeting with, and write notes before and after each visit. They can also track what was discussed and what comes next, including medications or follow-ups.
Patients book their appointments through a separate website. As soon as a booking is made, it shows up for the doctor—along with any notes the patient added.
Everything is private and secure. Physicians see only what they need to do their job, and patients only use the booking site.

The project will be a success if:

More than 85% of Physicians say it helps them stay organized
Appointment planning takes 30% less time
Physicians spend 25% less time writing things down or getting ready


---
# README
# PhysicianConnect (Physician View)

PhysicianConnect is a healthcare coordination platform designed to improve how physicians manage appointments, review patient history, and prescribe medications. This version focuses specifically on physician-facing features, supporting a streamlined and efficient experience for care providers. The system is structured with a 3-tier architecture, separating the presentation (UI), business logic (services), and persistence (database access) layers to ensure modularity, scalability, and testability.

## Features

- **Appointment Slot Management:** Add, update, or remove available time slots for patient bookings.
- **View Patient Medical History:** Access past visit summaries, detailed examination records, and patient health timelines.
- **Prescribe Medication:** Search a dynamic formulary, select safe medications, add dosage details, and include prescription notes.
- **Navigation Dashboard:** Tab-based physician interface offering access to all workflows from a centralized location.
- **Health Education Resource Sharing:** *(Planned)* Share exercise plans, nutrition guides, and health tips directly with patients.

## Dependencies & Versions

- **Java:** JDK 21.0.6 (or a compatible version)
- **Gradle:** Gradle 7.6 (or similar version)
- **SQLite JDBC:** org.xerial:sqlite-jdbc:3.36.0.3  
  *(Provides the SQLite driver for embedded database support)*
- **JUnit 5:** For unit and integration testing

## Setting Up & Running

The application uses a composition root (typically in the `main()` method) to wire up the correct persistence implementation (stub, test, or production). In production mode, the app uses an SQLite database.

### Running the Application

# Clone the Repository
If you haven't already cloned the repository, do so using:
```bash
git clone https://code.cs.umanitoba.ca/comp3350-summer2025/a01-g08-todo-everything.git
```

# Navigate to the Application Directory
From the root of the cloned project, navigate to the app folder:
```bash
cd a01-g08-todo-everything/physiciansconnect/app
```
Use forward slashes (/) for Unix-based systems (macOS, Linux) and backslashes (\) for Windows in Command Prompt or PowerShell.

# Run the Application
Use Gradle to start the application:
```bash
./gradlew run
```
On Windows (PowerShell or CMD), if you encounter permission issues with ./gradlew, try:
```bash
./gradlew.bat run
```

## Running with the .jar File

# Navigate to the Application Directory
From the root of the cloned project, navigate to the app folder:
```bash
cd a01-g08-todo-everything/physiciansconnect/app
```
Use forward slashes (/) for Unix-based systems (macOS, Linux) and backslashes (\) for Windows in Command Prompt or PowerShell.

# Run the Application
To run the application use:
```bash
java -jar physiciansconnect-1.0-all.jar
```

## Database Management

The SQLite database is stored as a single file (for example, `prod.db` for production). The application automatically creates the necessary tables and seeds initial data if needed.

### Wiping the Database

To wipe the database from the command line:

1. **Delete the Database File:**

   On macOS/Linux:
   ```bash
   rm path/to/prod.db
   ```

   On Windows:
   ```cmd
   del path\to\prod.db
   ```

2. **Using the SQLite Command-Line Tool:**

   To delete all records from the `notes` table:
   ```bash
   sqlite3 path/to/prod.db "DELETE FROM notes;"
   ```

   To drop the entire `notes` table:
   ```bash
   sqlite3 path/to/prod.db "DROP TABLE IF EXISTS notes;"
   ```

After wiping the database, the application will recreate the necessary tables on startup.

## 3-Tier Architecture

SummerNotes is designed following a 3-tier architecture, which separates the application into three distinct layers:

### 1. Presentation Layer (UI)

- **Purpose:**  
  This layer is responsible for all user interactions. In SummerNotes, the presentation layer is implemented using Swing (or can be swapped out for another UI framework if needed). It displays notes, handles user input (like clicking buttons or entering text), and shows the results.

- **Key Characteristics:**  
  - Decoupled from the business logic and persistence layers.
  - Only communicates with the business logic (NoteManager) to perform actions.
  - Responsible for displaying feedback (errors, confirmations) to the user.

### 2. Business Logic Layer

- **Purpose:**  
  The business logic layer handles the core operations of your application. It enforces business rules, performs validations, and mediates between the UI and the data layer. In SummerNotes, this is primarily handled by the `NoteManager` class.

- **Key Characteristics:**  
  - Acts as an intermediary between the presentation layer and persistence layer.
  - Contains the logic to add, edit, delete, search, and retrieve notes.
  - Delegates data storage and retrieval tasks to the persistence layer.
  - Remains independent of how data is actually stored.

### 3. Persistence Layer (Data Access)

- **Purpose:**  
  The persistence layer is responsible for all interactions with the data storage. In PhysicianConnect, you have multiple implementations (a stub for testing, and a production implementation using SQLite). This layer encapsulates all the SQL queries and data manipulation logic.

- **Key Characteristics:**  
  - Implements a common interface, allowing the business logic to remain unaware of the underlying database.
  - Manages the creation of tables, seeding of data, and CRUD operations.
  - Can be swapped out easily (for example, from SQLite to another database) without impacting the rest of the application.

### Benefits of 3-Tier Architecture

- **Modularity:**  
  Each layer can be developed, tested, and maintained independently.

- **Flexibility:**  
  Changes to one layer (for example, updating the UI or changing the database engine) have minimal impact on other layers.

- **Scalability & Maintainability:**  
  Clear separation of concerns makes the application easier to extend and troubleshoot over time.

---

## Summary

PhysicianConnect for Physicians empowers care providers with tools to manage their schedule, view patient history, and prescribe treatments — all within a clean, modular application. Designed using a 3-tier architecture and supported by SQLite, it balances simplicity with scalability, making it easy to extend and maintain over time.

For questions, testing setup, or to contribute, please refer to the project documentation or contact the development team.
