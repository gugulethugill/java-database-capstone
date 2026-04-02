package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {
    private final DoctorService doctorService;
    private final Service service;

    @Autowired
    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    /**
     * Get doctor's available slots for a specific date.
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {

        // Validate token for given user type
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, user);
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity(tokenValidation.getBody(), HttpStatus.UNAUTHORIZED);
        }

        LocalDate availabilityDate = LocalDate.parse(date);
        List<String> availableSlots = doctorService.getDoctorAvailability(doctorId, String.valueOf(availabilityDate));

        Map<String, Object> response = new HashMap<>();
        response.put("doctorId", doctorId);
        response.put("date", availabilityDate);
        response.put("availableSlots", availableSlots);

        return ResponseEntity.ok(response);
    }

    /**
     * Get a list of all doctors.
     */
    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        return ResponseEntity.ok(doctors);
    }

    /**
     * Add a new doctor.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> addDoctor(
            @PathVariable String token,
            @RequestBody Doctor doctor) {

        // Only admins can add doctors
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity(tokenValidation.getBody(), HttpStatus.UNAUTHORIZED);
        }

        int result = doctorService.saveDoctor(doctor);
        Map<String, String> response = new HashMap<>();

        if (result == 1) {
            response.put("message", "Doctor added to db");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else if (result == -1) {
            response.put("error", "Doctor already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            response.put("error", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Doctor login endpoint.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    /**
     * Update doctor details.
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @PathVariable String token,
            @RequestBody Doctor doctor) {

        // Only admins can update doctor details
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity(tokenValidation.getBody(), HttpStatus.UNAUTHORIZED);
        }

        int result = doctorService.updateDoctor(doctor);
        Map<String, String> response = new HashMap<>();

        if (result == 1) {
            response.put("message", "Doctor updated");
            return ResponseEntity.ok(response);
        } else if (result == -1) {
            response.put("error", "Doctor not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            response.put("error", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete a doctor by ID.
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token) {

        // Only admins can delete doctors
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity(tokenValidation.getBody(), HttpStatus.UNAUTHORIZED);
        }

        int result = doctorService.deleteDoctor(id);
        Map<String, String> response = new HashMap<>();

        if (result == 1) {
            response.put("message", "Doctor deleted successfully");
            return ResponseEntity.ok(response);
        } else if (result == -1) {
            response.put("error", "Doctor not found with id");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            response.put("error", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Filter doctors by name, specialty, and available time.
     */
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filterDoctors(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        Map<String, Object> filteredDoctors = service.filterDoctor(name, speciality, time);
        return ResponseEntity.ok(filteredDoctors);
    }

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST controller that serves JSON responses.
//    - Use `@RequestMapping("${api.path}doctor")` to prefix all endpoints with a configurable API path followed by "doctor".
//    - This class manages doctor-related functionalities such as registration, login, updates, and availability.


// 2. Autowire Dependencies:
//    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
//    - Inject the shared `Service` class for general-purpose features like token validation and filtering.


// 3. Define the `getDoctorAvailability` Method:
//    - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
//    - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
//    - First validates the token against the user type.
//    - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.


// 4. Define the `getDoctor` Method:
//    - Handles HTTP GET requests to retrieve a list of all doctors.
//    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.


// 5. Define the `saveDoctor` Method:
//    - Handles HTTP POST requests to register a new doctor.
//    - Accepts a validated `Doctor` object in the request body and a token for authorization.
//    - Validates the token for the `"admin"` role before proceeding.
//    - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.


// 6. Define the `doctorLogin` Method:
//    - Handles HTTP POST requests for doctor login.
//    - Accepts a validated `Login` DTO containing credentials.
//    - Delegates authentication to the `DoctorService` and returns login status and token information.


// 7. Define the `updateDoctor` Method:
//    - Handles HTTP PUT requests to update an existing doctor's information.
//    - Accepts a validated `Doctor` object and a token for authorization.
//    - Token must belong to an `"admin"`.
//    - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.


// 8. Define the `deleteDoctor` Method:
//    - Handles HTTP DELETE requests to remove a doctor by ID.
//    - Requires both doctor ID and an admin token as path variables.
//    - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.


// 9. Define the `filter` Method:
//    - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
//    - Accepts `name`, `time`, and `speciality` as path variables.
//    - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.


}
