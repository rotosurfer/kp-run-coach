# Epic 1: User Management - User Stories

## US-1.1: Coach Registration
**As a** coach,  
**I want to** register and log in securely  
**So that** I can access the platform and manage my runners

### Acceptance Criteria
- [ ] Registration form with email and password fields
- [ ] Password meets security requirements (8+ chars, mixed case, numbers)
- [ ] Email verification process
- [ ] Login with registered credentials
- [ ] Password recovery option
- [ ] Account lockout after failed attempts

## US-1.2: Runner Registration
**As a** runner,  
**I want to** sign up using a coach's invite code  
**So that** I can join my coach's training group

### Acceptance Criteria
- [ ] Registration form with invite code field
- [ ] Email and password fields
- [ ] Automatic coach association
- [ ] Email verification required
- [ ] Invalid code error handling
- [ ] Welcome email with getting started guide

## US-1.3: Runner List Management
**As a** coach,  
**I want to** view and manage my registered runners  
**So that** I can keep track of who I'm training

### Acceptance Criteria
- [ ] List view of all runners
- [ ] Search functionality
- [ ] Filter by status (active/inactive)
- [ ] Sort by name, join date, status
- [ ] Quick access to runner profiles
- [ ] Runner count summary

## US-1.4: Role Assignment
**As a** coach,  
**I want to** assign and manage user roles  
**So that** I can control access levels and permissions

### Acceptance Criteria
- [ ] Role assignment interface
- [ ] Available roles: coach, runner, admin
- [ ] Role modification capability
- [ ] Audit log of role changes
- [ ] Permission set for each role
- [ ] Role conflict prevention

## US-1.5: Profile Management
**As a** runner,  
**I want to** manage my profile information  
**So that** my coach has accurate information about me

### Acceptance Criteria
- [ ] Edit personal details:
  - [ ] Name
  - [ ] Age
  - [ ] Experience level
  - [ ] Goals
- [ ] Profile picture upload
- [ ] Notification preferences
- [ ] Privacy settings
- [ ] Save/cancel changes
- [ ] Form validation

## US-1.6: Coach Profile Management
**As a** coach,  
**I want to** maintain my professional profile  
**So that** runners can learn about my expertise

### Acceptance Criteria
- [ ] Edit coach profile:
  - [ ] Professional bio
  - [ ] Certifications
  - [ ] Specialties
  - [ ] Years of experience
- [ ] Profile visibility settings
- [ ] Contact information
- [ ] Profile preview
- [ ] Social media links
