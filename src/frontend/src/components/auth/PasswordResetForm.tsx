import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';

interface PasswordResetFormData {
  newPassword: string;
  confirmPassword: string;
}

const PasswordResetForm: React.FC = () => {
  const [searchParams] = useSearchParams();
  const [formData, setFormData] = useState<PasswordResetFormData>({
    newPassword: '',
    confirmPassword: '',
  });
  const [errors, setErrors] = useState<Partial<PasswordResetFormData & { token?: string }>>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);
  const [token, setToken] = useState<string | null>(null);

  useEffect(() => {
    const urlToken = searchParams.get('token');
    if (!urlToken) {
      setErrors({ token: 'Invalid or missing reset token' });
    } else {
      setToken(urlToken);
    }
  }, [searchParams]);

  const validateForm = (): boolean => {
    const newErrors: Partial<PasswordResetFormData> = {};
    
    if (!formData.newPassword) {
      newErrors.newPassword = 'Password is required';
    } else if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*?&]{8,}$/.test(formData.newPassword)) {
      newErrors.newPassword = 'Password must be at least 8 characters with letters and numbers';
    }
    
    if (!formData.confirmPassword) {
      newErrors.confirmPassword = 'Please confirm your password';
    } else if (formData.newPassword !== formData.confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm() || !token) return;

    setIsSubmitting(true);
    setErrors({});

    try {
      const response = await fetch('/api/auth/password-reset/confirm', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          token,
          newPassword: formData.newPassword,
        }),
      });

      if (response.ok) {
        setIsSuccess(true);
      } else {
        const errorData = await response.json();
        if (response.status === 400) {
          setErrors({ token: errorData.message || 'Invalid or expired reset token' });
        } else {
          setErrors({ newPassword: errorData.message || 'Password reset failed' });
        }
      }
    } catch (error) {
      setErrors({ newPassword: 'Password reset failed. Please try again.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (errors.token) {
    return (
      <div className="password-reset-error">
        <div className="error-container">
          <div className="error-icon">⚠️</div>
          <h2>Invalid Reset Link</h2>
          <p>{errors.token}</p>
          <div className="actions">
            <a href="/forgot-password" className="btn btn-primary">
              Request New Reset Link
            </a>
            <a href="/login" className="btn btn-secondary">
              Back to Login
            </a>
          </div>
        </div>
      </div>
    );
  }

  if (isSuccess) {
    return (
      <div className="password-reset-success">
        <div className="success-container">
          <div className="success-icon">✓</div>
          <h2>Password Reset Successful!</h2>
          <p>Your password has been successfully updated. You can now log in with your new password.</p>
          <div className="actions">
            <a href="/login" className="btn btn-primary">
              Go to Login
            </a>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="password-reset-form">
      <div className="reset-container">
        <h2>Set New Password</h2>
        <p>Please enter your new password below.</p>
        
        <form onSubmit={handleSubmit} className="reset-form">
          <div className="form-group">
            <label htmlFor="newPassword">New Password</label>
            <input
              type="password"
              id="newPassword"
              value={formData.newPassword}
              onChange={(e) => setFormData({...formData, newPassword: e.target.value})}
              placeholder="Enter your new password"
            />
            {errors.newPassword && <span className="error">{errors.newPassword}</span>}
            <div className="password-requirements">
              <small>Password must be at least 8 characters with letters and numbers</small>
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm New Password</label>
            <input
              type="password"
              id="confirmPassword"
              value={formData.confirmPassword}
              onChange={(e) => setFormData({...formData, confirmPassword: e.target.value})}
              placeholder="Confirm your new password"
            />
            {errors.confirmPassword && <span className="error">{errors.confirmPassword}</span>}
          </div>

          <button type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Updating Password...' : 'Update Password'}
          </button>

          <div className="form-links">
            <a href="/login">Back to Login</a>
          </div>
        </form>
      </div>
    </div>
  );
};

export default PasswordResetForm;
