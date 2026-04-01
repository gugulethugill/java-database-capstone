# Smart Clinic Schema Design

This document outlines the database design for the Smart Clinic Management System using both MySQL (relational) and MongoDB (NoSQL). The system is designed to handle structured operational data in MySQL and flexible, evolving data in MongoDB.

---

## MySQL Database Design

Structured data such as users, appointments, and relationships between entities are stored in MySQL.

### Table: patients
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100), Not Null
- email: VARCHAR(100), Unique, Not Null
- password: VARCHAR(255), Not Null
- phone: VARCHAR(20), Not Null
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

---

### Table: doctors
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100), Not Null
- email: VARCHAR(100), Unique, Not Null
- specialization: VARCHAR(100), Not Null
- phone: VARCHAR(20), Not Null
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

---

### Table: admin
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(100), Unique, Not Null
- password: VARCHAR(255), Not Null

---

### Table: appointments
- id: INT, Primary Key, Auto Increment
- patient_id: INT, Foreign Key → patients(id), Not Null
- doctor_id: INT, Foreign Key → doctors(id), Not Null
- appointment_time: DATETIME, Not Null
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

**Constraints & Notes:**
- A patient can have multiple appointments (1-to-many relationship).
- A doctor can have multiple appointments.
- Overlapping appointments for doctors should be prevented via application logic.
- If a patient is deleted, their appointments may either:
    - Be deleted (CASCADE), or
    - Be retained for history (SET NULL) depending on business rules.

---

### Table: clinic_locations (optional)
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100), Not Null
- address: VARCHAR(255), Not Null

---

### Table: payments (optional)
- id: INT, Primary Key, Auto Increment
- patient_id: INT, Foreign Key → patients(id)
- appointment_id: INT, Foreign Key → appointments(id)
- amount: DECIMAL(10,2), Not Null
- payment_status: VARCHAR(50)
- created_at: TIMESTAMP

---

## MongoDB Collection Design

MongoDB is used for flexible, semi-structured data such as prescriptions, notes, and metadata.

### Collection: prescriptions

```json
{
  "_id": "ObjectId('64abc123456')",
  "appointmentId": 101,
  "patientId": 1,
  "doctorId": 5,
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Every 6 hours"
    },
    {
      "name": "Ibuprofen",
      "dosage": "200mg",
      "frequency": "Twice daily"
    }
  ],
  "doctorNotes": "Patient should rest and stay hydrated.",
  "refillCount": 2,
  "createdAt": "2026-04-01T10:00:00Z",
  "pharmacy": {
    "name": "City Pharmacy",
    "location": "Main Street"
  },
  "tags": ["fever", "pain relief"]
}