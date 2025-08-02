import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';

const EmailVerification: React.FC = () => {
  const [searchParams] = useSearchParams();
  const [verificationStatus, setVerificationStatus] = useState<'loading' | 'success' | 'error'>('loading');
  const [message, setMessage] = useState('');

  useEffect(() => {
    const token = searchParams.get('token');
    
    if (!token) {
      setVerificationStatus('error');
      setMessage('Invalid verification link');
      return;
    }

    verifyEmail(token);
  }, [searchParams]);

  const verifyEmail = async (token: string) => {
    try {
      const response = await fetch(`/api/auth/verify-email?token=${token}`, {
        method: 'GET',
      });

      if (response.ok) {
        const data = await response.json();
        setVerificationStatus('success');
        setMessage(data.message || 'Email verified successfully!');
      } else {
        const errorData = await response.json();
        setVerificationStatus('error');
        setMessage(errorData.message || 'Email verification failed');
      }
    } catch (error) {
      setVerificationStatus('error');
      setMessage('Email verification failed. Please try again.');
    }
  };

  const renderContent = () => {
    switch (verificationStatus) {
      case 'loading':
        return (
          <div className="verification-loading">
            <div className="spinner"></div>
            <p>Verifying your email...</p>
          </div>
        );
      case 'success':
        return (
          <div className="verification-success">
            <div className="success-icon">✓</div>
            <h2>Email Verified!</h2>
            <p>{message}</p>
            <div className="actions">
              <a href="/login" className="btn btn-primary">
                Go to Login
              </a>
            </div>
          </div>
        );
      case 'error':
        return (
          <div className="verification-error">
            <div className="error-icon">✗</div>
            <h2>Verification Failed</h2>
            <p>{message}</p>
            <div className="actions">
              <a href="/register" className="btn btn-secondary">
                Register Again
              </a>
              <a href="/login" className="btn btn-primary">
                Try Login
              </a>
            </div>
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="email-verification">
      <div className="verification-container">
        {renderContent()}
      </div>
    </div>
  );
};

export default EmailVerification;
