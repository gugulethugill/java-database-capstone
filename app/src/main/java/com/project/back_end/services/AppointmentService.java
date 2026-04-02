package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TokenService tokenService;

    /**
     * Book a new appointment
     * @param appointment Appointment object to be saved
     * @return 1 if successful, 0 if failed
     */
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Update an existing appointment
     * @param appointment Appointment object to update
     * @return ResponseEntity with success/failure message
     */
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> existing = appointmentRepository.findById(appointment.getId());

        if (existing.isPresent()) {
            // Optionally validate appointment before updating
            appointmentRepository.save(appointment);
            response.put("message", "Appointment updated successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Appointment not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * Cancel an appointment by ID with token validation
     * @param id Appointment ID
     * @param token Authorization token
     * @return ResponseEntity with success/failure message
     */
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);

        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            // Validate token corresponds to patient who booked
            Long patientId = Long.valueOf(tokenService.extractIdentifier(token));
            if (!appointment.getPatient().getId().equals(patientId)) {
                response.put("message", "Unauthorized to cancel this appointment");
                return ResponseEntity.status(403).body(response);
            }
            appointmentRepository.delete(appointment);
            response.put("message", "Appointment cancelled successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Appointment not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * Retrieve appointments for a doctor on a specific date
     * @param pname Optional patient name filter
     * @param date Appointment date
     * @param token Doctor authorization token
     * @return Map containing list of appointments
     */
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();
        Long doctorId = Long.valueOf(tokenService.extractIdentifier(token));
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<Appointment> appointments;
        if (pname == null || pname.isEmpty()) {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        } else {
            appointments = appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                            doctorId, pname, start, end);
        }

        response.put("appointments", appointments);
        return response;
    }
// 1. **Add @Service Annotation**:
//    - To indicate that this class is a service layer class for handling business logic.
//    - The `@Service` annotation should be added before the class declaration to mark it as a Spring service component.
//    - Instruction: Add `@Service` above the class definition.

// 2. **Constructor Injection for Dependencies**:
//    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`.
//    - These dependencies should be injected through the constructor.
//    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.

// 3. **Add @Transactional Annotation for Methods that Modify Database**:
//    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
//    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.

// 4. **Book Appointment Method**:
//    - Responsible for saving the new appointment to the database.
//    - If the save operation fails, it returns `0`; otherwise, it returns `1`.
//    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.

// 5. **Update Appointment Method**:
//    - This method is used to update an existing appointment based on its ID.
//    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
//    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
//    - Instruction: Ensure proper validation and error handling is included for appointment updates.

// 6. **Cancel Appointment Method**:
//    - This method cancels an appointment by deleting it from the database.
//    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
//    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.

// 7. **Get Appointments Method**:
//    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
//    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
//    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.

// 8. **Change Status Method**:
//    - This method updates the status of an appointment by changing its value in the database.
//    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
//    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.


}
