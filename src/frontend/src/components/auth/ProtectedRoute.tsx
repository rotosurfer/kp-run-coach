import React, { ReactNode } from 'react';
import { useAuth } from '../../contexts/AuthContext';

interface ProtectedRouteProps {
  children: ReactNode;
  requiredRole?: string;
  fallback?: ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ 
  children, 
  requiredRole, 
  fallback 
}) => {
  const { isAuthenticated, user, isLoading } = useAuth();

  if (isLoading) {
    return <div className="loading">Loading...</div>;
  }

  if (!isAuthenticated) {
    return <>{fallback || <div className="unauthorized">Please log in to access this page.</div>}</>;
  }

  if (requiredRole && user?.role !== requiredRole) {
    return <div className="unauthorized">You don't have permission to access this page.</div>;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
