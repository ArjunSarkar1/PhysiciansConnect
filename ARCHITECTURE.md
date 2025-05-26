flowchart TD
    %% Presentation Layer
    UI[Presentation Layer<br/>physicianconnect.presentation<br/>LoginScreen, PhysicianApp]
    
    %% Logic Layer
    LM[Logic Layer<br/>physicianconnect.logic<br/>PhysicianManager, AppointmentManager, AppointmentValidator]

    %% Domain Objects
    OBJ[Domain Objects<br/>physicianconnect.objects<br/>Physician, Appointment, Medication]

    %% Exceptions
    EXC[Exceptions<br/>physicianconnect.exceptions<br/>InvalidAppointmentException]

    %% Persistence Interfaces
    PINT[Persistence Interfaces<br/>physicianconnect.persistence<br/>PhysicianPersistence, AppointmentPersistence, MedicationPersistence]

    %% Stub DB
    STUB[Stub DB<br/>physicianconnect.persistence.stub<br/>in-memory logic]

    %% SQLite DB
    SQLITE[SQLite DB<br/>physicianconnect.persistence.sqlite<br/>PhysicianDB, AppointmentDB, MedicationDB, SchemaInitializer, DatabaseSeeder]

    %% Connection Manager
    CM[Connection Manager<br/>physicianconnect.persistence.ConnectionManager]

    %% Composition Root
    MAIN[Main Launcher<br/>App.java]

    %% Presentation to Logic
    UI --> LM

    %% Logic to Domain
    LM --> OBJ
    LM --> EXC

    %% Logic to Persistence Interface
    LM --> PINT

    %% Persistence Interface branches to stub/sqlite
    PINT --> STUB
    PINT --> SQLITE

    %% SQLite depends on Connection
    SQLITE --> CM

    %% App.java launches the system
    MAIN --> UI
    MAIN --> PINT
