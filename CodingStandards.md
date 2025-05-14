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
    - `/ui` (Components, views, screens)
    - `/logic` (Business logic and utilities)
    - `/model` (Data models)
    - `/persistence` (API, DB access, or storage layer)
    - `/tests` (test files??)

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

---

## 3. Formatting & Style

- **Indentation**
    - JavaScript/TypeScript: 2 spaces
    - Python/Java: 4 spaces

- **Line Length**: 
    - Max 100 characters

- **Braces**
    - JavaScript: opening brace on same line
    - Java: brace on new line for classes/methods

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
    - `// FIXME:` for known issues

- Use JSDoc or JavaDoc as appropriate ??

---

## 5. Code Organization

- **Order in Classes**
1. Constants
2. Fields
3. Constructors
4. Public methods
5. Private methods

- **Access Control**
    - Use `private` or `protected` where possible
    - Avoid public fields unless explicitly needed

---

## 6. Error Handling

- **Use `try-catch`** for:
    - Network calls ??
    - File operations ??
    - Critical logic prone to failure

- **Exception Handling**
    - Catch only expected exceptions
    - Log and rethrow unexpected issues
    - Use custom exception classes where helpful (e.g., `SlotUnavailableError`)

---

## 7. Version Control Practices

- **Branch Naming**
- `feature/appointment-form`
- `bugfix/cancel-crash`
- `chore/update-docs`

- **Commit Messages**
    - [type]: short message

    - **Examples:**
        - ```"feature: implemented slot manager"```
        - ```"fix: corrected time zone bug in scheduler"```


- **Pushing & Merging**
    - Push frequently ??
    - Merge only after PR review

- **Code Reviews**
    - All merges must be reviewed by at least one other team member ??
    - Provide clear, constructive and actionable feedback

---

## 8. Testing Standards

- **Coverage**: 
    - Aim for 80%+ unit test coverage

- **Naming**
    - Test files: `ComponentName.test.js`
    - Test cases: `shouldHandleEmptyInputCorrectly`

- **Structure**
    - Group by feature/component
    - Follow Given–When–Then pattern

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

---

<br>

All developers must read and follow this guide when contributing to the *PhysicianConnect* project. Any inquiries or proposals for changes should be discussed during regular team standups or code reviews.


