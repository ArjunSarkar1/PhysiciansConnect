```mermaid
flowchart TD
    %% Theme & Styling
    classDef entryPoint fill:#e3f2fd,stroke:#90caf9,stroke-width:2px;
    classDef presentation fill:#fff3e0,stroke:#ffb74d,stroke-width:2px;
    classDef logic fill:#e8f5e9,stroke:#81c784,stroke-width:2px;
    classDef domain fill:#ede7f6,stroke:#9575cd,stroke-width:2px;
    classDef exceptions fill:#ffebee,stroke:#e57373,stroke-width:2px;
    classDef persistence fill:#e0f7fa,stroke:#4dd0e1,stroke-width:2px;
    classDef infra fill:#f3e5f5,stroke:#ce93d8,stroke-width:2px;
    classDef config fill:#fce4ec,stroke:#f48fb1,stroke-width:2px;

    %% Nodes
    MAIN["App Entry Point<br><b>App.java</b>"]:::entryPoint

    CONFIG["Configuration<br><code>physicianconnect.config</code><br>AppConfig"]:::config

    UI["Presentation Layer<br><code>physicianconnect.presentation</code><br>LoginScreen, PhysicianApp"]:::presentation

    LM["Logic Layer<br><code>physicianconnect.logic</code><br>PhysicianManager, AppointmentManager, AppointmentValidator"]:::logic

    OBJ["Domain Objects<br><code>physicianconnect.objects</code><br>Physician, Appointment, Medication"]:::domain

    EXC["Exceptions<br><code>physicianconnect.exceptions</code><br>InvalidAppointmentException"]:::exceptions

    PI["Persistence Interfaces<br><code>physicianconnect.persistence</code><br>PhysicianPersistence, AppointmentPersistence, MedicationPersistence"]:::persistence

    STUB["Stub DB (Test)<br><code>physicianconnect.persistence.stub</code>"]:::persistence

    SQLITE["SQLite DB (Prod)<br><code>physicianconnect.persistence.sqlite</code><br>AppointmentDB, MedicationDB, PhysicianDB, SchemaInitializer, DatabaseSeeder"]:::persistence

    CM["Infrastructure<br>ConnectionManager"]:::infra

    %% Flow Connections
    MAIN --> CONFIG
    MAIN --> UI
    MAIN --> LM
    MAIN --> PI

    CONFIG --> PI

    UI --> LM
    LM --> OBJ
    LM --> EXC
    LM --> PI

    PI --> STUB
    PI --> SQLITE
    SQLITE --> CM
```

## Dependency Injection Configuration

The application uses a simple but effective dependency injection pattern to switch between different persistence implementations. This is achieved through the following components:

### 1. AppConfig
Located in `physicianconnect.config.AppConfig`, this class provides a centralized configuration point for the application. It manages:
- The persistence type (PROD, TEST, or STUB)
- Whether to seed initial data
- Other application-wide settings

### 2. PersistenceType Enum
Defines the available persistence implementations:
- `PROD`: Uses SQLite database for production
- `TEST`: Uses SQLite database with test configuration
- `STUB`: Uses in-memory stub implementations for testing

### 3. PersistenceFactory
The factory class that creates and manages persistence implementations based on the configuration. It provides:
- Centralized initialization of all persistence components
- Automatic fallback to stub implementations if database initialization fails
- Clean separation between persistence interfaces and their implementations

### Usage Example

To switch between persistence implementations, simply update the configuration:

```java
// Use SQLite in production
AppConfig.setPersistenceType(PersistenceType.PROD);

// Use in-memory stubs for testing
AppConfig.setPersistenceType(PersistenceType.STUB);

// Use SQLite with test configuration
AppConfig.setPersistenceType(PersistenceType.TEST);
```

The application will automatically use the appropriate implementation based on this configuration.