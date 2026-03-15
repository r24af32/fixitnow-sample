# Milestone-2 Task Change Log

Date: 2026-03-10

This file records the task changes integrated into the milestone-2 project.

## Files Updated

1. backend/src/main/java/com/fixitnow/backend/config/SecurityConfig.java

- Added public access rule for `/error` endpoint.
- Change: `.requestMatchers("/error").permitAll()`.
- Purpose: Avoid security blocking of framework error route and prevent unwanted 403 behavior during auth/controller exceptions.

2. backend/src/main/java/com/fixitnow/backend/controller/AuthController.java

- Added `HttpStatus` import.
- Register flow:
  - Added safe role parsing with `try/catch`.
  - Returns `400 Bad Request` with `Invalid role` for invalid role input.
- Login flow:
  - Changed return type to `ResponseEntity<?>`.
  - Added null checks for email/password and returns `400` when missing.
  - Returns `401` for unknown user and invalid password.
  - Returns `400` if provider profile is missing.
  - Returns `403` when provider is not approved.
  - Returns `200 OK` with `AuthResponse` on success.
- Purpose: Replace RuntimeException-based flow with explicit API status responses for cleaner frontend integration.

3. backend/src/main/java/com/fixitnow/backend/security/JwtAuthenticationFilter.java

- Removed duplicate import of `SimpleGrantedAuthority`.
- Added early bypass for `/api/auth/` endpoints.
- Replaced hard `401` on token parse failure with `SecurityContextHolder.clearContext()` and continued filter chain.
- Purpose: Keep auth endpoints truly public even if stale/invalid Authorization header is sent, reducing integration failures.

## Validation

- Ran Maven validation command:
  - `backend\\mvnw.cmd -q validate`
- Result: Success (exit code 0)

## Notes

- Only the above three backend files were modified for this task integration.
- Existing project structure and other modules remain unchanged.

---

# Milestone-2 Booking System + Status Workflow (Task Completion)

Date: 2026-03-15

This section records the complete implementation of the Booking System workflow for FixItNow backend, including security, DTO cleanup, status normalization, and global error handling.

## Scope Completed

1. Booking entity migrated to relationship-based model with required fields:

- id
- customer (ManyToOne -> User)
- provider (ManyToOne -> User)
- service (ManyToOne -> ServiceEntity)
- timeSlot
- status
- createdAt

2. Booking workflow APIs implemented on `/api/bookings`:

- `POST /api/bookings` (CUSTOMER creates -> `PENDING`)
- `GET /api/bookings/customer` (customer bookings)
- `PUT /api/bookings/{id}/cancel` (customer cancels)
- `GET /api/bookings/provider` (provider bookings)
- `PUT /api/bookings/{id}/accept` (provider -> `CONFIRMED`)
- `PUT /api/bookings/{id}/reject` (provider -> `REJECTED`)
- `PUT /api/bookings/{id}/complete` (provider -> `COMPLETED`)

3. Principal-based authentication handling used for booking operations:

- Logged-in user resolved via JWT `Principal#getName()` (email)
- Role checks enforced at service layer (`CUSTOMER`/`PROVIDER`)

4. Booking status consistency improvements:

- Automatic uppercase status normalization on persist/update
- Startup migration normalizer to convert legacy lowercase statuses in DB

5. Response and security improvements:

- Booking responses converted to clean DTO output (no deep nested graph)
- User password hidden from JSON serialization with `@JsonIgnore`

6. Error handling improvements:

- Added global exception handler with consistent JSON error body:
  - timestamp
  - status
  - error
  - message
  - path

## Files Added

1. `backend/src/main/java/com/fixitnow/backend/dto/BookingCreateRequest.java`

- Request DTO for booking create API (`serviceId`, `timeSlot`).

2. `backend/src/main/java/com/fixitnow/backend/dto/BookingResponse.java`

- Response DTO for all booking APIs.
- Includes compact booking/customer/provider/service summary fields.

3. `backend/src/main/java/com/fixitnow/backend/config/BookingStatusNormalizer.java`

- `CommandLineRunner` that normalizes existing booking statuses in DB to uppercase at startup.

4. `backend/src/main/java/com/fixitnow/backend/dto/ApiErrorResponse.java`

- Standard API error payload model.

5. `backend/src/main/java/com/fixitnow/backend/config/GlobalExceptionHandler.java`

- Global exception mapping to uniform API error responses.

## Files Updated

1. `backend/src/main/java/com/fixitnow/backend/entity/Booking.java`

- Replaced old ID-only booking model with entity relationships.
- Added `createdAt` field and lifecycle hooks (`@PrePersist`, `@PreUpdate`).
- Added status auto-uppercase normalization on save/update.
- Fixed accidental in-file corruption on the `customer` field line.

2. `backend/src/main/java/com/fixitnow/backend/repository/BookingRepository.java`

- Added provider/time-slot conflict check based on active statuses.
- Updated query methods for relationship-based model.

3. `backend/src/main/java/com/fixitnow/backend/service/BookingService.java`

- Reworked logic for role-based booking create/read/update workflow.
- Added transition guards (`PENDING` -> `CONFIRMED/REJECTED`, `CONFIRMED` -> `COMPLETED`).
- Added ownership checks for customer/provider operations.
- Changed outputs to `BookingResponse` DTOs.

4. `backend/src/main/java/com/fixitnow/backend/controller/BookingController.java`

- Replaced legacy routes with required REST routes.
- Switched to Principal-based operations.
- Switched responses to `BookingResponse`.

5. `backend/src/main/java/com/fixitnow/backend/entity/User.java`

- Added `@JsonIgnore` on `password` field to prevent password hash exposure in API responses.

## Files Removed

- None.

## Behavior Changes Summary

1. Booking create no longer accepts full booking entity payload from client.

- Now strictly accepts `serviceId` and `timeSlot`.

2. Booking APIs now return DTO-based compact responses.

3. Legacy inconsistent statuses (e.g., `pending`, `confirmed`) are normalized to uppercase.

4. Runtime/validation errors now return consistent structured JSON via global handler.

## Validation Performed

1. Repeated compile checks:

- `backend\\mvnw.cmd -DskipTests compile`
- Result: Success

2. Runtime startup validation:

- Verified app startup on available ports (`8080` / `8081` depending on conflict)
- Confirmed booking normalizer executes at startup and updates old rows.

3. API flow validation (manual):

- Auth login token retrieval
- Customer booking creation
- Customer/provider booking retrieval
- Status transition operations
