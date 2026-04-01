## Section 1: Architecture Summary

This Spring Boot application follows a layered architecture that combines both MVC and RESTful design patterns to support 
multiple types of clients. Traditional server-side rendered views are implemented using Thymeleaf templates for modules 
like the Admin and Doctor dashboards, while REST APIs are used for other components such as patient-facing features and 
external integrations. This hybrid approach allows the system to deliver both dynamic web pages and scalable API responses 
depending on the use case.

The application is structured around a clean separation of concerns. All incoming requests are handled by controllers, 
which delegate processing to a centralized service layer responsible for business logic and validation. The service layer 
communicates with repositories that abstract data access. The system uses a dual-database setup: MySQL for structured, 
relational data (such as users, appointments, and roles) and MongoDB for flexible, document-based data like prescriptions. 
Data retrieved from these databases is mapped into model classes and returned either as rendered HTML (via Thymeleaf) or 
JSON responses (via REST APIs).

---

## Section 2: Numbered Flow of Data and Control

1. Users interact with the system either through Thymeleaf-based web dashboards (e.g., AdminDashboard, DoctorDashboard)or via REST API clients such as mobile apps and frontend modules.

2. Incoming requests are routed to the appropriate controller based on the URL and HTTP method—either a Thymeleaf controller for HTML views or a REST controller for JSON responses.

3. The controller processes the request, performs initial validation if needed, and forwards the request to the service layer.

4. The service layer executes business logic, enforces rules (e.g., appointment scheduling constraints), and coordinates operations across different parts of the system.

5. The service layer communicates with the repository layer to fetch or persist data. This includes both JPA repositories (for MySQL) and MongoDB repositories.

6. The repositories interact directly with their respective databases—MySQL for structured relational data and MongoDB for flexible document-based data—and return the results.

7. Retrieved data is mapped into application models (JPA entities or MongoDB documents), which are then returned to the controller. The controller sends the final response back to the user either as a rendered HTML page (via Thymeleaf) or as a JSON response (via REST API).