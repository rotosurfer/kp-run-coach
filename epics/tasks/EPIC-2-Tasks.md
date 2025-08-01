# Epic 2: Training Plan Management - Implementation Tasks

## US-2.1: Create Individual Training Plan
### Backend Tasks (Spring Boot 3.1 + PostgreSQL)
- [ ] Create TrainingPlan entity with JPA annotations (@Entity, @Table, @OneToMany relationships)
- [ ] Design Workout entity with @ManyToOne relationship to TrainingPlan
- [ ] Implement TrainingPlanRepository with Spring Data JPA
- [ ] Create POST /api/training-plans endpoint in TrainingPlanController
- [ ] Build GET /api/training-plans/{id} and PUT /api/training-plans/{id} endpoints
- [ ] Implement TrainingPlanTemplate entity for reusable templates
- [ ] Create TrainingPlanService with validation logic using @Valid annotations
- [ ] Add plan versioning with @Version annotation for optimistic locking
- [ ] Implement plan status enum (DRAFT, PUBLISHED, ARCHIVED)
- [ ] Create TrainingPlanDTO with JsonView for different access levels

### Frontend Tasks (React + TypeScript)
- [ ] Create TrainingPlanForm.tsx with multi-step wizard component
- [ ] Build WeeklyScheduleDesigner.tsx with drag-and-drop functionality
- [ ] Implement PlanTemplateSelector.tsx with template grid view
- [ ] Create PlanPreview.tsx component with calendar visualization
- [ ] Build DraftPublishControls.tsx with status management
- [ ] Add WorkoutBuilder.tsx modal for individual workout creation
- [ ] Implement PlanValidation.tsx with real-time validation feedback
- [ ] Create PlanNavigation.tsx for multi-week plan navigation
- [ ] Add AutoSave functionality with debounced API calls

### Testing Tasks
- [ ] Unit tests for TrainingPlanService using JUnit 5 and Mockito
- [ ] Integration tests for TrainingPlanController with @WebMvcTest
- [ ] Repository tests for TrainingPlanRepository with @DataJpaTest
- [ ] Validation tests for plan business rules
- [ ] Frontend unit tests with Jest and React Testing Library
- [ ] E2E tests for plan creation flow using Playwright/Cypress

## US-2.2: Group Plan Creation
### Backend Tasks (Spring Boot + Batch Processing)
- [ ] Create GroupTrainingPlan entity with @OneToMany to individual plans
- [ ] Implement BulkAssignmentService with @Transactional batch operations
- [ ] Create PlanVariation entity for individual modifications
- [ ] Build POST /api/group-plans/{id}/assign endpoint with List<Long> runnerIds
- [ ] Implement @Async notification service for assignment emails
- [ ] Create GroupPlanRepository with custom queries for group management
- [ ] Add validation for maximum group size constraints
- [ ] Implement plan copying service with deep cloning logic
- [ ] Create assignment status tracking (PENDING, ASSIGNED, DECLINED)

### Frontend Tasks (React + TypeScript)
- [ ] Create RunnerSelectionGrid.tsx with checkbox selection
- [ ] Build GroupPlanDesigner.tsx extending individual plan form
- [ ] Implement VariationManager.tsx for runner-specific modifications
- [ ] Create BulkAssignmentModal.tsx with progress tracking
- [ ] Build AssignmentConfirmation.tsx with assignment summary
- [ ] Add RunnerGroupFilter.tsx for filtering available runners
- [ ] Implement AssignmentProgress.tsx with real-time status updates
- [ ] Create GroupPlanPreview.tsx showing all variations

### Testing Tasks
- [ ] Unit tests for BulkAssignmentService with batch operations
- [ ] Performance tests for large group assignments (100+ runners)
- [ ] Integration tests for variation handling
- [ ] Email notification delivery tests with mock SMTP
- [ ] Frontend tests for runner selection and bulk operations

## US-2.3: Workout Configuration
### Backend Tasks (Spring Boot + Validation)
- [ ] Create Workout entity with workout types enum (EASY_RUN, INTERVALS, TEMPO, etc.)
- [ ] Implement WorkoutType entity with configurable parameters
- [ ] Create Route entity with GPS coordinates and distance calculation
- [ ] Build WorkoutController with CRUD endpoints
- [ ] Implement pace calculation service with time/distance validation
- [ ] Create WorkoutValidationService with business rule checks
- [ ] Add coaching notes field with @Lob annotation for large text
- [ ] Implement workout intensity zones with enum validation
- [ ] Create workout duration calculation service

### Frontend Tasks (React + Maps Integration)
- [ ] Create WorkoutBuilderModal.tsx with tabbed interface
- [ ] Build PaceCalculator.tsx with time/distance inputs and live calculation
- [ ] Implement RouteMapEditor.tsx with Google Maps/Mapbox integration
- [ ] Create WorkoutTypeSelector.tsx with exercise type grid
- [ ] Build CoachingNotesEditor.tsx with rich text formatting
- [ ] Add IntensityZoneSelector.tsx with visual zone indicators
- [ ] Implement WorkoutPreview.tsx with summary display
- [ ] Create WorkoutTemplate.tsx for reusable workout patterns

### Testing Tasks
- [ ] Unit tests for pace calculation algorithms
- [ ] Integration tests for route storage and retrieval
- [ ] Validation tests for workout type constraints
- [ ] Map integration tests with mock map services
- [ ] E2E tests for complete workout creation flow

## US-2.4: Plan Calendar View
### Backend Tasks (Spring Boot + Caching)
- [ ] Create GET /api/training-plans/{id}/calendar endpoint with date range parameters
- [ ] Implement CalendarService with efficient date-range queries
- [ ] Add @Cacheable annotations for calendar data caching
- [ ] Create WorkoutDetailDTO with all necessary workout information
- [ ] Implement calendar export service (ICS format) using iCal4j library
- [ ] Build date-based workout lookup with indexed queries
- [ ] Add pagination support for large date ranges
- [ ] Create calendar summary statistics endpoint

### Frontend Tasks (React + Calendar Library)
- [ ] Build TrainingCalendar.tsx using react-big-calendar or similar
- [ ] Create WorkoutDetailPopover.tsx for quick workout info
- [ ] Implement CalendarNavigation.tsx with month/week/day views
- [ ] Build CalendarExport.tsx with format selection (PDF, ICS)
- [ ] Add WorkoutQuickEdit.tsx for inline workout modifications
- [ ] Create CalendarPrint.tsx with print-optimized layout
- [ ] Implement CalendarFilters.tsx for workout type filtering
- [ ] Build MonthView.tsx, WeekView.tsx, and DayView.tsx components

### Testing Tasks
- [ ] Unit tests for calendar data aggregation
- [ ] Performance tests for large date range queries
- [ ] Export format validation tests
- [ ] Frontend calendar rendering tests
- [ ] Date calculation and timezone handling tests

## US-2.5: Workout Status Updates
### Backend Tasks (Spring Boot + File Upload)
- [ ] Create WorkoutStatus entity with completion tracking
- [ ] Implement WorkoutLog entity for detailed completion data
- [ ] Build POST /api/workouts/{id}/status endpoint
- [ ] Create file upload service for workout evidence (photos, GPS files)
- [ ] Implement WorkoutMetrics entity for performance tracking
- [ ] Add WorkoutRating entity with 1-5 star system
- [ ] Create evidence storage service with file validation
- [ ] Implement completion percentage calculation service
- [ ] Add workout feedback collection with @Lob text fields

### Frontend Tasks (React + File Upload + Forms)
- [ ] Create WorkoutStatusForm.tsx with completion options
- [ ] Build EvidenceUpload.tsx with drag-and-drop file support
- [ ] Implement WorkoutRating.tsx with star rating component
- [ ] Create CompletionMetrics.tsx for time/distance/pace input
- [ ] Build WorkoutFeedback.tsx with text area and mood selector
- [ ] Add StatusHistory.tsx showing completion timeline
- [ ] Implement QuickStatusUpdate.tsx for mobile-friendly updates
- [ ] Create WorkoutEvidence.tsx gallery for uploaded files

### Testing Tasks
- [ ] Unit tests for status update logic and calculations
- [ ] Integration tests for file upload functionality
- [ ] Validation tests for metric data integrity
- [ ] Frontend tests for form validation and file upload
- [ ] E2E tests for complete workout completion flow
- [ ] Security tests for file upload vulnerabilities

## DevOps & Integration Tasks (Based on arch.md)
### Database Schema
- [ ] Create PostgreSQL migration scripts for training plan tables
- [ ] Set up foreign key relationships between plans, workouts, and users
- [ ] Add database indexes for performance optimization
- [ ] Implement database constraints for data integrity

### API Documentation
- [ ] Add Swagger/OpenAPI documentation for training plan endpoints
- [ ] Document request/response models with examples
- [ ] Create API versioning strategy for future updates

### Performance Optimization
- [ ] Implement database connection pooling for plan queries
- [ ] Add Redis caching for frequently accessed training plans
- [ ] Optimize SQL queries with proper indexing strategy
- [ ] Implement lazy loading for large workout collections

### Security & Authorization
- [ ] Add @PreAuthorize annotations for coach-only plan creation
- [ ] Implement plan ownership validation in service layer
- [ ] Add audit logging for plan modifications
- [ ] Secure file upload endpoints with proper validation
