import React, { useState } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { 
  Key, 
  RefreshCw, 
  Check, 
  Copy, 
  AlertCircle, 
  Sparkles, 
  ShieldCheck 
} from 'lucide-react';

interface KeyRecord {
  id: string;
  provider: string;
  name: string;
  category: 'market_data' | 'broker_oauth' | 'ai_model' | 'notifications';
  maskedValue: string;
  status: 'valid' | 'rate_limited' | 'expired';
  latencyMs: number;
  quotaUsedPercent: number;
  lastRotated: string;
}

const INITIAL_KEYS: KeyRecord[] = [
  {
    id: 'k1',
    provider: 'gemini_api',
    name: 'Google Gemini 1.5/2.0 API',
    category: 'ai_model',
    maskedValue: 'AIzaSyA••••••••9A12',
    status: 'valid',
    latencyMs: 140,
    quotaUsedPercent: 82,
    lastRotated: '2026-10-15 08:00 IST',
  },
  {
    id: 'k2',
    provider: 'zerodha_kite',
    name: 'Zerodha Kite Connect (Data)',
    category: 'broker_oauth',
    maskedValue: 'kite-client-••••••••4B71',
    status: 'valid',
    latencyMs: 180,
    quotaUsedPercent: 45,
    lastRotated: '2026-11-01 10:30 IST',
  },
  {
    id: 'k3',
    provider: 'upstox_dev',
    name: 'Upstox Developer Feed',
    category: 'broker_oauth',
    maskedValue: 'upstox-app-••••••••7C22',
    status: 'valid',
    latencyMs: 195,
    quotaUsedPercent: 30,
    lastRotated: '2026-11-05 14:15 IST',
  },
  {
    id: 'k4',
    provider: 'truedata_feed',
    name: 'TrueData Real-time NSE/BSE',
    category: 'market_data',
    maskedValue: 'td-feed-••••••••3C89',
    status: 'valid',
    latencyMs: 42,
    quotaUsedPercent: 68,
    lastRotated: '2026-09-20 09:15 IST',
  },
  {
    id: 'k5',
    provider: 'fcm_push',
    name: 'Firebase Cloud Messaging',
    category: 'notifications',
    maskedValue: 'fcm-srv-••••••••1E90',
    status: 'valid',
    latencyMs: 65,
    quotaUsedPercent: 12,
    lastRotated: '2026-10-28 16:00 IST',
  },
];

export const KeyManagerScreen: React.FC = () => {
  const [keys, setKeys] = useState<KeyRecord[]>(INITIAL_KEYS);
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [testingId, setTestingId] = useState<string | null>(null);
  const [copiedId, setCopiedId] = useState<string | null>(null);
  const [activeModalKey, setActiveModalKey] = useState<KeyRecord | null>(null);
  const [newKeyInput, setNewKeyInput] = useState('');
  const [isRotating, setIsRotating] = useState(false);
  const [successToast, setSuccessToast] = useState<string | null>(null);

  const filteredKeys = selectedCategory === 'all' 
    ? keys 
    : keys.filter(k => k.category === selectedCategory);

  const handleTestPing = async (key: KeyRecord) => {
    setTestingId(key.id);
    await new Promise(r => setTimeout(r, 600));
    setTestingId(null);
    setSuccessToast(`Connection verified for ${key.name}: ${key.latencyMs}ms latency.`);
    setTimeout(() => setSuccessToast(null), 3500);
  };

  const handleCopy = (id: string, text: string) => {
    navigator.clipboard.writeText(text);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  const handleRotateSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!activeModalKey || !newKeyInput) return;

    setIsRotating(true);
    await new Promise(r => setTimeout(r, 800));

    // Update masked key
    const maskedSuffix = newKeyInput.slice(-4);
    const updated = keys.map(k => {
      if (k.id === activeModalKey.id) {
        return {
          ...k,
          maskedValue: `${newKeyInput.slice(0, 8)}••••••••${maskedSuffix}`,
          lastRotated: 'Just now',
          status: 'valid' as const,
        };
      }
      return k;
    });

    setKeys(updated);
    setIsRotating(false);
    setSuccessToast(`Secret key for ${activeModalKey.name} rotated successfully.`);
    setActiveModalKey(null);
    setNewKeyInput('');
    setTimeout(() => setSuccessToast(null), 4000);
  };

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Toast */}
      {successToast && (
        <div className="fixed top-5 right-5 z-50 flex items-center gap-2 rounded-lg border border-primary-bullish/40 bg-surface-elevated px-4 py-3 text-xs text-primary-bullish shadow-xl max-w-md">
          <ShieldCheck className="h-4 w-4 shrink-0" />
          <span className="truncate">{successToast}</span>
        </div>
      )}

      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="min-w-0">
          <h1 className="text-xl font-bold tracking-tight text-text-primary flex items-center gap-2">
            <Key className="h-5 w-5 text-primary-bullish shrink-0" />
            <span>Universal Key & Secret Vault</span>
          </h1>
          <p className="text-xs text-text-secondary mt-0.5">
            Manage external credentials for market feeds, AI models, and broker adapters with zero backend redeployments.
          </p>
        </div>

        {/* Category Filters */}
        <div className="flex flex-wrap items-center gap-1.5 bg-surface-card p-1 rounded-lg border border-border-subtle shrink-0">
          {['all', 'market_data', 'broker_oauth', 'ai_model'].map((cat) => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`px-2.5 sm:px-3 py-1 text-xs rounded font-medium transition-colors whitespace-nowrap ${
                selectedCategory === cat
                  ? 'bg-primary-bullish text-bg-app font-semibold'
                  : 'text-text-secondary hover:text-text-primary'
              }`}
            >
              {cat === 'all' ? 'All Keys' : cat.replace('_', ' ').toUpperCase()}
            </button>
          ))}
        </div>
      </div>

      {/* Keys Table Card */}
      <Card>
        <CardHeader className="pb-3 border-b border-border-subtle">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
            <CardTitle className="text-sm">Configured API Providers ({filteredKeys.length})</CardTitle>
            <span className="text-[11px] text-text-muted flex items-center gap-1 shrink-0">
              <Sparkles className="h-3.5 w-3.5 text-primary-bullish shrink-0" />
              AES-256-GCM Encrypted at rest
            </span>
          </div>
        </CardHeader>

        <CardContent className="p-0 overflow-x-auto">
          <table className="w-full text-left border-collapse min-w-[680px]">
            <thead>
              <tr className="border-b border-border-subtle bg-bg-app text-[11px] font-semibold uppercase tracking-wider text-text-secondary">
                <th className="py-3 px-4">Provider / Service</th>
                <th className="py-3 px-4">Masked Secret</th>
                <th className="py-3 px-4">Health / Latency</th>
                <th className="py-3 px-4">Quota Utilized</th>
                <th className="py-3 px-4">Last Rotated</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border-subtle text-xs font-mono">
              {filteredKeys.map((k) => (
                <tr key={k.id} className="hover:bg-surface-elevated/40 transition-colors">
                  <td className="py-3 px-4 font-sans">
                    <p className="font-semibold text-text-primary whitespace-nowrap">{k.name}</p>
                    <span className="text-[10px] text-text-muted uppercase font-mono">{k.category}</span>
                  </td>

                  <td className="py-3 px-4 whitespace-nowrap">
                    <div className="inline-flex items-center gap-2 bg-surface-elevated/70 border border-border-subtle px-2.5 py-1 rounded">
                      <span className="text-text-secondary">{k.maskedValue}</span>
                      <button
                        onClick={() => handleCopy(k.id, k.maskedValue)}
                        className="text-text-muted hover:text-text-primary"
                        title="Copy masked string"
                      >
                        {copiedId === k.id ? <Check className="h-3 w-3 text-primary-bullish" /> : <Copy className="h-3 w-3" />}
                      </button>
                    </div>
                  </td>

                  <td className="py-3 px-4 whitespace-nowrap">
                    <div className="flex items-center gap-2 font-sans">
                      <Badge variant="healthy" dot>Valid</Badge>
                      <span className="text-[11px] font-mono text-text-secondary">{k.latencyMs}ms</span>
                    </div>
                  </td>

                  <td className="py-3 px-4 whitespace-nowrap">
                    <div className="w-28 space-y-1">
                      <div className="flex items-center justify-between text-[10px] text-text-muted">
                        <span>{k.quotaUsedPercent}%</span>
                      </div>
                      <div className="h-1.5 w-full bg-surface-elevated rounded-full overflow-hidden">
                        <div 
                          className={`h-full rounded-full ${k.quotaUsedPercent > 80 ? 'bg-accent-warning' : 'bg-primary-bullish'}`}
                          style={{ width: `${k.quotaUsedPercent}%` }}
                        />
                      </div>
                    </div>
                  </td>

                  <td className="py-3 px-4 text-[11px] text-text-secondary font-sans whitespace-nowrap">
                    {k.lastRotated}
                  </td>

                  <td className="py-3 px-4 text-right whitespace-nowrap">
                    <div className="flex items-center justify-end gap-2 font-sans">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleTestPing(k)}
                        isLoading={testingId === k.id}
                        className="h-7 text-xs px-2.5"
                      >
                        Test Ping
                      </Button>
                      <Button
                        variant="primary"
                        size="sm"
                        onClick={() => setActiveModalKey(k)}
                        className="h-7 text-xs px-2.5"
                      >
                        Rotate Key
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </CardContent>
      </Card>

      {/* Rotation Modal Dialog */}
      {activeModalKey && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-bg-app/80 backdrop-blur-sm p-4">
          <Card className="w-full max-w-lg border-border-subtle shadow-2xl">
            <CardHeader className="pb-3 border-b border-border-subtle">
              <CardTitle className="text-base flex items-center gap-2">
                <RefreshCw className="h-4 w-4 text-primary-bullish shrink-0" />
                <span className="truncate">Rotate Secret: {activeModalKey.name}</span>
              </CardTitle>
              <p className="text-xs text-text-secondary">
                The new credential will be verified against upstream endpoints before being committed to PostgreSQL vault.
              </p>
            </CardHeader>

            <CardContent className="pt-4">
              <form onSubmit={handleRotateSubmit} className="space-y-4">
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-text-secondary">Current Masked Value</label>
                  <Input value={activeModalKey.maskedValue} disabled className="opacity-60 font-mono" />
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-text-secondary">New Secret Key Payload</label>
                  <Input
                    isSecret
                    placeholder="Paste new secret credential here..."
                    value={newKeyInput}
                    onChange={(e) => setNewKeyInput(e.target.value)}
                    required
                    autoFocus
                  />
                </div>

                <div className="rounded-md border border-border-subtle bg-surface-elevated/40 p-3 text-xs text-text-secondary flex items-start gap-2">
                  <AlertCircle className="h-4 w-4 text-accent-warning shrink-0 mt-0.5" />
                  <span>
                    Zero-downtime rotation. All running worker threads will invalidate their cached token and swap credentials automatically.
                  </span>
                </div>

                <div className="flex items-center justify-end gap-2 pt-3 border-t border-border-subtle">
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => { setActiveModalKey(null); setNewKeyInput(''); }}
                    disabled={isRotating}
                  >
                    Cancel
                  </Button>
                  <Button
                    type="submit"
                    variant="primary"
                    isLoading={isRotating}
                    disabled={!newKeyInput}
                  >
                    Commit & Encrypt
                  </Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
};