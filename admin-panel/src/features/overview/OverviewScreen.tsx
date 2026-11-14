import React from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { 
  Users, 
  CreditCard, 
  Radio, 
  AlertTriangle, 
  ArrowUpRight, 
  ShieldAlert, 
  CheckCircle2, 
  ChevronRight,
  TrendingUp,
  BarChart2
} from 'lucide-react';
import { Link } from 'react-router-dom';

export const OverviewScreen: React.FC = () => {
  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Top Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="min-w-0">
          <h1 className="text-xl font-bold tracking-tight text-text-primary">Platform Overview</h1>
          <p className="text-xs text-text-secondary mt-0.5">
            Monitor infrastructure reliability, broker adapter health, and governance gates.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-2 shrink-0">
          <Link to="/terminal">
            <Button variant="primary" size="sm" className="text-xs shadow-glow-green font-bold">
              <TrendingUp className="h-3.5 w-3.5 mr-1.5" />
              Live Trading Terminal
            </Button>
          </Link>
          <Link to="/governance">
            <Button variant="outline" size="sm" className="text-xs border-accent-bearish/40 text-accent-bearish hover:bg-accent-bearish/10">
              <ShieldAlert className="h-3.5 w-3.5 mr-1.5" />
              Emergency Controls
            </Button>
          </Link>
          <Link to="/keys">
            <Button variant="outline" size="sm" className="text-xs">
              Manage Keys
            </Button>
          </Link>
        </div>
      </div>

      {/* 4 Metric Summary Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
        {/* Total Users */}
        <Card className="hover:border-primary-bullish/50 transition-colors">
          <div className="flex items-center justify-between pb-2">
            <span className="text-xs font-medium text-text-secondary">Total Users</span>
            <Users className="h-4 w-4 text-primary-bullish shrink-0" />
          </div>
          <div className="text-2xl font-bold font-mono text-text-primary">24,860</div>
          <div className="flex flex-wrap items-center gap-1.5 mt-2">
            <span className="text-[11px] font-semibold text-primary-bullish flex items-center">
              <ArrowUpRight className="h-3 w-3 mr-0.5" /> +8.4%
            </span>
            <span className="text-[11px] text-text-muted">this month</span>
          </div>
        </Card>

        {/* Active Subscriptions */}
        <Card className="hover:border-primary-bullish/50 transition-colors">
          <div className="flex items-center justify-between pb-2">
            <span className="text-xs font-medium text-text-secondary">Active Subscriptions</span>
            <CreditCard className="h-4 w-4 text-primary-accent shrink-0" />
          </div>
          <div className="text-2xl font-bold font-mono text-text-primary">4,218</div>
          <div className="flex flex-wrap items-center gap-1.5 mt-2">
            <span className="text-[11px] text-text-secondary">Pro 3,206 · Premium 1,012</span>
            <span className="rounded bg-primary-bullish/10 text-primary-bullish text-[10px] px-1 font-semibold whitespace-nowrap">
              100% Free
            </span>
          </div>
        </Card>

        {/* Feed Health */}
        <Card className="border-accent-warning/40 bg-surface-card hover:border-accent-warning transition-colors">
          <div className="flex items-center justify-between pb-2">
            <span className="text-xs font-medium text-text-secondary">Feed Health</span>
            <Radio className="h-4 w-4 text-accent-warning animate-pulse shrink-0" />
          </div>
          <div className="flex items-center justify-between gap-2">
            <div className="text-2xl font-bold font-mono text-accent-warning">Degraded</div>
            <Badge variant="degraded">2 Issues</Badge>
          </div>
          <p className="text-[11px] text-text-muted mt-2 truncate">
            NSE latency elevated · BSE snapshot cache stale
          </p>
        </Card>

        {/* API Error Rate */}
        <Card className="hover:border-primary-bullish/50 transition-colors">
          <div className="flex items-center justify-between pb-2">
            <span className="text-xs font-medium text-text-secondary">API Error Rate</span>
            <CheckCircle2 className="h-4 w-4 text-primary-bullish shrink-0" />
          </div>
          <div className="text-2xl font-bold font-mono text-text-primary">0.42%</div>
          <div className="flex items-center gap-2 mt-2">
            <div className="w-full bg-surface-elevated rounded-full h-1.5 overflow-hidden">
              <div className="bg-primary-bullish h-full rounded-full" style={{ width: '42%' }}></div>
            </div>
            <span className="text-[11px] text-text-muted font-mono whitespace-nowrap shrink-0">&lt; 0.50%</span>
          </div>
        </Card>
      </div>

      {/* Main 2-Column Operational Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column (2 Cols): Feed Latency Chart & Active Incidents */}
        <div className="lg:col-span-2 space-y-6 min-w-0">
          {/* Feed Latency Visualizer */}
          <Card>
            <CardHeader className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 pb-2">
              <div className="min-w-0">
                <CardTitle className="text-sm flex items-center gap-2">
                  <BarChart2 className="h-4 w-4 text-primary-bullish" />
                  <span>Market Data Feed Latency</span>
                </CardTitle>
                <p className="text-xs text-text-secondary mt-0.5 truncate sm:whitespace-normal">Mock adapter round-trip samples over past 60 minutes</p>
              </div>
              <div className="flex items-center gap-2">
                <Link to="/terminal" className="text-xs text-primary-bullish hover:underline font-semibold flex items-center gap-1">
                  Trading Terminal <ChevronRight className="h-3.5 w-3.5" />
                </Link>
                <Badge variant="neutral">60 min window</Badge>
              </div>
            </CardHeader>
            <CardContent>
              {/* Simulated Latency Bars with overflow protection */}
              <div className="overflow-x-auto pb-1">
                <div className="min-w-[320px]">
                  <div className="h-44 flex items-end gap-1 sm:gap-1.5 pt-6 pb-2 border-b border-border-subtle">
                    {[
                      180, 190, 210, 195, 220, 240, 210, 190, 185, 230,
                      260, 240, 310, 290, 320, 280, 240, 210, 250, 380,
                      420, 580, 690, 780, 720, 640, 520, 410, 320, 280
                    ].map((latency, idx) => {
                      const heightPercent = Math.min(100, Math.max(8, (latency / 800) * 100));
                      const isDegraded = latency > 500;
                      return (
                        <div
                          key={idx}
                          className="flex-1 h-full flex flex-col justify-end group relative cursor-pointer"
                        >
                          {/* Tooltip */}
                          <div className="absolute -top-8 left-1/2 -translate-x-1/2 hidden group-hover:flex items-center px-2 py-0.5 rounded bg-surface-elevated border border-border-subtle text-[10px] font-mono text-text-primary whitespace-nowrap z-10 shadow-lg">
                            {latency} ms
                          </div>
                          <div
                            className={`w-full rounded-t transition-all ${
                              isDegraded 
                                ? 'bg-accent-warning hover:bg-[#FFD97D]' 
                                : 'bg-primary-bullish/70 hover:bg-primary-bullish'
                            }`}
                            style={{ height: `${heightPercent}%`, minHeight: '6px' }}
                          />
                        </div>
                      );
                    })}
                  </div>

                  <div className="flex items-center justify-between text-[11px] font-mono text-text-muted pt-2">
                    <span>09:00 IST</span>
                    <span className="text-accent-warning font-semibold">▲ P2 Spike (780ms)</span>
                    <span>09:41 IST</span>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Active Incidents / Needs Attention */}
          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-3 gap-2">
              <CardTitle className="text-sm flex items-center gap-2 min-w-0">
                <AlertTriangle className="h-4 w-4 text-accent-warning shrink-0" />
                <span className="truncate">Active Adapter Incidents</span>
              </CardTitle>
              <Link to="/feed-health" className="text-xs text-primary-bullish hover:underline flex items-center shrink-0">
                Incident Center <ChevronRight className="h-3.5 w-3.5" />
              </Link>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between p-3 rounded-lg border border-border-subtle bg-surface-elevated/40 gap-3">
                <div className="flex items-start gap-3 min-w-0">
                  <span className="shrink-0 rounded bg-accent-warning/10 border border-accent-warning/30 px-1.5 py-0.5 text-[10px] font-bold text-accent-warning font-mono">
                    P2
                  </span>
                  <div className="min-w-0">
                    <p className="text-xs font-semibold text-text-primary truncate">NSE Adapter Elevated Latency</p>
                    <p className="text-[11px] text-text-secondary truncate sm:whitespace-normal">Current sample 680ms (Threshold: 500ms) · Ops team investigating</p>
                  </div>
                </div>
                <Badge variant="degraded" className="self-start sm:self-center">Acknowledged</Badge>
              </div>

              <div className="flex flex-col sm:flex-row sm:items-center justify-between p-3 rounded-lg border border-border-subtle bg-surface-elevated/40 gap-3">
                <div className="flex items-start gap-3 min-w-0">
                  <span className="shrink-0 rounded bg-accent-bearish/10 border border-accent-bearish/30 px-1.5 py-0.5 text-[10px] font-bold text-accent-bearish font-mono">
                    P3
                  </span>
                  <div className="min-w-0">
                    <p className="text-xs font-semibold text-text-primary truncate">BSE Cache Stale Snapshot</p>
                    <p className="text-[11px] text-text-secondary truncate sm:whitespace-normal">Snapshot age &gt; 120s · Redis worker fallback triggered</p>
                  </div>
                </div>
                <Badge variant="danger" className="self-start sm:self-center">Investigating</Badge>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Right Column (1 Col): Strategy Review Queue & Governance */}
        <div className="space-y-6 min-w-0">
          {/* Strategy Queue */}
          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-3 gap-2">
              <CardTitle className="text-sm truncate">Strategy Review Queue</CardTitle>
              <Link to="/strategies" className="text-xs text-primary-bullish hover:underline shrink-0">
                View All (3)
              </Link>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="p-3 rounded-lg border border-border-subtle bg-surface-elevated/40 space-y-2">
                <div className="flex items-center justify-between gap-2">
                  <span className="text-xs font-semibold text-text-primary truncate">NIFTY momentum v1.4</span>
                  <Badge variant="degraded">Pending</Badge>
                </div>
                <div className="flex items-center justify-between text-[11px] text-text-secondary">
                  <span className="truncate">Author: Quant Team</span>
                  <span className="font-mono text-primary-bullish shrink-0">Win 58.6%</span>
                </div>
              </div>

              <div className="p-3 rounded-lg border border-border-subtle bg-surface-elevated/40 space-y-2">
                <div className="flex items-center justify-between gap-2">
                  <span className="text-xs font-semibold text-text-primary truncate">VWAP reclaim v2.1</span>
                  <Badge variant="healthy">Approved</Badge>
                </div>
                <div className="flex items-center justify-between text-[11px] text-text-secondary">
                  <span className="truncate">Author: Research</span>
                  <span className="font-mono text-primary-bullish shrink-0">Win 62.1%</span>
                </div>
              </div>

              <div className="p-3 rounded-lg border border-border-subtle bg-surface-elevated/40 space-y-2">
                <div className="flex items-center justify-between gap-2">
                  <span className="text-xs font-semibold text-text-primary truncate">Bank spread v0.8</span>
                  <Badge variant="danger">Changes</Badge>
                </div>
                <div className="flex items-center justify-between text-[11px] text-text-secondary">
                  <span className="truncate">Author: Quant Team</span>
                  <span className="font-mono text-accent-bearish shrink-0">Max DD 14.2%</span>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Research Governance & Kill Switch Status */}
          <Card>
            <CardHeader className="pb-3">
              <CardTitle className="text-sm">Governance & Compliance</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="flex items-center justify-between text-xs gap-2">
                <span className="text-text-secondary truncate">Verified Source Templates</span>
                <span className="font-mono font-semibold text-text-primary shrink-0">18 / 20</span>
              </div>
              <div className="flex items-center justify-between text-xs gap-2">
                <span className="text-text-secondary truncate">Mandatory Disclosures</span>
                <span className="text-primary-bullish font-semibold shrink-0">All Current</span>
              </div>
              <div className="flex items-center justify-between text-xs gap-2">
                <span className="text-text-secondary truncate">Live Order Feature Flag</span>
                <span className="text-accent-bearish font-mono font-bold bg-accent-bearish/10 px-1.5 py-0.5 rounded shrink-0 whitespace-nowrap">
                  OFF LOCKED
                </span>
              </div>
            </CardContent>
          </Card>

          {/* Mini Audit Log Preview */}
          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-2 gap-2">
              <CardTitle className="text-sm truncate">Recent Audit Events</CardTitle>
              <Link to="/audit" className="text-xs text-text-muted hover:text-text-primary shrink-0">
                Full Log →
              </Link>
            </CardHeader>
            <CardContent className="space-y-2 text-[11px] font-mono text-text-secondary">
              <div className="truncate border-b border-border-subtle/50 pb-1.5">
                <span className="text-primary-bullish">09:38</span> · ops.admin updated stale threshold · EVT_10482
              </div>
              <div className="truncate border-b border-border-subtle/50 pb-1.5">
                <span className="text-primary-bullish">09:31</span> · compliance.lead approved disclosure v1.2 · EVT_10481
              </div>
              <div className="truncate">
                <span className="text-primary-bullish">09:26</span> · system disabled unavailable quote adapter · EVT_10480
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};