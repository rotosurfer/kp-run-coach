import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { AuthProvider, useAuth } from '../AuthContext';

// Test component that uses the AuthContext
const TestComponent: React.FC = () => {
  const { user, token, isAuthenticated, isLoading, login, logout, updateUser } = useAuth();

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      <div data-testid="auth-status">
        {isAuthenticated ? 'Authenticated' : 'Not Authenticated'}
      </div>
      {user && (
        <div>
          <div data-testid="user-email">{user.email}</div>
          <div data-testid="user-name">{user.firstName} {user.lastName}</div>
          <div data-testid="user-role">{user.role}</div>
        </div>
      )}
      {token && <div data-testid="token">{token}</div>}
      
      <button 
        onClick={() => login('test-token', {
          email: 'test@example.com',
          firstName: 'John',
          lastName: 'Doe',
          role: 'COACH'
        })}
        data-testid="login-btn"
      >
        Login
      </button>
      
      <button onClick={logout} data-testid="logout-btn">
        Logout
      </button>
      
      <button 
        onClick={() => updateUser({ firstName: 'Jane' })}
        data-testid="update-btn"
      >
        Update User
      </button>
    </div>
  );
};

// Mock localStorage
const localStorageMock = (() => {
  let store: Record<string, string> = {};
  return {
    getItem: jest.fn((key: string) => store[key] || null),
    setItem: jest.fn((key: string, value: string) => {
      store[key] = value.toString();
    }),
    removeItem: jest.fn((key: string) => {
      delete store[key];
    }),
    clear: jest.fn(() => {
      store = {};
    }),
  };
})();

Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
});

describe('AuthContext', () => {
  beforeEach(() => {
    localStorageMock.clear();
    jest.clearAllMocks();
  });

  const renderWithAuthProvider = (component: React.ReactElement) => {
    return render(
      <AuthProvider>
        {component}
      </AuthProvider>
    );
  };

  test('should initialize with no authentication', async () => {
    renderWithAuthProvider(<TestComponent />);

    await waitFor(() => {
      expect(screen.getByTestId('auth-status')).toHaveTextContent('Not Authenticated');
    });

    expect(screen.queryByTestId('user-email')).not.toBeInTheDocument();
    expect(screen.queryByTestId('token')).not.toBeInTheDocument();
  });

  test('should handle login correctly', async () => {
    renderWithAuthProvider(<TestComponent />);

    await waitFor(() => {
      expect(screen.getByTestId('auth-status')).toHaveTextContent('Not Authenticated');
    });

    fireEvent.click(screen.getByTestId('login-btn'));

    await waitFor(() => {
      expect(screen.getByTestId('auth-status')).toHaveTextContent('Authenticated');
      expect(screen.getByTestId('user-email')).toHaveTextContent('test@example.com');
      expect(screen.getByTestId('user-name')).toHaveTextContent('John Doe');
      expect(screen.getByTestId('user-role')).toHaveTextContent('COACH');
      expect(screen.getByTestId('token')).toHaveTextContent('test-token');
    });

    // Check localStorage was updated
    expect(localStorageMock.setItem).toHaveBeenCalledWith('token', 'test-token');
    expect(localStorageMock.setItem).toHaveBeenCalledWith('user', JSON.stringify({
      email: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
      role: 'COACH'
    }));
  });

  test('should handle logout correctly', async () => {
    renderWithAuthProvider(<TestComponent />);

    // First login
    fireEvent.click(screen.getByTestId('login-btn'));
    await waitFor(() => {
      expect(screen.getByTestId('auth-status')).toHaveTextContent('Authenticated');
    });

    // Then logout
    fireEvent.click(screen.getByTestId('logout-btn'));

    await waitFor(() => {
      expect(screen.getByTestId('auth-status')).toHaveTextContent('Not Authenticated');
    });

    expect(screen.queryByTestId('user-email')).not.toBeInTheDocument();
    expect(screen.queryByTestId('token')).not.toBeInTheDocument();

    // Check localStorage was cleared
    expect(localStorageMock.removeItem).toHaveBeenCalledWith('token');
    expect(localStorageMock.removeItem).toHaveBeenCalledWith('user');
  });

  test('should handle user update correctly', async () => {
    renderWithAuthProvider(<TestComponent />);

    // First login
    fireEvent.click(screen.getByTestId('login-btn'));
    await waitFor(() => {
      expect(screen.getByTestId('user-name')).toHaveTextContent('John Doe');
    });

    // Update user
    fireEvent.click(screen.getByTestId('update-btn'));

    await waitFor(() => {
      expect(screen.getByTestId('user-name')).toHaveTextContent('Jane Doe');
    });

    // Check localStorage was updated
    expect(localStorageMock.setItem).toHaveBeenCalledWith('user', JSON.stringify({
      email: 'test@example.com',
      firstName: 'Jane',
      lastName: 'Doe',
      role: 'COACH'
    }));
  });

  test('should restore authentication from localStorage on initialization', async () => {
    // Set up localStorage with existing auth data
    localStorageMock.setItem('token', 'stored-token');
    localStorageMock.setItem('user', JSON.stringify({
      email: 'stored@example.com',
      firstName: 'Stored',
      lastName: 'User',
      role: 'COACH'
    }));

    renderWithAuthProvider(<TestComponent />);

    await waitFor(() => {
      expect(screen.getByTestId('auth-status')).toHaveTextContent('Authenticated');
      expect(screen.getByTestId('user-email')).toHaveTextContent('stored@example.com');
      expect(screen.getByTestId('user-name')).toHaveTextContent('Stored User');
      expect(screen.getByTestId('token')).toHaveTextContent('stored-token');
    });
  });

  test('should handle corrupted localStorage data gracefully', async () => {
    // Set up corrupted localStorage data
    localStorageMock.setItem('token', 'valid-token');
    localStorageMock.setItem('user', 'invalid-json');

    renderWithAuthProvider(<TestComponent />);

    await waitFor(() => {
      expect(screen.getByTestId('auth-status')).toHaveTextContent('Not Authenticated');
    });

    // Should clear corrupted data
    expect(localStorageMock.removeItem).toHaveBeenCalledWith('token');
    expect(localStorageMock.removeItem).toHaveBeenCalledWith('user');
  });

  test('should throw error when useAuth is used outside AuthProvider', () => {
    // Spy on console.error to prevent error from appearing in test output
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

    expect(() => {
      render(<TestComponent />);
    }).toThrow('useAuth must be used within an AuthProvider');

    consoleErrorSpy.mockRestore();
  });
});
