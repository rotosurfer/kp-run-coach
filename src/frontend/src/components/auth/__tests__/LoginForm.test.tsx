import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import LoginForm from '../LoginForm';

// Mock fetch globally
global.fetch = jest.fn();

describe('LoginForm', () => {
  const mockOnLoginSuccess = jest.fn();

  beforeEach(() => {
    (fetch as jest.Mock).mockClear();
    mockOnLoginSuccess.mockClear();
  });

  test('renders login form with all fields', () => {
    render(<LoginForm onLoginSuccess={mockOnLoginSuccess} />);

    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument();
    expect(screen.getByText(/forgot your password?/i)).toBeInTheDocument();
  });

  test('validates required fields', async () => {
    render(<LoginForm onLoginSuccess={mockOnLoginSuccess} />);

    const submitButton = screen.getByRole('button', { name: /login/i });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/email is required/i)).toBeInTheDocument();
      expect(screen.getByText(/password is required/i)).toBeInTheDocument();
    });
  });

  test('validates email format', async () => {
    render(<LoginForm onLoginSuccess={mockOnLoginSuccess} />);

    const emailInput = screen.getByLabelText(/email/i);
    const passwordInput = screen.getByLabelText(/password/i);
    const submitButton = screen.getByRole('button', { name: /login/i });

    fireEvent.change(emailInput, { target: { value: 'invalid-email' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/invalid email format/i)).toBeInTheDocument();
    });
  });

  test('submits form with valid credentials', async () => {
    const mockResponse = {
      token: 'jwt-token',
      email: 'john.doe@example.com',
      firstName: 'John',
      lastName: 'Doe',
      role: 'COACH'
    };

    (fetch as jest.Mock).mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse,
    });

    // Mock localStorage
    const setItemSpy = jest.spyOn(Storage.prototype, 'setItem');

    render(<LoginForm onLoginSuccess={mockOnLoginSuccess} />);

    fireEvent.change(screen.getByLabelText(/email/i), { 
      target: { value: 'john.doe@example.com' } 
    });
    fireEvent.change(screen.getByLabelText(/password/i), { 
      target: { value: 'password123' } 
    });

    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => {
      expect(fetch).toHaveBeenCalledWith('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: 'john.doe@example.com',
          password: 'password123',
        }),
      });
    });

    await waitFor(() => {
      expect(setItemSpy).toHaveBeenCalledWith('token', 'jwt-token');
      expect(setItemSpy).toHaveBeenCalledWith('user', JSON.stringify({
        email: 'john.doe@example.com',
        firstName: 'John',
        lastName: 'Doe',
        role: 'COACH'
      }));
      expect(mockOnLoginSuccess).toHaveBeenCalledWith('jwt-token', mockResponse);
      expect(screen.getByText(/login successful!/i)).toBeInTheDocument();
    });

    setItemSpy.mockRestore();
  });

  test('displays error message on failed login', async () => {
    (fetch as jest.Mock).mockResolvedValueOnce({
      ok: false,
      status: 401,
      json: async () => ({ message: 'Invalid credentials' }),
    });

    render(<LoginForm onLoginSuccess={mockOnLoginSuccess} />);

    fireEvent.change(screen.getByLabelText(/email/i), { 
      target: { value: 'john.doe@example.com' } 
    });
    fireEvent.change(screen.getByLabelText(/password/i), { 
      target: { value: 'wrongpassword' } 
    });

    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => {
      expect(screen.getByText(/invalid credentials/i)).toBeInTheDocument();
    });

    expect(mockOnLoginSuccess).not.toHaveBeenCalled();
  });

  test('shows loading state during submission', async () => {
    (fetch as jest.Mock).mockImplementation(
      () => new Promise(resolve => setTimeout(() => resolve({ 
        ok: true, 
        json: () => ({}) 
      }), 100))
    );

    render(<LoginForm onLoginSuccess={mockOnLoginSuccess} />);

    fireEvent.change(screen.getByLabelText(/email/i), { 
      target: { value: 'john.doe@example.com' } 
    });
    fireEvent.change(screen.getByLabelText(/password/i), { 
      target: { value: 'password123' } 
    });

    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    expect(screen.getByRole('button', { name: /logging in.../i })).toBeInTheDocument();
    expect(screen.getByRole('button')).toBeDisabled();
  });

  test('handles network error gracefully', async () => {
    (fetch as jest.Mock).mockRejectedValueOnce(new Error('Network error'));

    render(<LoginForm onLoginSuccess={mockOnLoginSuccess} />);

    fireEvent.change(screen.getByLabelText(/email/i), { 
      target: { value: 'john.doe@example.com' } 
    });
    fireEvent.change(screen.getByLabelText(/password/i), { 
      target: { value: 'password123' } 
    });

    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => {
      expect(screen.getByText(/login failed. please try again/i)).toBeInTheDocument();
    });

    expect(mockOnLoginSuccess).not.toHaveBeenCalled();
  });
});
