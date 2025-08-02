import { test, expect } from '@playwright/test';

test.describe('Coach Registration E2E', () => {
  test.beforeEach(async ({ page }) => {
    // Navigate to the registration page
    await page.goto('http://localhost:3000/register');
  });

  test('should register a new coach successfully', async ({ page }) => {
    // Fill out the registration form
    await page.fill('input[id="firstName"]', 'John');
    await page.fill('input[id="lastName"]', 'Doe');
    await page.fill('input[id="email"]', 'john.doe@example.com');
    await page.fill('input[id="password"]', 'password123');

    // Mock the API response
    await page.route('**/api/auth/register/coach', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          message: 'Registration successful. Please check your email to verify your account.',
          email: 'john.doe@example.com'
        })
      });
    });

    // Submit the form
    await page.click('button[type="submit"]');

    // Verify success message appears
    await expect(page.locator('.success-message')).toContainText('Registration successful');
    
    // Verify form is cleared
    await expect(page.locator('input[id="firstName"]')).toHaveValue('');
    await expect(page.locator('input[id="lastName"]')).toHaveValue('');
    await expect(page.locator('input[id="email"]')).toHaveValue('');
    await expect(page.locator('input[id="password"]')).toHaveValue('');
  });

  test('should show validation errors for empty fields', async ({ page }) => {
    // Try to submit empty form
    await page.click('button[type="submit"]');

    // Verify validation errors appear
    await expect(page.locator('text=First name is required')).toBeVisible();
    await expect(page.locator('text=Last name is required')).toBeVisible();
    await expect(page.locator('text=Email is required')).toBeVisible();
    await expect(page.locator('text=Password is required')).toBeVisible();
  });

  test('should show error for invalid email format', async ({ page }) => {
    await page.fill('input[id="firstName"]', 'John');
    await page.fill('input[id="lastName"]', 'Doe');
    await page.fill('input[id="email"]', 'invalid-email');
    await page.fill('input[id="password"]', 'password123');

    await page.click('button[type="submit"]');

    await expect(page.locator('text=Invalid email format')).toBeVisible();
  });

  test('should show error for weak password', async ({ page }) => {
    await page.fill('input[id="firstName"]', 'John');
    await page.fill('input[id="lastName"]', 'Doe');
    await page.fill('input[id="email"]', 'john.doe@example.com');
    await page.fill('input[id="password"]', 'weak');

    await page.click('button[type="submit"]');

    await expect(page.locator('text=Password must be at least 8 characters with letters and numbers')).toBeVisible();
  });

  test('should handle registration failure', async ({ page }) => {
    await page.fill('input[id="firstName"]', 'John');
    await page.fill('input[id="lastName"]', 'Doe');
    await page.fill('input[id="email"]', 'existing@example.com');
    await page.fill('input[id="password"]', 'password123');

    // Mock API error response
    await page.route('**/api/auth/register/coach', async route => {
      await route.fulfill({
        status: 400,
        contentType: 'application/json',
        body: JSON.stringify({
          message: 'Email already registered'
        })
      });
    });

    await page.click('button[type="submit"]');

    await expect(page.locator('text=Email already registered')).toBeVisible();
  });
});

test.describe('Login E2E', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:3000/login');
  });

  test('should login successfully with valid credentials', async ({ page }) => {
    await page.fill('input[id="email"]', 'john.doe@example.com');
    await page.fill('input[id="password"]', 'password123');

    // Mock successful login response
    await page.route('**/api/auth/login', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          token: 'jwt-token',
          email: 'john.doe@example.com',
          firstName: 'John',
          lastName: 'Doe',
          role: 'COACH'
        })
      });
    });

    await page.click('button[type="submit"]');

    await expect(page.locator('text=Login successful')).toBeVisible();
  });

  test('should show error for invalid credentials', async ({ page }) => {
    await page.fill('input[id="email"]', 'john.doe@example.com');
    await page.fill('input[id="password"]', 'wrongpassword');

    // Mock failed login response
    await page.route('**/api/auth/login', async route => {
      await route.fulfill({
        status: 401,
        contentType: 'application/json',
        body: JSON.stringify({
          message: 'Invalid credentials'
        })
      });
    });

    await page.click('button[type="submit"]');

    await expect(page.locator('text=Invalid credentials')).toBeVisible();
  });

  test('should validate required fields', async ({ page }) => {
    await page.click('button[type="submit"]');

    await expect(page.locator('text=Email is required')).toBeVisible();
    await expect(page.locator('text=Password is required')).toBeVisible();
  });
});

test.describe('Email Verification E2E', () => {
  test('should verify email successfully with valid token', async ({ page }) => {
    // Mock successful verification response
    await page.route('**/api/auth/verify-email?token=valid-token', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          message: 'Email verified successfully'
        })
      });
    });

    await page.goto('http://localhost:3000/verify-email?token=valid-token');

    await expect(page.locator('text=Email Verified!')).toBeVisible();
    await expect(page.locator('text=Email verified successfully')).toBeVisible();
    await expect(page.locator('a[href="/login"]')).toBeVisible();
  });

  test('should show error for invalid token', async ({ page }) => {
    // Mock failed verification response
    await page.route('**/api/auth/verify-email?token=invalid-token', async route => {
      await route.fulfill({
        status: 400,
        contentType: 'application/json',
        body: JSON.stringify({
          message: 'Invalid verification token'
        })
      });
    });

    await page.goto('http://localhost:3000/verify-email?token=invalid-token');

    await expect(page.locator('text=Verification Failed')).toBeVisible();
    await expect(page.locator('text=Invalid verification token')).toBeVisible();
  });
});
