import React, { useState } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { 
  ShieldAlert, 
  ToggleLeft, 
  ToggleRight, 
  AlertTriangle, 
  Sliders, 
  Lock 
} from 'lucide-react';

interface FeatureFlag {
  id: string;
  key: string;
  name: string;
  description: string;
  isEnabled: boolean;
  isLocked?: boolean;
  category: 'core' | 'billing' | 'options' | 'research';
}

const INITIAL_FLAGS: FeatureFlag[] = [
  {
    id: 'f1',
    key: 'GLOBAL_FREE_TIER_OVERRIDE',
    name: 'Global Free-Tier Mode (100% Free Launch)',
    description: 'Unlocks all Pro and Premium features (unlimited scanner, option Greeks, backtesting) without subscription barriers.',
    isEnabled: true,
    isLocked: true,
    category: 'billing',
  },
  {
    id: 'f2',
    key: 'F_AND_O_CHAIN_ENABLED',
    name: 'F&O Derivatives Chain & Greeks Desk',
    description: 'Enables strike table, implied volatility, Delta, Gamma, Theta, Vega, and payoff diagram rendering on mobile.',
    isEnabled: true,
    category: 'options',
  },
  {
    id: 'f3',
    key: 'AI_RESEARCH_ASSISTANT_ENABLED',
    name: 'AI Research Assistant (Sourced RAG)',
    description: 'Enables Screen S10 natural-language queries over verified quarterly company filings with numbered citations.',
    isEnabled: true,
    category: 'research',
  },
  {
    id: 'f4',
    key: 'REALISTIC_SLIPPAGE_AND_FEE_MODELING',
    name: 'Paper Simulator Fee & Slippage Modeling',
    description: 'Simulates statutory STT, GST, and exchange fees alongside 0.05% slippage on virtual order fills.',
    isEnabled: true,
    category: 'core',
  },
];

export const GovernanceScreen: React.FC = () => {
  const [killSwitchActive, setKillSwitchActive] = useState<boolean>(false);
  const [showKillModal, setShowKillModal] = useState<boolean>(false);
  const [confirmInput, setConfirmInput] = useState<string>('');
  const [flags, setFlags] = useState<FeatureFlag[]>(INITIAL_FLAGS);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const handleToggleFlag = (id: string) => {
    setFlags(prev => prev.map(f => {
      if (f.id === id && !f.isLocked) {
        const next = !f.isEnabled;
        setToastMessage(`Feature flag '${f.key}' updated to ${next ? 'ENABLED' : 'DISABLED'}.`);
        setTimeout(() => setToastMessage(null), 3000);
        return { ...f, isEnabled: next };
      }
      return f;
    }));
  };

  const handleConfirmKillSwitch = () => {
    if (confirmInput !== 'CONFIRM FREEZE') return;
    setKillSwitchActive(true);
    setShowKillModal(false);
    setConfirmInput('');
    setToastMessage('EMERGENCY KILL SWITCH ENGAGED: Live signals and paper order simulation paused.');
    setTimeout(() => setToastMessage(null), 5000);
  };

  const handleResumeKillSwitch = () => {
    setKillSwitchActive(false);
    setToastMessage('NORMAL OPERATIONS RESUMED: Paper trading and signals engine unfrozen.');
    setTimeout(() => setToastMessage(null), 4000);
  };

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Toast */}
      {toastMessage && (
        <div className="fixed top-5 right-5 z-50 flex items-center gap-2 rounded-lg border border-accent-warning/40 bg-surface-elevated px-4 py-3 text-xs text-accent-warning shadow-xl max-w-md">
          <AlertTriangle className="h-4 w-4 shrink-0" />
          <span className="truncate">{toastMessage}</span>
        </div>
      )}

      {/* Header */}
      <div>
        <h1 className="text-xl font-bold tracking-tight text-text-primary flex items-center gap-2">
          <Sliders className="h-5 w-5 text-primary-bullish shrink-0" />
          <span>Governance, Feature Flags & Emergency Kill Switch</span>
        </h1>
        <p className="text-xs text-text-secondary mt-0.5">
          Global operational toggles, 100% free launch settings, and circuit-breaker emergency platform halts.
        </p>
      </div>

      {/* Master Emergency Kill Switch Card */}
      <Card className={`border-2 transition-all ${
        killSwitchActive 
          ? 'border-accent-bearish bg-accent-bearish/10 shadow-glow-red' 
          : 'border-border-subtle bg-surface-card'
      }`}>
        <CardContent className="p-4 sm:p-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div className="flex items-start gap-4 min-w-0">
              <div className={`flex h-12 w-12 shrink-0 items-center justify-center rounded-xl border ${
                killSwitchActive 
                  ? 'border-accent-bearish bg-accent-bearish/20 text-accent-bearish animate-pulse' 
                  : 'border-border-subtle bg-surface-elevated text-text-muted'
              }`}>
                <ShieldAlert className="h-6 w-6" />
              </div>
              <div className="space-y-1.5 min-w-0">
                <div className="flex flex-wrap items-center gap-2">
                  <h3 className="text-base font-bold text-text-primary">MASTER PLATFORM KILL SWITCH</h3>
                  <Badge variant={killSwitchActive ? 'danger' : 'neutral'} dot>
                    {killSwitchActive ? 'ACTIVE (FROZEN)' : 'STANDBY'}
                  </Badge>
                </div>
                <p className="text-xs text-text-secondary leading-relaxed">
                  Immediately halts all signal generation and paper order simulation across all connected mobile apps in &lt; 500ms.
                  Use during corrupted feed events or upstream data provider anomalies.
                </p>
              </div>
            </div>

            <div className="shrink-0 w-full sm:w-auto">
              {killSwitchActive ? (
                <Button 
                  variant="primary" 
                  onClick={handleResumeKillSwitch}
                  className="w-full sm:w-auto"
                >
                  Resume Normal Operations
                </Button>
              ) : (
                <Button 
                  variant="destructive" 
                  onClick={() => setShowKillModal(true)}
                  className="w-full sm:w-auto"
                >
                  Engage Emergency Freeze
                </Button>
              )}
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Feature Flags Card */}
      <Card>
        <CardHeader className="pb-3 border-b border-border-subtle">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
            <CardTitle className="text-sm">Platform Feature Flags ({flags.length})</CardTitle>
            <Badge variant="healthy" dot>100% Free Tier Active</Badge>
          </div>
        </CardHeader>
        <CardContent className="divide-y divide-border-subtle p-0">
          {flags.map((flag) => (
            <div key={flag.id} className="p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 hover:bg-surface-elevated/30 transition-colors">
              <div className="space-y-1 min-w-0">
                <div className="flex flex-wrap items-center gap-2">
                  <span className="text-xs font-semibold text-text-primary">{flag.name}</span>
                  <span className="font-mono text-[10px] text-text-muted bg-surface-elevated px-1.5 py-0.5 rounded border border-border-subtle">
                    {flag.key}
                  </span>
                  {flag.isLocked && (
                    <span className="inline-flex items-center gap-1 text-[10px] text-primary-bullish font-semibold bg-primary-bullish/10 px-1.5 py-0.5 rounded whitespace-nowrap">
                      <Lock className="h-3 w-3" /> Launch Mandate
                    </span>
                  )}
                </div>
                <p className="text-xs text-text-secondary">{flag.description}</p>
              </div>

              <div className="self-end sm:self-center shrink-0">
                <button
                  type="button"
                  onClick={() => handleToggleFlag(flag.id)}
                  disabled={flag.isLocked}
                  className={`transition-colors focus:outline-none ${flag.isLocked ? 'cursor-not-allowed opacity-80' : 'cursor-pointer'}`}
                  aria-label={`Toggle ${flag.name}`}
                >
                  {flag.isEnabled ? (
                    <ToggleRight className="h-8 w-8 text-primary-bullish" />
                  ) : (
                    <ToggleLeft className="h-8 w-8 text-text-muted" />
                  )}
                </button>
              </div>
            </div>
          ))}
        </CardContent>
      </Card>

      {/* Confirmation Modal for Kill Switch */}
      {showKillModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-bg-app/80 backdrop-blur-sm p-4">
          <Card className="w-full max-w-md border-accent-bearish shadow-2xl">
            <CardHeader className="pb-3 border-b border-border-subtle">
              <CardTitle className="text-base text-accent-bearish flex items-center gap-2">
                <AlertTriangle className="h-5 w-5 shrink-0" />
                <span>Confirm Emergency Platform Halt</span>
              </CardTitle>
            </CardHeader>
            <CardContent className="pt-4 space-y-4">
              <p className="text-xs text-text-secondary leading-relaxed">
                This will immediately broadcast a halt signal to all mobile applications, pausing simulated order placement and freezing the algorithmic signal stream.
              </p>

              <div className="space-y-1.5">
                <label className="text-xs font-semibold text-text-secondary">
                  Type <span className="font-mono text-accent-bearish font-bold">CONFIRM FREEZE</span> to proceed:
                </label>
                <Input
                  value={confirmInput}
                  onChange={(e) => setConfirmInput(e.target.value)}
                  placeholder="CONFIRM FREEZE"
                  autoFocus
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2">
                <Button variant="outline" onClick={() => { setShowKillModal(false); setConfirmInput(''); }}>
                  Cancel
                </Button>
                <Button 
                  variant="destructive" 
                  disabled={confirmInput !== 'CONFIRM FREEZE'}
                  onClick={handleConfirmKillSwitch}
                >
                  Halt Platform
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
};