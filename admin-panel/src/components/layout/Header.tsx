import React, { useState, useEffect } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import type { AdminRole } from '@/features/auth/types';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { LogOut, User, Clock, ShieldCheck, Menu } from 'lucide-react';

interface HeaderProps {
  onToggleNav?: () => void;
}

export const Header: React.FC<HeaderProps> = ({ onToggleNav }) => {
  const { user, logout, switchRole } = useAuth();
  const [istTime, setIstTime] = useState<string>('');

  useEffect(() => {
    const updateClock = () => {
      const now = new Date();
      const formatted = new Intl.DateTimeFormat('en-IN', {
        timeZone: 'Asia/Kolkata',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false,
      }).format(now);
      setIstTime(`${formatted} IST`);
    };

    updateClock();
    const interval = setInterval(updateClock, 1000);
    return () => clearInterval(interval);
  }, []);

  return (
    <header className="h-16 border-b border-border-subtle bg-bg-app px-4 sm:px-6 flex items-center justify-between shrink-0 gap-2">
      {/* Left: Mobile Nav Hamburger & Environment & Market Clock */}
      <div className="flex items-center gap-2 sm:gap-4 min-w-0">
        <button
          type="button"
          onClick={onToggleNav}
          className="lg:hidden p-2 rounded-md text-text-secondary hover:text-text-primary hover:bg-surface-elevated shrink-0"
          aria-label="Open navigation menu"
        >
          <Menu className="h-5 w-5" />
        </button>

        <Badge variant="accent" dot className="hidden xs:inline-flex text-[10px] sm:text-xs">
          SANDBOX
        </Badge>

        <div className="hidden sm:flex items-center gap-1.5 text-xs text-text-secondary font-mono bg-surface-card border border-border-subtle px-2.5 py-1 rounded-md shrink-0">
          <Clock className="h-3.5 w-3.5 text-text-muted" />
          <span>{istTime || '09:41:00 IST'}</span>
        </div>
      </div>

      {/* Right: Role Switcher & User Profile & Logout */}
      <div className="flex items-center gap-2 sm:gap-3 shrink-0">
        {/* Interactive Role Switcher */}
        <div className="flex items-center gap-1 sm:gap-1.5 bg-surface-elevated border border-border-subtle rounded-md px-2 py-1">
          <ShieldCheck className="h-3.5 w-3.5 text-primary-bullish shrink-0" />
          <span className="hidden md:inline text-[11px] text-text-muted font-medium">Role:</span>
          <select
            value={user?.role || 'super_admin'}
            onChange={(e) => switchRole(e.target.value as AdminRole)}
            className="bg-transparent text-[11px] font-mono text-primary-bullish focus:outline-none cursor-pointer max-w-[110px] sm:max-w-none"
            aria-label="Active administrative role"
          >
            <option value="super_admin" className="bg-bg-app text-text-primary">Super Admin</option>
            <option value="operations_admin" className="bg-bg-app text-text-primary">Ops Admin</option>
            <option value="compliance_lead" className="bg-bg-app text-text-primary">Compliance Lead</option>
            <option value="quant_lead" className="bg-bg-app text-text-primary">Quant Lead</option>
          </select>
        </div>

        <div className="text-right hidden xl:block">
          <p className="text-xs font-semibold text-text-primary leading-tight truncate max-w-[140px]">{user?.fullName}</p>
          <p className="text-[10px] font-mono text-primary-bullish uppercase tracking-wider">
            {user?.role?.replace('_', ' ')}
          </p>
        </div>

        <div className="h-8 w-8 rounded-full bg-surface-elevated border border-border-subtle hidden sm:flex items-center justify-center text-text-secondary shrink-0">
          <User className="h-4 w-4" />
        </div>

        <Button
          variant="outline"
          size="sm"
          onClick={logout}
          className="text-xs text-text-secondary hover:text-accent-bearish hover:border-accent-bearish shrink-0 px-2.5 sm:px-3"
          title="Sign out of console"
        >
          <LogOut className="h-3.5 w-3.5 sm:mr-1.5" />
          <span className="hidden sm:inline">Sign Out</span>
        </Button>
      </div>
    </header>
  );
};
