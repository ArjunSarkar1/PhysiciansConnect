Here’s an updated `RELEASE.md` that includes **setup instructions** and a **note about using the temporary "Go to Dashboard" button** due to the login system not being implemented yet:

---

# RELEASE.md

## PhysicianConnect – Initial Release

PhysicianConnect is a patient-physician collaboration platform designed to enhance communication, appointment scheduling, and care planning. This release lays the groundwork for a secure, user-friendly system that connects patients with their healthcare providers efficiently.

---

## Key Features

* **Direct Appointment Booking:**
  Patients can schedule appointments with specific physicians based on availability. Physicians can modify schedules as needed.

* **Patient Visit & Medical Record Management:**
  Physicians can view medical histories, add visit notes, prescribe medications, and create referrals for tests or specialists.

* **Pre-Visit Preparation for Patients:**
  Patients can complete pre-visit forms and note discussion topics to make appointments more effective.

* **Care Plan Delivery:**
  After appointments, patients receive easy-to-understand summaries of diagnoses, prescriptions, referrals, and recommended resources.

* **Privacy Controls:**
  Patients can only view their own data; physicians have role-based access to relevant patient information, ensuring privacy and compliance.

---

## Setup Instructions

1. **Ensure Prerequisites:**

   * Java JDK 21 or compatible runtime installed.
   * Gradle installed (7.6 or newer recommended).
   * SQLite is used as the embedded database.

2. **Clone the Repository & Navigate to the Project Folder:**

   ```bash
   git clone https://code.cs.umanitoba.ca/comp3350-summer2025/a01-g08-todo-everything.git
   cd physicianconnect
   ```

3. **Build and Run the Application:**

   ```bash
   ./gradlew run
   ```

4. **Launching the Interface:**
   Once the application starts, you’ll see the launch screen.

   > **Login functionality is not yet implemented.**
   > For now, click the **“Go to Dashboard”** button to proceed to the application interface.


---

## Performance Goals

This version is expected to:

* Improve appointment booking time by 30% or more.
* Reduce administrative coordination costs by at least 25%.
* Achieve 85%+ satisfaction from patients and providers in feedback surveys.

---

## Roadmap

Future updates will include:

* Full login and user management system.
* Integrated messaging and teleconsultation tools.
* Automatic import of external medical data.
* Enhanced accessibility and multilingual support.

---

## Summary

PhysicianConnect is a patient-centered healthcare app that simplifies administrative workflows and enhances the quality of patient-physician interaction. This initial release provides core functionality with a focus on clarity, privacy, and ease of use.

We welcome feedback to help shape future iterations. For issues, suggestions, or contributions, please reach out to the development team or refer to the project documentation.

---
