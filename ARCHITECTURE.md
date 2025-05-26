```mermaid
flowchart TD
    UI[Presentation Layer - physicianconnect.presentation - LoginScreen, PhysicianApp]
    LM[Logic Layer - physicianconnect.logic - PhysicianManager, AppointmentManager, AppointmentValidator]
    OBJ[Domain Objects - physicianconnect.objects - Physician, Appointment, Medication]
    EXC[Exceptions - physicianconnect.exceptions - InvalidAppointmentException]
    PI[Persistence Interfaces - physicianconnect.persistence - PhysicianPersistence, AppointmentPersistence, MedicationPersistence]
    STUB[Stub DB (Test) - physicianconnect.persistence.stub]
    SQLITE[SQLite DB (Prod) - physicianconnect.persistence.sqlite - AppointmentDB, MedicationDB, PhysicianDB, SchemaInitializer, DatabaseSeeder]
    CM[Infrastructure - ConnectionManager]
    MAIN[App Entry Point - App.java]

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