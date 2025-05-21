
---

## PhysicianConnect – Initial Release

PhysicianConnect is a patient-physician collaboration platform designed to enhance communication, appointment scheduling, and care planning. 

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
   * Gradle installed (7.6 or newer recommended) []
   * SQLite is used as the embedded database.



   ```bash
   git clone https://code.cs.umanitoba.ca/comp3350-summer2025/a01-g08-todo-everything.git
   cd physicianconnect
   gradle build -x test 
   gradle run
   ```


4. **Launching the Interface:**
   Once the application starts, you’ll see the launch screen.

   > **Login functionality is not yet implemented.**
   > **Test does not seem to work for now hence**
   > **Features except the Physician Name is not working we wil fix that**
   > For now, click the **“Go to Dashboard”** button to proceed to the application interface. 


---


