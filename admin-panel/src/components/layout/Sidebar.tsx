import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  Activity, 
  Key, 
  GitPullRequest, 
  Radio, 
  ShieldAlert, 
  FileText, 
  Shield,
  X,
  TrendingUp
} from 'lucide-react';
import { cn } from '@/lib/utils';

interface NavItem {
  name: string;
  to: string;
  icon: React.ElementType;
  badge?: string;
}

const navItems: NavItem[] = [
  { name: 'Platform Overview', to: '/overview', icon: Activity },
  { name: 'Trading Terminal', to: '/terminal', icon: TrendingUp, badge: 'Pro' },
  { name: 'Key & Secret Vault', to: '/keys', icon: Key, badge: 'Live' },
  { name: 'Strategy Queue', to: '/strategies', icon: GitPullRequest, badge: '3' },
  { name: 'Feed Health', to: '/feed-health', icon: Radio },
  { name: 'Governance & Kill Switch', to: '/governance', icon: ShieldAlert },
  { name: 'Audit Trail', to: '/audit', icon: FileText },
];

interface SidebarProps {
  isOpen?: boolean;
  onClose?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ isOpen = false, onClose }) => {
  return (
    <>
      {/* Mobile Backdrop */}
      {isOpen && (
        <div 
          className="fixed inset-0 z-40 bg-bg-app/80 backdrop-blur-sm lg:hidden transition-opacity"
          onClick={onClose}
          aria-hidden="true"
        />
      )}

      {/* Sidebar Container */}
      <aside 
        className={cn(
          "w-64 shrink-0 border-r border-border-subtle bg-bg-app flex flex-col h-screen select-none transition-transform duration-300 z-50",
          "fixed inset-y-0 left-0 lg:static lg:translate-x-0",
          isOpen ? "translate-x-0 shadow-2xl" : "-translate-x-full lg:translate-x-0"
        )}
      >
        {/* Brand Header */}
        <div className="h-16 flex items-center justify-between px-5 border-b border-border-subtle">
          <div className="flex items-center gap-2.5">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-surface-elevated border border-border-subtle shadow-glow-green">
              <Shield className="h-4 w-4 text-primary-bullish" />
            </div>
            <div>
              <span className="font-bold text-sm tracking-tight text-text-primary block leading-none">
                BOLD <span className="text-primary-bullish">ADMIN</span>
              </span>
              <span className="text-[10px] text-text-muted tracking-widest uppercase">
                Control Room v1.0
              </span>
            </div>
          </div>

          {/* Close button on mobile */}
          <button 
            type="button" 
            onClick={onClose}
            className="lg:hidden p-1.5 rounded-md text-text-secondary hover:text-text-primary hover:bg-surface-elevated"
            aria-label="Close sidebar"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Navigation List */}
        <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
          <p className="px-3 pb-2 text-[10px] font-semibold uppercase tracking-wider text-text-muted">
            Platform Operations
          </p>

          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.to}
                to={item.to}
                onClick={() => onClose?.()}
                className={({ isActive }) =>
                  cn(
                    "flex items-center justify-between px-3 py-2.5 rounded-md text-xs font-medium transition-all group",
                    isActive
                      ? "bg-surface-elevated text-primary-bullish font-semibold border-l-2 border-primary-bullish shadow-sm"
                      : "text-text-secondary hover:text-text-primary hover:bg-surface-elevated/60"
                  )
                }
              >
                <div className="flex items-center gap-2.5 min-w-0">
                  <Icon className="h-4 w-4 shrink-0 transition-colors group-hover:text-primary-bullish" />
                  <span className="truncate">{item.name}</span>
                </div>
                {item.badge && (
                  <span className="shrink-0 rounded-full bg-surface-card border border-border-subtle px-1.5 py-0.5 text-[10px] font-mono text-text-muted">
                    {item.badge}
                  </span>
                )}
              </NavLink>
            );
          })}
        </nav>

        {/* Footer Info */}
        <div className="p-3 border-t border-border-subtle text-[11px] text-text-muted">
          <div className="flex items-center justify-between">
            <span>Engine Status</span>
            <span className="flex items-center gap-1.5 text-primary-bullish font-mono">
              <span className="h-2 w-2 rounded-full bg-primary-bullish animate-pulse"></span> Online
            </span>
          </div>
        </div>
      </aside>
    </>
  );
};
