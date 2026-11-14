import React, { useState } from 'react';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { 
  GitPullRequest, 
  Check, 
  X, 
  Eye, 
  CheckCircle2 
} from 'lucide-react';

interface StrategyItem {
  id: string;
  name: string;
  author: string;
  instrument: string;
  timeframe: string;
  riskCategory: 'low' | 'medium' | 'high';
  winRate: number;
  profitFactor: number;
  maxDrawdown: number;
  status: 'pending' | 'approved' | 'rejected';
  submittedDate: string;
  rules: {
    entry: string;
    exit: string;
    stopLoss: string;
  };
}

const INITIAL_STRATEGIES: StrategyItem[] = [
  {
    id: 's1',
    name: 'NIFTY momentum v1.4',
    author: 'Quant Team',
    instrument: 'NIFTY 50',
    timeframe: '15m',
    riskCategory: 'medium',
    winRate: 58.6,
    profitFactor: 1.84,
    maxDrawdown: 6.20,
    status: 'pending',
    submittedDate: '12 Nov 2026, 09:30 IST',
    rules: {
      entry: 'EMA 20 crosses above EMA 50 AND RSI(14) > 55',
      exit: 'Price closes below EMA 20 OR target reaches 2R',
      stopLoss: '1% fixed stop loss · 0.5% trailing stop',
    },
  },
  {
    id: 's2',
    name: 'VWAP reclaim v2.1',
    author: 'Research Lead',
    instrument: 'NSE Liquid 50',
    timeframe: '15m',
    riskCategory: 'low',
    winRate: 62.1,
    profitFactor: 2.10,
    maxDrawdown: 4.80,
    status: 'approved',
    submittedDate: '11 Nov 2026, 14:10 IST',
    rules: {
      entry: 'Close above VWAP with 1.8x 20-period average volume',
      exit: 'Close below VWAP or 15:00 IST session close',
      stopLoss: 'Lowest low of previous 2 candles',
    },
  },
  {
    id: 's3',
    name: 'Bank spread v0.8',
    author: 'Quant Team',
    instrument: 'BANK NIFTY Options',
    timeframe: '5m',
    riskCategory: 'high',
    winRate: 51.2,
    profitFactor: 1.42,
    maxDrawdown: 14.20,
    status: 'pending',
    submittedDate: '10 Nov 2026, 16:45 IST',
    rules: {
      entry: 'Straddle delta imbalance > 0.35 at opening 30 mins',
      exit: 'Gamma spike or 50% decay captured',
      stopLoss: '1.5x initial combined credit',
    },
  },
];

export const StrategyQueueScreen: React.FC = () => {
  const [strategies, setStrategies] = useState<StrategyItem[]>(INITIAL_STRATEGIES);
  const [riskFilter, setRiskFilter] = useState<string>('all');
  const [selectedStrategy, setSelectedStrategy] = useState<StrategyItem | null>(null);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const filtered = riskFilter === 'all'
    ? strategies
    : strategies.filter(s => s.riskCategory === riskFilter);

  const handleDecision = (id: string, decision: 'approved' | 'rejected') => {
    setStrategies(prev => prev.map(s => s.id === id ? { ...s, status: decision } : s));
    const target = strategies.find(s => s.id === id);
    setToastMessage(`Strategy '${target?.name}' marked as ${decision.toUpperCase()}.`);
    if (selectedStrategy?.id === id) {
      setSelectedStrategy(null);
    }
    setTimeout(() => setToastMessage(null), 3500);
  };

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Toast */}
      {toastMessage && (
        <div className="fixed top-5 right-5 z-50 flex items-center gap-2 rounded-lg border border-primary-bullish/40 bg-surface-elevated px-4 py-3 text-xs text-primary-bullish shadow-xl max-w-md">
          <CheckCircle2 className="h-4 w-4 shrink-0" />
          <span className="truncate">{toastMessage}</span>
        </div>
      )}

      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="min-w-0">
          <h1 className="text-xl font-bold tracking-tight text-text-primary flex items-center gap-2">
            <GitPullRequest className="h-5 w-5 text-primary-bullish shrink-0" />
            <span>Strategy Review & Compliance Queue</span>
          </h1>
          <p className="text-xs text-text-secondary mt-0.5">
            Inspect algorithmic rules, backtest metrics, and approve strategies before mobile signal broadcasting.
          </p>
        </div>

        {/* Filter chips */}
        <div className="flex flex-wrap items-center gap-1.5 bg-surface-card p-1 rounded-lg border border-border-subtle shrink-0">
          {['all', 'low', 'medium', 'high'].map((risk) => (
            <button
              key={risk}
              onClick={() => setRiskFilter(risk)}
              className={`px-3 py-1 text-xs rounded font-medium transition-colors whitespace-nowrap ${
                riskFilter === risk
                  ? 'bg-primary-bullish text-bg-app font-semibold'
                  : 'text-text-secondary hover:text-text-primary'
              }`}
            >
              {risk.toUpperCase()}
            </button>
          ))}
        </div>
      </div>

      {/* Queue Table Card */}
      <Card>
        <CardContent className="p-0 overflow-x-auto">
          <table className="w-full text-left border-collapse min-w-[680px]">
            <thead>
              <tr className="border-b border-border-subtle bg-bg-app text-[11px] font-semibold uppercase tracking-wider text-text-secondary">
                <th className="py-3 px-4">Strategy & Author</th>
                <th className="py-3 px-4">Instrument / TF</th>
                <th className="py-3 px-4">Risk Tier</th>
                <th className="py-3 px-4">Win Rate</th>
                <th className="py-3 px-4">Max Drawdown</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border-subtle text-xs">
              {filtered.map((s) => (
                <tr key={s.id} className="hover:bg-surface-elevated/40 transition-colors">
                  <td className="py-3 px-4 whitespace-nowrap">
                    <p className="font-semibold text-text-primary">{s.name}</p>
                    <span className="text-[11px] text-text-muted">{s.author} · {s.submittedDate}</span>
                  </td>

                  <td className="py-3 px-4 font-mono whitespace-nowrap">
                    <span className="text-text-primary font-sans">{s.instrument}</span>
                    <span className="ml-1 text-[10px] text-text-muted">({s.timeframe})</span>
                  </td>

                  <td className="py-3 px-4 whitespace-nowrap">
                    <Badge variant={s.riskCategory === 'high' ? 'danger' : s.riskCategory === 'medium' ? 'degraded' : 'healthy'}>
                      {s.riskCategory}
                    </Badge>
                  </td>

                  <td className="py-3 px-4 font-mono text-primary-bullish font-semibold whitespace-nowrap">
                    {s.winRate}%
                  </td>

                  <td className="py-3 px-4 font-mono text-accent-bearish whitespace-nowrap">
                    {s.maxDrawdown}%
                  </td>

                  <td className="py-3 px-4 whitespace-nowrap">
                    <Badge variant={s.status === 'approved' ? 'healthy' : s.status === 'rejected' ? 'danger' : 'degraded'}>
                      {s.status}
                    </Badge>
                  </td>

                  <td className="py-3 px-4 text-right whitespace-nowrap">
                    <div className="flex items-center justify-end gap-1.5">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => setSelectedStrategy(s)}
                        className="h-7 text-xs px-2.5"
                      >
                        <Eye className="h-3.5 w-3.5 mr-1" /> Inspect
                      </Button>
                      {s.status === 'pending' && (
                        <>
                          <Button
                            variant="primary"
                            size="sm"
                            onClick={() => handleDecision(s.id, 'approved')}
                            className="h-7 text-xs px-2"
                            title="Approve Strategy"
                          >
                            <Check className="h-3.5 w-3.5" />
                          </Button>
                          <Button
                            variant="destructive"
                            size="sm"
                            onClick={() => handleDecision(s.id, 'rejected')}
                            className="h-7 text-xs px-2"
                            title="Reject Strategy"
                          >
                            <X className="h-3.5 w-3.5" />
                          </Button>
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </CardContent>
      </Card>

      {/* Inspect Side Drawer */}
      {selectedStrategy && (
        <div className="fixed inset-0 z-50 flex items-center justify-end bg-bg-app/80 backdrop-blur-sm">
          <div className="w-full sm:max-w-md md:max-w-lg h-full bg-surface-card border-l border-border-subtle p-4 sm:p-6 space-y-6 overflow-y-auto">
            <div className="flex items-center justify-between pb-3 border-b border-border-subtle gap-2">
              <div className="min-w-0">
                <h3 className="text-base font-bold text-text-primary truncate">{selectedStrategy.name}</h3>
                <span className="text-xs text-text-muted truncate block">Submitted by {selectedStrategy.author}</span>
              </div>
              <Button variant="ghost" size="sm" onClick={() => setSelectedStrategy(null)} className="shrink-0">
                <X className="h-4 w-4" />
              </Button>
            </div>

            <div className="space-y-4">
              <div>
                <h4 className="text-xs font-semibold uppercase tracking-wider text-text-muted mb-2">Backtest Summary</h4>
                <div className="grid grid-cols-3 gap-2 text-center">
                  <div className="p-2.5 rounded bg-surface-elevated border border-border-subtle min-w-0">
                    <span className="text-[10px] text-text-muted block truncate">Win Rate</span>
                    <span className="text-sm font-bold font-mono text-primary-bullish">{selectedStrategy.winRate}%</span>
                  </div>
                  <div className="p-2.5 rounded bg-surface-elevated border border-border-subtle min-w-0">
                    <span className="text-[10px] text-text-muted block truncate">Profit Factor</span>
                    <span className="text-sm font-bold font-mono text-text-primary">{selectedStrategy.profitFactor}</span>
                  </div>
                  <div className="p-2.5 rounded bg-surface-elevated border border-border-subtle min-w-0">
                    <span className="text-[10px] text-text-muted block truncate">Max DD</span>
                    <span className="text-sm font-bold font-mono text-accent-bearish">{selectedStrategy.maxDrawdown}%</span>
                  </div>
                </div>
              </div>

              <div className="space-y-3">
                <h4 className="text-xs font-semibold uppercase tracking-wider text-text-muted">Rule Logic Tree</h4>
                <div className="p-3 rounded bg-surface-elevated border border-border-subtle space-y-2 text-xs">
                  <p className="break-words"><span className="text-primary-bullish font-semibold">01 ENTRY:</span> {selectedStrategy.rules.entry}</p>
                  <p className="break-words"><span className="text-primary-accent font-semibold">02 EXIT:</span> {selectedStrategy.rules.exit}</p>
                  <p className="break-words"><span className="text-accent-warning font-semibold">03 RISK:</span> {selectedStrategy.rules.stopLoss}</p>
                </div>
              </div>

              <div className="pt-4 border-t border-border-subtle flex flex-col sm:flex-row gap-2">
                <Button
                  variant="primary"
                  className="flex-1"
                  onClick={() => handleDecision(selectedStrategy.id, 'approved')}
                >
                  Approve for Signals Feed
                </Button>
                <Button
                  variant="destructive"
                  className="flex-1"
                  onClick={() => handleDecision(selectedStrategy.id, 'rejected')}
                >
                  Reject Strategy
                </Button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};