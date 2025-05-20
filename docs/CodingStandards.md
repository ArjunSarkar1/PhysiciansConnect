# PhysicianConnect – Coding Standards

<br>

This document outlines the agreed coding standards for the *PhysicianConnect* project. All contributors should follow these guidelines to ensure consistency, readability, and maintainability across the codebase.

<br>

---

## 1. File & Project Structure

- **File & Folder Naming**
  - Folders: `lowercase-with-hyphens` (e.g., `appointment-booking`)
  - Files: use camelCase or PascalCase based on language conventions (e.g., `AppointmentForm.jsx`)

- **Directory Layout**
    - **Modular Approach** 
        - `/src`
            - `/docs` ( General overview of what the functions and classes do )
            - `/main`
                - `/presentation` (Components, views, screens)
                - `/logic` (Business logic and utilities)
                - `/objects` (Data models, Classes related to the Domain problem)
                - `/persistence` (API, DB access, or storage layer)
                - `/execptions` ( Potentially have custom exceptions in here )
                
            - `/test`
                - `/type-of-tests`
                    - `/presentation` ( Not alot of testing mostly just going to be testing with mockito )
                    - `/logic` ( This is where alot of our validation and overall testin is going to be)
                    - `/persistence` ( Just making sure that we're saving stuff ot the backend correctly )
                - `/resources` ( Refers to SQL insert and SQL cleanup, potentially: we also have our stub databases in here )
        

---

## 2. Naming Conventions

- **Classes**:
    - `PascalCase` (e.g., `PatientProfile`)

- **Variables & Methods**: 
    - `camelCase` (e.g., `getPatientDetails`)

- **Constants**: 
    - `ALL_CAPS_WITH_UNDERSCORES` (e.g., `MAX_RESULTS`)

- **General Rule**:
    - Always use clear names rather than abbreviations (`appointmentTime`, not `aptTm`)

- **Testing**:
    - `snake_case`  Using the format of `given_when_expect` (e.g., `given_invalid_patient_when_creating_an_appointment_we_should_fail`)


---

## 3. Formatting & Style

- **Indentation**
    - Java: 4 spaces or the same as a default tab

- **Line Length**: 
    - Max 100 characters

- **Braces**
    - Java: brace on the same line for as the header

- **Blank Lines**
    - Between methods
    - Before `return` statements
    - Between logical sections in code

---

## 4. Commenting Practices

- **Required Comments**
    - Public methods and classes must include header comments
    - Complex logic should include inline explanations

- **Use Tags**
    - `// TODO:` for upcoming tasks
    - `// FIXME:` for known issues ( Preferable utilize Gitlab for these issues or TODOs)

- **Optional Comments**
    - Before stopping and or while working ptu a little summary or what you were doing and your train of thought
---

## 5. Code Organization

- **Order in Classes**
    1. Imports
    2. Constants
    3. Fields
    4. Constructors
    5. Public methods
    6. Private methods

- **Access Control**
    - Use `private` or `protected` where possible
    - Avoid public fields unless explicitly needed
---

## 6. Error Handling

- **Use `try-catch`** for:
    - Critical logic prone to failure

- **Exception Handling**
    - Catch only expected exceptions
    - Log and re-throw unexpected issues
    - Use custom exception classes where helpful (e.g., `SlotUnavailableError`)

---

## 7. Version Control Practices
### ___DO NOT COPY PASTE GIT COMMANDS FROM AI OR STACK OVERFLOW___
- **Branch Naming**
    - `appointment-form`
    - `cancel-crash`
    - `update-docs`

- **Commit Messages**
    - [type]: short message

    - **Examples:**
        - ```"feature: implemented slot manager"```
        - ```"fix: corrected time zone bug in scheduler"```


- **Pushing & Merging**
    - Push frequently, because we can always squash it later
    - Merge only after PR review

- **Code Reviews**
    - All merges must be reviewed by at least one other team member
    - Provide clear, constructive and actionable feedback

---

## 8. Testing Standards

- **Coverage**: 
    - Aim for 80%+ unit test coverage

- **Structure**
    - Group by feature/component
    - You can also group by black box or white box testing
    - Ask for help or talk to other before saying you're done

- **Mocks**
    - Use mocks for APIs and dependencies

---

## 9. Dependency Management

- **Approved Libraries**
    - UI: ??
    - Testing: JUnit
    - Backend: ??

- **Guidelines**
    - Use only vetted libraries ??
    - Document new third-party additions ??
    - Avoid overloading with dependencies ??

---

## 10. Collaboration & Workflow Norms

- **Pair Programming**:
    - Encouraged for complex logic or problem solving puroposes

- **Pull Requests**
    - Keep PRs concise and focused to improve quick code readability performing it thoroughly
    - Reference related issues or features in the PR (proper description)

- **Decision Logging**
    - Track major decisions in `/docs/decision-log.md`

- **GEN AI usage**
    - Its good to use it but don't over do it
    - Remember to take out emoji and stuff.

---

<br>

All developers must read and follow this guide when contributing to the *PhysicianConnect* project. Any inquiries or proposals for changes should be discussed during regular team standups or code reviews.


