import * as React from 'react';
import { cn } from '@/lib/utils';

export interface BadgeProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: 'healthy' | 'degraded' | 'danger' | 'neutral' | 'accent';
  dot?: boolean;
}

export function Badge({ className, variant = 'neutral', dot = true, children, ...props }: BadgeProps) {
  const variants = {
    healthy: "border-[#38D996]/30 bg-[#38D996]/10 text-primary-bullish",
    degraded: "border-[#F3C969]/30 bg-[#F3C969]/10 text-accent-warning",
    danger: "border-[#FF657A]/30 bg-[#FF657A]/10 text-accent-bearish",
    neutral: "border-border-subtle bg-surface-elevated text-text-secondary",
    accent: "border-primary-accent/30 bg-primary-accent/10 text-primary-accent",
  };

  const dotColors = {
    healthy: "bg-primary-bullish",
    degraded: "bg-accent-warning animate-pulse",
    danger: "bg-accent-bearish animate-pulse",
    neutral: "bg-text-secondary",
    accent: "bg-primary-accent",
  };

  return (
    <div
      className={cn(
        "inline-flex items-center gap-1.5 rounded-sm border px-2 py-0.5 text-xs font-semibold uppercase tracking-wider shrink-0 whitespace-nowrap select-none",
        variants[variant],
        className
      )}
      {...props}
    >
      {dot && <span className={cn("h-1.5 w-1.5 shrink-0 rounded-full", dotColors[variant])} />}
      <span>{children}</span>
    </div>
  );
}
