# 🔹 EPIC 1: User Management

## Description.
As a coach, I want to manage my runners so I can assign and track plans.

## User Stories

### Coach Registration and Authentication
- [ ] As a coach, I can register and log in securely
  - Must support email/password authentication
  - Requires email verification
  - Password requirements: minimum 8 characters, mixed case, numbers

### Runner Registration
- [ ] As a runner, I can sign up with a unique invite or group code
  - Coach generates unique registration codes
  - Runners automatically associated with inviting coach
  - Email verification required

### Runner Management
- [ ] As a coach, I can view a list of all my registered runners
  - View basic profile information
  - Filter by active/inactive status
  - Search by name or email

### Role Management
- [ ] As a coach, I can assign roles (e.g., coach, runner, admin)
  - Define role permissions
  - Change roles as needed
  - Maintain audit log of role changes

### Profile Management
- [ ] As a runner, I can edit my profile
  - Update personal information:
    - Name
    - Age
    - Experience level
    - Goals
  - Upload profile picture
  - Set notification preferences
