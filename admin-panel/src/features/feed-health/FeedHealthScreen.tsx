import React, { useState } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { 
  Radio, 
  RefreshCw 
} from 'lucide-react';

interface BrokerAdapter {
  id: string;
  name: string;
  type: string;
  status: 'healthy' | 'degraded' | 'offline';
  latencyMs: number;
  lastSync: string;
  tokenExpiry: string;
  activeSubscriptions: number;
}

const ADAPTERS: BrokerAdapter[] = [
  {
    id: 'b1',
    name: 'Zerodha Kite Connect',
    type: 'REST + WebSocket Market Feed',
    status: 'healthy',
    latencyMs: 142,
    lastSync: '09:41:00 IST',
    tokenExpiry: '14h 18m remaining',
    activeSubscriptions: 1420,
  },
  {
    id: 'b2',
    name: 'Upstox Developer API',
    type: 'Account Sync & Historical Candles',
    status: 'healthy',
    latencyMs: 185,
    lastSync: '09:40:45 IST',
    tokenExpiry: '16h 02m remaining',
    activeSubscriptions: 850,
  },
  {
    id: 'b3',
    name: 'Angel One SmartAPI',
    type: 'Quote API & Greeks Calculation',
    status: 'degraded',
    latencyMs: 680,
    lastSync: '09:36:12 IST',
    tokenExpiry: '8h 45m remaining',
    activeSubscriptions: 610,
  },
  {
    id: 'b4',
    name: 'Dhan HQ API',
    type: 'Market Data & Instrument Master',
    status: 'healthy',
    latencyMs: 120,
    lastSync: '09:41:02 IST',
    tokenExpiry: '22h 10m remaining',
    activeSubscriptions: 430,
  },
  {
    id: 'b5',
    name: 'Fyers API v3',
    type: 'Data & Symbol Quotes',
    status: 'healthy',
    latencyMs: 155,
    lastSync: '09:40:55 IST',
    tokenExpiry: '19h 30m remaining',
    activeSubscriptions: 390,
  },
  {
    id: 'b6',
    name: '5paisa Developer API',
    type: 'Account Holdings Sync Only',
    status: 'healthy',
    latencyMs: 210,
    lastSync: '09:39:20 IST',
    tokenExpiry: '11h 15m remaining',
    activeSubscriptions: 190,
  },
];

export const FeedHealthScreen: React.FC = () => {
  const [adapters, setAdapters] = useState<BrokerAdapter[]>(ADAPTERS);
  const [syncingId, setSyncingId] = useState<string | null>(null);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const handleSync = async (id: string) => {
    setSyncingId(id);
    await new Promise(r => setTimeout(r, 600));

    const now = new Intl.DateTimeFormat('en-IN', {
      timeZone: 'Asia/Kolkata',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false,
    }).format(new Date());

    setAdapters(prev => prev.map(adapter => {
      if (adapter.id === id) {
        return {
          ...adapter,
          status: 'healthy',
          latencyMs: 135,
          lastSync: `${now} IST`,
          tokenExpiry: '23h 59m remaining',
        };
      }
      return adapter;
    }));

    const target = adapters.find(a => a.id === id);
    setToastMessage(`Adapter '${target?.name}' session refreshed: status HEALTHY, latency 135ms.`);
    setSyncingId(null);
    setTimeout(() => setToastMessage(null), 4000);
  };

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Success Toast */}
      {toastMessage && (
        <div className="fixed top-5 right-5 z-50 flex items-center gap-2 rounded-lg border border-primary-bullish/40 bg-surface-elevated px-4 py-3 text-xs text-primary-bullish shadow-xl max-w-md animate-in fade-in">
          <RefreshCw className="h-4 w-4 shrink-0 text-primary-bullish animate-spin" />
          <span className="truncate">{toastMessage}</span>
        </div>
      )}

      <div>
        <h1 className="text-xl font-bold tracking-tight text-text-primary flex items-center gap-2">
          <Radio className="h-5 w-5 text-primary-bullish shrink-0" />
          <span>Broker Adapters & Market Data Telemetry</span>
        </h1>
        <p className="text-xs text-text-secondary mt-0.5">
          Monitor connection health, latency benchmarks, and OAuth token expirations across normalized Indian broker feeds.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
        {adapters.map((adapter) => (
          <Card key={adapter.id} className="hover:border-primary-bullish/50 transition-colors">
            <CardHeader className="pb-3 border-b border-border-subtle flex flex-row items-center justify-between gap-2">
              <div className="min-w-0">
                <CardTitle className="text-sm truncate">{adapter.name}</CardTitle>
                <p className="text-[11px] text-text-muted mt-0.5 truncate">{adapter.type}</p>
              </div>
              <Badge variant={adapter.status === 'healthy' ? 'healthy' : 'degraded'} dot>
                {adapter.status}
              </Badge>
            </CardHeader>

            <CardContent className="pt-4 space-y-3">
              <div className="flex items-center justify-between text-xs gap-2">
                <span className="text-text-secondary truncate">Latency</span>
                <span className={`font-mono font-semibold shrink-0 ${adapter.latencyMs > 500 ? 'text-accent-warning' : 'text-primary-bullish'}`}>
                  {adapter.latencyMs} ms
                </span>
              </div>

              <div className="flex items-center justify-between text-xs gap-2">
                <span className="text-text-secondary truncate">Active Subscriptions</span>
                <span className="font-mono text-text-primary shrink-0">{adapter.activeSubscriptions.toLocaleString()} ticks</span>
              </div>

              <div className="flex items-center justify-between text-xs gap-2">
                <span className="text-text-secondary truncate">Token Expiration</span>
                <span className="font-mono text-text-muted shrink-0">{adapter.tokenExpiry}</span>
              </div>

              <div className="flex items-center justify-between text-xs gap-2">
                <span className="text-text-secondary truncate">Last Sync</span>
                <span className="font-mono text-text-muted shrink-0">{adapter.lastSync}</span>
              </div>

              <div className="pt-2 border-t border-border-subtle flex gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  className="flex-1 text-xs"
                  onClick={() => handleSync(adapter.id)}
                  isLoading={syncingId === adapter.id}
                >
                  <RefreshCw className="h-3 w-3 mr-1 shrink-0" /> Reconnect
                </Button>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
};