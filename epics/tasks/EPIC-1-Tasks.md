# Epic 1: User Management - Implementation Tasks

## US-1.1: Coach Registration
### Backend Tasks (Spring Boot 3.1 + PostgreSQL)
- [x] Complete User entity with JPA annotations (@Entity, @Table, @Column constraints)
- [x] Implement BCryptPasswordEncoder in SecurityConfig for password hashing
- [x] Create POST /api/auth/register/coach endpoint in AuthController
- [x] Implement EmailService with JavaMailSender for verification emails
- [x] Create POST /api/auth/login endpoint with JWT token generation using JwtUtils
- [x] Implement password reset with secure token generation and validation
- [x] Add account lockout mechanism (failedLoginAttempts, accountLocked fields)
- [x] Create email verification token validation endpoint
- [x] Add PostgreSQL database migration scripts (if using Flyway/Liquibase)

### Frontend Tasks (React + TypeScript)
- [x] Complete RegisterForm.tsx component with proper validation
- [x] Create LoginForm.tsx component with JWT token handling
- [x] Implement form validation using react-hook-form or custom validation
- [x] Create EmailVerification.tsx component for verification flow
- [x] Build PasswordReset.tsx and PasswordResetForm.tsx components
- [x] Add error handling with toast notifications or error displays
- [x] Create AuthContext for JWT token management and user state
- [x] Implement protected route wrapper component
- [x] Add loading states and form submission handling

### Testing Tasks
- [x] Unit tests for AuthService using JUnit 5 and Mockito
- [x] Integration tests for AuthController with @WebMvcTest
- [x] Repository tests for UserRepository with @DataJpaTest
- [x] Security tests for password hashing and JWT validation
- [x] Email service tests with mock SMTP server
- [x] Frontend unit tests with Jest and React Testing Library
- [x] E2E tests using Playwright or Cypress

## US-1.2: Runner Registration
### Backend Tasks (Spring Boot)
- [ ] Create InviteCode entity with JPA annotations
- [ ] Implement InviteCodeService for generation and validation
- [ ] Create InviteCodeRepository with Spring Data JPA
- [ ] Add coach-runner relationship mapping in User entity (@ManyToOne/@OneToMany)
- [ ] Create POST /api/auth/register/runner endpoint
- [ ] Implement invite code expiration logic with @Scheduled tasks
- [ ] Add welcome email template and service method
- [ ] Create GET /api/coaches/{id}/invite-codes endpoint for code management

### Frontend Tasks (React + TypeScript)
- [ ] Create RunnerRegistrationForm.tsx with invite code input
- [ ] Build InviteCodeInput.tsx reusable component
- [ ] Create WelcomeScreen.tsx for successful registration
- [ ] Add form validation for runner-specific fields
- [ ] Implement OnboardingFlow.tsx component with multi-step form
- [ ] Create InviteCodeManagement.tsx for coaches to generate codes
- [ ] Add error handling for invalid invite codes

### Testing Tasks
- [ ] Unit tests for InviteCodeService
- [ ] Integration tests for runner registration flow
- [ ] Tests for coach-runner association logic
- [ ] Email delivery verification tests
- [ ] Frontend tests for invite code validation

## US-1.3: Runner List Management
### Backend Tasks (Spring Boot + JPA)
- [ ] Create GET /api/coaches/{id}/runners endpoint with pagination
- [ ] Implement RunnerService with search functionality using JPA Criteria API
- [ ] Add filtering capabilities (status, registration date, etc.)
- [ ] Create RunnerDTO for data transfer with JsonView annotations
- [ ] Implement sorting with Pageable parameters
- [ ] Add runner count analytics endpoint
- [ ] Create RunnerStatus enum and status management logic
- [ ] Implement data export functionality (CSV/Excel)

### Frontend Tasks (React + TypeScript)
- [ ] Create RunnerListView.tsx with data table component
- [ ] Build SearchAndFilter.tsx component with form controls
- [ ] Create RunnerCard.tsx for individual runner display
- [ ] Implement pagination component with page navigation
- [ ] Add sorting functionality with column headers
- [ ] Create RunnerQuickView.tsx modal component
- [ ] Build DataExport.tsx component for CSV/Excel download

### Testing Tasks
- [ ] Unit tests for RunnerService search and filtering
- [ ] Performance tests for large datasets with JMeter
- [ ] Integration tests for pagination and sorting
- [ ] Frontend tests for search functionality and user interactions

## US-1.4: Role Assignment
### Backend Tasks (Spring Boot Security)
- [ ] Extend UserRole enum with additional roles (ADMIN, SUPER_COACH, etc.)
- [ ] Create RoleAssignmentService with permission checking logic
- [ ] Implement @PreAuthorize annotations on sensitive endpoints
- [ ] Create AuditLog entity for tracking role changes
- [ ] Build PUT /api/admin/users/{id}/role endpoint
- [ ] Implement role conflict resolution logic
- [ ] Add bulk role assignment endpoint for multiple users
- [ ] Create permission checking utility class

### Frontend Tasks (React + TypeScript + Role-Based UI)
- [ ] Create RoleManagement.tsx admin interface
- [ ] Build RoleAssignmentModal.tsx with dropdown selection
- [ ] Implement AuditLogViewer.tsx with data table
- [ ] Create PermissionMatrix.tsx visualization component
- [ ] Add BulkRoleAssignment.tsx for multiple user updates
- [ ] Implement role-based navigation and feature toggles
- [ ] Add confirmation dialogs for role changes

### Testing Tasks
- [ ] Security tests for role assignment permissions
- [ ] Unit tests for permission checking logic
- [ ] Integration tests for audit logging
- [ ] Tests for role conflict scenarios

## US-1.5: Profile Management
### Backend Tasks (Spring Boot + File Upload)
- [ ] Create ProfileUpdateRequest DTO with validation annotations
- [ ] Implement PUT /api/users/profile endpoint
- [ ] Add file upload service for profile images using MultipartFile
- [ ] Create image resizing and optimization service
- [ ] Implement NotificationPreferences entity and repository
- [ ] Build PrivacySettings entity with user preferences
- [ ] Add profile image storage (local filesystem or cloud storage)
- [ ] Create profile data validation service

### Frontend Tasks (React + File Upload)
- [ ] Create ProfileEditForm.tsx with form sections
- [ ] Build ImageUpload.tsx component with drag-and-drop
- [ ] Implement NotificationSettings.tsx with toggle switches
- [ ] Create PrivacyControls.tsx with granular privacy options
- [ ] Add form validation with real-time feedback
- [ ] Build ImageCropper.tsx for profile picture editing
- [ ] Implement ProfilePreview.tsx for changes preview

### Testing Tasks
- [ ] Unit tests for profile update validation
- [ ] Integration tests for file upload functionality
- [ ] Tests for image processing and resizing
- [ ] Frontend tests for form validation and file upload
- [ ] Security tests for file upload vulnerabilities

## DevOps & Deployment Tasks (Based on arch.md)
### Spring Boot Backend
- [ ] Configure Gradle build with proper JAR generation
- [ ] Set up application.properties for production environment
- [ ] Configure DATABASE_URL environment variable usage
- [ ] Implement JWT_SECRET environment variable configuration
- [ ] Set up SPRING_PROFILES_ACTIVE for prod profile
- [ ] Configure email service for production SMTP

### React Frontend
- [ ] Set up npm build pipeline for static assets
- [ ] Configure build output to /build directory
- [ ] Optimize bundle size and implement code splitting
- [ ] Set up environment variables for API endpoints
- [ ] Configure production build optimizations

### Database Setup
- [ ] Create PostgreSQL database migration scripts
- [ ] Set up connection pooling configuration
- [ ] Implement database health checks
- [ ] Configure backup and recovery procedures

### Monitoring & Logging
- [ ] Add Spring Boot Actuator for health monitoring
- [ ] Implement structured logging with JSON format
- [ ] Set up application metrics collection
- [ ] Configure error tracking and alerting
