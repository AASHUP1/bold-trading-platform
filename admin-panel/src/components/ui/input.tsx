import * as React from 'react';
import { cn } from '@/lib/utils';
import { Eye, EyeOff } from 'lucide-react';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  isSecret?: boolean;
  error?: string;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ className, type = 'text', isSecret = false, error, ...props }, ref) => {
    const [showSecret, setShowSecret] = React.useState(false);

    const actualType = isSecret ? (showSecret ? 'text' : 'password') : type;

    return (
      <div className="relative w-full">
        <input
          type={actualType}
          ref={ref}
          className={cn(
            "flex h-10 w-full rounded-md border border-border-subtle bg-surface-card px-3 py-2 text-sm text-text-primary placeholder:text-text-muted transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary-bullish focus-visible:border-transparent disabled:cursor-not-allowed disabled:opacity-50",
            isSecret && "pr-10 font-mono",
            error && "border-accent-bearish focus-visible:ring-accent-bearish",
            className
          )}
          {...props}
        />
        {isSecret && (
          <button
            type="button"
            onClick={() => setShowSecret(!showSecret)}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-text-secondary hover:text-text-primary focus:outline-none"
            tabIndex={-1}
            aria-label={showSecret ? "Hide secret" : "Show secret"}
          >
            {showSecret ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
          </button>
        )}
        {error && <p className="mt-1 text-xs text-accent-bearish">{error}</p>}
      </div>
    );
  }
);
Input.displayName = 'Input';
