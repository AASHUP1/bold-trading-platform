import React, { createContext, useContext, useState, useEffect } from 'react';
import type { AdminUser, AdminRole } from '../types';

interface AuthContextType {
  user: AdminUser | null;
  role: AdminRole | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  login: (email: string, pass: string) => Promise<boolean>;
  logout: () => void;
  switchRole: (role: AdminRole) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const DEMO_ADMIN: AdminUser = {
  id: 'a0000000-0000-0000-0000-000000000001',
  email: 'admin@bold.app',
  fullName: 'Super Administrator',
  role: 'super_admin',
  isActive: true,
  lastLoginAt: new Date().toISOString(),
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AdminUser | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // Check local stored session
    try {
      const stored = localStorage.getItem('bold_admin_session');
      if (stored) {
        const parsed = JSON.parse(stored) as AdminUser;
        if (parsed && parsed.isActive) {
          setUser(parsed);
        }
      }
    } catch {
      localStorage.removeItem('bold_admin_session');
    } finally {
      setIsLoading(false);
    }
  }, []);

  const login = async (email: string, pass: string): Promise<boolean> => {
    setIsLoading(true);
    setError(null);

    // Simulate authenticating against Firebase/Gateway
    await new Promise((r) => setTimeout(r, 300));

    if (!email.includes('@') || pass.length < 6) {
      setError('Please provide a valid email and password (minimum 6 characters).');
      setIsLoading(false);
      return false;
    }

    // Role mapping based on email prefix or default
    let assignedRole: AdminRole = 'operations_admin';
    if (email.includes('compliance')) assignedRole = 'compliance_lead';
    if (email.includes('quant')) assignedRole = 'quant_lead';
    if (email.includes('super') || email.startsWith('admin@')) assignedRole = 'super_admin';

    const sessionUser: AdminUser = {
      ...DEMO_ADMIN,
      email,
      fullName: email.startsWith('admin@') ? 'Super Administrator' : email.split('@')[0].replace('.', ' ').toUpperCase(),
      role: assignedRole,
      lastLoginAt: new Date().toISOString(),
    };

    setUser(sessionUser);
    localStorage.setItem('bold_admin_session', JSON.stringify(sessionUser));
    setIsLoading(false);
    return true;
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('bold_admin_session');
  };

  const switchRole = (newRole: AdminRole) => {
    if (!user) return;
    const updated: AdminUser = { ...user, role: newRole };
    setUser(updated);
    localStorage.setItem('bold_admin_session', JSON.stringify(updated));
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        role: user?.role ?? null,
        isAuthenticated: !!user,
        isLoading,
        error,
        login,
        logout,
        switchRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
