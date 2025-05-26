```mermaid
flowchart TD
    %% Presentation Layer
    UI[Presentation Layer<br/>physicianconnect.presentation<br/>LoginScreen, PhysicianApp]

    %% Logic Layer
    LM[Logic Layer<br/>physicianconnect.logic<br/>PhysicianManager, AppointmentManager, AppointmentValidator]

    %% Domain Objects
    OBJ[Domain Objects<br/>physicianconnect.objects<br/>Physician, Appointment, Medication]

    %% Exceptions
    EXC[Exceptions<br/>physicianconnect.exceptions<br/>InvalidAppointmentException]

    %% Persistence Interfaces and Implementations
    PI[Persistence Interfaces<br/>physicianconnect.persistence<br/>PhysicianPersistence, AppointmentPersistence, MedicationPersistence]
    STUB[Stub DB (Test)<br/>physicianconnect.persistence.stub]
    SQLITE[SQLite DB (Prod)<br/>physicianconnect.persistence.sqlite<br/>AppointmentDB, MedicationDB, PhysicianDB, SchemaInitializer, DatabaseSeeder]

    %% Infrastructure
    CM[Infrastructure<br/>ConnectionManager]

    %% Main Entry Point
    MAIN[App Entry Point<br/>App.java]

    %% Flow Connections
    MAIN --> UI
    MAIN --> LM
    MAIN --> PI

    UI --> LM
    LM --> OBJ
    LM --> EXC
    LM --> PI

    PI --> STUB
    PI --> SQLITE
    SQLITE --> CM
```