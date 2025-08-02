import React, { useState } from 'react';

const PasswordReset: React.FC = () => {
  const [email, setEmail] = useState('');
  const [errors, setErrors] = useState<{ email?: string }>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitMessage, setSubmitMessage] = useState('');
  const [isSubmitted, setIsSubmitted] = useState(false);

  const validateEmail = (): boolean => {
    const newErrors: { email?: string } = {};
    if (!email) newErrors.email = 'Email is required';
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      newErrors.email = 'Invalid email format';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateEmail()) return;

    setIsSubmitting(true);
    setSubmitMessage('');
    setErrors({});

    try {
      const response = await fetch('/api/auth/password-reset', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email }),
      });

      if (response.ok) {
        const data = await response.json();
        setSubmitMessage(data.message || 'Password reset email sent if account exists');
        setIsSubmitted(true);
      } else {
        const errorData = await response.json();
        setErrors({ email: errorData.message || 'Failed to send reset email' });
      }
    } catch (error) {
      setErrors({ email: 'Failed to send reset email. Please try again.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isSubmitted) {
    return (
      <div className="password-reset-success">
        <div className="success-container">
          <div className="success-icon">📧</div>
          <h2>Check Your Email</h2>
          <p>{submitMessage}</p>
          <p>If an account with that email exists, you'll receive a password reset link shortly.</p>
          <div className="actions">
            <a href="/login" className="btn btn-primary">
              Back to Login
            </a>
            <button 
              onClick={() => {
                setIsSubmitted(false);
                setEmail('');
                setSubmitMessage('');
              }}
              className="btn btn-secondary"
            >
              Send Another Reset Email
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="password-reset">
      <div className="reset-container">
        <h2>Reset Your Password</h2>
        <p>Enter your email address and we'll send you a link to reset your password.</p>
        
        <form onSubmit={handleSubmit} className="reset-form">
          <div className="form-group">
            <label htmlFor="email">Email Address</label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Enter your email"
            />
            {errors.email && <span className="error">{errors.email}</span>}
          </div>

          <button type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Sending...' : 'Send Reset Link'}
          </button>

          <div className="form-links">
            <a href="/login">Back to Login</a>
            <a href="/register">Don't have an account? Register</a>
          </div>
        </form>
      </div>
    </div>
  );
};

export default PasswordReset;
