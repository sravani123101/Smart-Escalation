# Smart Escalation - Spring Boot REST Backend

This companion Spring Boot service provides REST APIs for the Smart Escalation medicine reminder and trusted contact notification system.

## Features
- Complete RESTful endpoints for medicines, reminders, dose confirmations, trusted contacts, and automated escalation evaluations.
- Jakarta Bean Validation on input payloads.
- Clean tiered architecture: Controllers, Services, Repositories, DTOs, and Models.
- Ready for local testing without cloud database credentials or MongoDB installation.

---

## Running the Backend

### Prerequisites
- JDK 17 or higher
- Maven 3.8+

### Commands
```bash
cd backend
mvn clean package
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.
When accessed from the Android Emulator, use `http://10.0.2.2:8080/api/`.

---

## REST API Endpoints

### 1. Medicines
- `GET /api/medicines` - List all scheduled medicines.
- `GET /api/medicines/{id}` - Get medicine by ID.
- `POST /api/medicines` - Create a medicine (with reminder times, dosage, instructions, stock).
- `PUT /api/medicines/{id}` - Update medicine details.
- `DELETE /api/medicines/{id}` - Delete medicine and cancel associated schedules.

### 2. Reminders & Dose Confirmation
- `GET /api/reminders` - List scheduled and pending reminders.
- `POST /api/reminders` - Schedule a new dose reminder.
- `POST /api/reminders/{id}/confirm` - Confirm a dose (accepts optional photo reference `photoPath` and `withPhoto: true`). Decrements inventory stock.
- `POST /api/reminders/{id}/skip` - Skip dose with optional reason (e.g. "Doctor advised pause").
- `GET /api/reminders/history` - Comprehensive adherence history and status logs.

### 3. Trusted Contacts
- `GET /api/contacts` - List trusted contacts ordered by escalation priority.
- `POST /api/contacts` - Add a caregiver or emergency contact.
- `PUT /api/contacts/{id}` - Update contact information or toggle escalation status.
- `DELETE /api/contacts/{id}` - Remove contact from network.

### 4. Safety Escalation Engine
- `GET /api/escalations` - View all historical escalation incidents.
- `POST /api/escalations/check` - Evaluate deadlines across all pending doses. Unconfirmed reminders past their safety deadline are flagged as `UNCONFIRMED`, duplicate alerts are prevented, and escalation events with formatted emergency messages are created.

---

## Future MongoDB Atlas Integration Plan (Phase 3)

The architecture is prepared for MongoDB Atlas integration without rewriting controller or service layers:

1. **Add Dependency** to `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-mongodb</artifactId>
   </dependency>
   ```

2. **Add Atlas Connection String** in `application.properties`:
   ```properties
   spring.data.mongodb.uri=mongodb+srv://<username>:<password>@cluster0.mongodb.net/smart_escalation?retryWrites=true&w=majority
   ```

3. **Annotate Models**:
   Add `@Document(collection = "medicines")` to `Medicine.java` and `@Id` to `id`.

4. **Swap Repositories**:
   Change `MedicineRepository` interface to extend `MongoRepository<Medicine, String>`. Remove `InMemoryMedicineRepository`.
