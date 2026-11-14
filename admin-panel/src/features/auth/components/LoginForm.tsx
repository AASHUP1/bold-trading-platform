import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { useAuth } from '../hooks/useAuth';
import { Shield, AlertCircle, KeyRound, Sparkles } from 'lucide-react';

export const LoginForm: React.FC = () => {
  const navigate = useNavigate();
  const { login, isAuthenticated, error: authError, isLoading } = useAuth();
  const [email, setEmail] = useState('admin@bold.app');
  const [password, setPassword] = useState('password123');
  const [clientError, setClientError] = useState<string | null>(null);

  // If already logged in, navigate immediately to overview
  useEffect(() => {
    if (isAuthenticated) {
      navigate('/overview', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setClientError(null);

    if (!email || !password) {
      setClientError('Please enter both your email address and password.');
      return;
    }

    const success = await login(email, password);
    if (success) {
      navigate('/overview', { replace: true });
    }
  };

  const handleQuickLogin = async (demoEmail: string) => {
    setEmail(demoEmail);
    setPassword('password123');
    const success = await login(demoEmail, 'password123');
    if (success) {
      navigate('/overview', { replace: true });
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-bg-app p-4">
      <div className="w-full max-w-md space-y-6">
        <div className="text-center space-y-2">
          <div className="inline-flex h-12 w-12 items-center justify-center rounded-xl bg-surface-elevated border border-border-subtle shadow-glow-green">
            <Shield className="h-6 w-6 text-primary-bullish" />
          </div>
          <h1 className="text-2xl font-bold tracking-tight text-text-primary">
            BOLD <span className="text-primary-bullish">CONTROL ROOM</span>
          </h1>
          <p className="text-xs text-text-secondary uppercase tracking-widest">
            Institutional Operations & Compliance Portal
          </p>
        </div>

        <Card className="border-border-subtle shadow-xl">
          <CardHeader className="text-center pb-2">
            <CardTitle className="text-lg">Sign in to Console</CardTitle>
            <CardDescription>Enter your corporate credentials to access platform controls</CardDescription>
          </CardHeader>

          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              {(clientError || authError) && (
                <div className="flex items-start gap-2.5 rounded-md border border-accent-bearish/30 bg-accent-bearish/10 p-3 text-xs text-accent-bearish">
                  <AlertCircle className="h-4 w-4 shrink-0 mt-0.5" />
                  <span>{clientError || authError}</span>
                </div>
              )}

              <div className="space-y-1.5">
                <label htmlFor="corporate-email" className="text-xs font-medium text-text-secondary">
                  Corporate Email
                </label>
                <Input
                  id="corporate-email"
                  name="email"
                  type="email"
                  placeholder="admin@bold.app"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  disabled={isLoading}
                  autoComplete="email"
                  required
                />
              </div>

              <div className="space-y-1.5">
                <div className="flex items-center justify-between">
                  <label htmlFor="corporate-password" className="text-xs font-medium text-text-secondary">
                    Password
                  </label>
                  <span className="text-xs text-text-muted hover:text-text-secondary cursor-pointer">
                    Forgot?
                  </span>
                </div>
                <Input
                  id="corporate-password"
                  name="password"
                  isSecret
                  placeholder="••••••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  disabled={isLoading}
                  autoComplete="current-password"
                  required
                />
              </div>

              <Button type="submit" variant="primary" className="w-full mt-2" isLoading={isLoading}>
                Authenticate & Access
              </Button>
            </form>

            {/* Quick 1-Click Demo Access */}
            <div className="mt-6 pt-5 border-t border-border-subtle/60 text-center space-y-3">
              <Button
                type="button"
                variant="outline"
                size="sm"
                className="w-full text-xs text-primary-bullish border-primary-bullish/30 hover:bg-primary-bullish/10"
                onClick={() => handleQuickLogin('admin@bold.app')}
                disabled={isLoading}
              >
                <Sparkles className="h-3.5 w-3.5 mr-1.5" />
                1-Click Sign In as Super Admin
              </Button>

              <div>
                <p className="text-xs text-text-muted mb-2 flex items-center justify-center gap-1">
                  <KeyRound className="h-3.5 w-3.5" /> Or choose role to test:
                </p>
                <div className="flex flex-wrap justify-center gap-1.5">
                  <button
                    type="button"
                    onClick={() => handleQuickLogin('ops.admin@boldtrading.internal')}
                    className="rounded border border-border-subtle bg-surface-elevated px-2 py-1 text-[11px] text-text-secondary hover:text-primary-bullish hover:border-primary-bullish transition-colors"
                  >
                    Ops Admin
                  </button>
                  <button
                    type="button"
                    onClick={() => handleQuickLogin('compliance.lead@boldtrading.internal')}
                    className="rounded border border-border-subtle bg-surface-elevated px-2 py-1 text-[11px] text-text-secondary hover:text-primary-bullish hover:border-primary-bullish transition-colors"
                  >
                    Compliance Lead
                  </button>
                  <button
                    type="button"
                    onClick={() => handleQuickLogin('quant.lead@boldtrading.internal')}
                    className="rounded border border-border-subtle bg-surface-elevated px-2 py-1 text-[11px] text-text-secondary hover:text-primary-bullish hover:border-primary-bullish transition-colors"
                  >
                    Quant Lead
                  </button>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <p className="text-center text-[11px] text-text-muted">
          Authorized personnel only. All administrative events are cryptographically recorded in the immutable audit trail.
        </p>
      </div>
    </div>
  );
};
