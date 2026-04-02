// doctorCard.js

export function createDoctorCard(doctor) {

    const card = document.createElement("div");
    card.classList.add("doctor-card");

    const role = localStorage.getItem("userRole");

    // Doctor info
    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    const name = document.createElement("h3");
    name.textContent = doctor.name;

    const specialty = document.createElement("p");
    specialty.textContent = "Specialty: " + doctor.specialty;

    const email = document.createElement("p");
    email.textContent = "Email: " + doctor.email;

    const availability = document.createElement("p");
    availability.textContent = "Available: " + (doctor.availableTimes?.join(", ") || "N/A");

    infoDiv.appendChild(name);
    infoDiv.appendChild(specialty);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    // Actions
    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    // ADMIN → DELETE
    if (role === "admin") {
        const deleteBtn = document.createElement("button");
        deleteBtn.textContent = "Delete";

        deleteBtn.addEventListener("click", () => {
            if (confirm("Delete this doctor?")) {
                card.remove(); // (API call will come later)
            }
        });

        actionsDiv.appendChild(deleteBtn);
    }

    // PATIENT (NOT LOGGED)
    else if (role === "patient") {
        const bookBtn = document.createElement("button");
        bookBtn.textContent = "Book Now";

        bookBtn.addEventListener("click", () => {
            alert("Please login first.");
        });

        actionsDiv.appendChild(bookBtn);
    }

    // LOGGED PATIENT
    else if (role === "loggedPatient") {
        const bookBtn = document.createElement("button");
        bookBtn.textContent = "Book Now";

        bookBtn.addEventListener("click", () => {
            alert("Booking functionality coming soon");
        });

        actionsDiv.appendChild(bookBtn);
    }

    // Final assembly
    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    return card;
}
/*
Import the overlay function for booking appointments from loggedPatient.js

  Import the deleteDoctor API function to remove doctors (admin role) from docotrServices.js

  Import function to fetch patient details (used during booking) from patientServices.js

  Function to create and return a DOM element for a single doctor card
    Create the main container for the doctor card
    Retrieve the current user role from localStorage
    Create a div to hold doctor information
    Create and set the doctor’s name
    Create and set the doctor's specialization
    Create and set the doctor's email
    Create and list available appointment times
    Append all info elements to the doctor info container
    Create a container for card action buttons
    === ADMIN ROLE ACTIONS ===
      Create a delete button
      Add click handler for delete button
     Get the admin token from localStorage
        Call API to delete the doctor
        Show result and remove card if successful
      Add delete button to actions container
   
    === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
      Create a book now button
      Alert patient to log in before booking
      Add button to actions container
  
    === LOGGED-IN PATIENT ROLE ACTIONS === 
      Create a book now button
      Handle booking logic for logged-in patient   
        Redirect if token not available
        Fetch patient data with token
        Show booking overlay UI with doctor and patient info
      Add button to actions container
   
  Append doctor info and action buttons to the car
  Return the complete doctor card element
*/
