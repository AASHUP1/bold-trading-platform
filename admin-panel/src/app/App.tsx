import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider, useAuth } from '@/features/auth/hooks/useAuth';
import { LoginForm } from '@/features/auth/components/LoginForm';
import { AdminLayout } from '@/components/layout/AdminLayout';
import { OverviewScreen } from '@/features/overview/OverviewScreen';
import { KeyManagerScreen } from '@/features/keys/KeyManagerScreen';
import { StrategyQueueScreen } from '@/features/strategies/StrategyQueueScreen';
import { FeedHealthScreen } from '@/features/feed-health/FeedHealthScreen';
import { GovernanceScreen } from '@/features/governance/GovernanceScreen';
import { AuditTrailScreen } from '@/features/audit/AuditTrailScreen';
import { TradingTerminalScreen } from '@/features/terminal/TradingTerminalScreen';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
});

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="flex h-screen w-screen items-center justify-center bg-bg-app">
        <div className="flex flex-col items-center gap-3 text-primary-bullish">
          <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary-bullish border-t-transparent" />
          <span className="text-xs font-mono tracking-widest text-text-secondary uppercase">
            Loading Control Room...
          </span>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};

const PublicRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="flex h-screen w-screen items-center justify-center bg-bg-app">
        <div className="flex flex-col items-center gap-3 text-primary-bullish">
          <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary-bullish border-t-transparent" />
          <span className="text-xs font-mono tracking-widest text-text-secondary uppercase">
            Loading Control Room...
          </span>
        </div>
      </div>
    );
  }

  if (isAuthenticated) {
    return <Navigate to="/overview" replace />;
  }

  return <>{children}</>;
};

export const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route
              path="/login"
              element={
                <PublicRoute>
                  <LoginForm />
                </PublicRoute>
              }
            />

            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <AdminLayout />
                </ProtectedRoute>
              }
            >
              <Route index element={<Navigate to="/overview" replace />} />
              <Route path="overview" element={<OverviewScreen />} />
              <Route path="terminal" element={<TradingTerminalScreen />} />
              <Route path="keys" element={<KeyManagerScreen />} />
              <Route path="strategies" element={<StrategyQueueScreen />} />
              <Route path="feed-health" element={<FeedHealthScreen />} />
              <Route path="governance" element={<GovernanceScreen />} />
              <Route path="audit" element={<AuditTrailScreen />} />
            </Route>

            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </QueryClientProvider>
  );
};
