import React, { useState } from 'react';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { 
  FileText, 
  Search, 
  ChevronDown, 
  ChevronRight 
} from 'lucide-react';

interface AuditEvent {
  id: string;
  eventHash: string;
  actorEmail: string;
  actorRole: string;
  actionCategory: 'key_rotation' | 'kill_switch' | 'strategy_approval' | 'feature_flag';
  targetResource: string;
  ipAddress: string;
  timestamp: string;
  stateBefore: Record<string, unknown>;
  stateAfter: Record<string, unknown>;
}

const AUDIT_LOGS: AuditEvent[] = [
  {
    id: '1',
    eventHash: 'EVT_10484_e3b0c44298fc1c14',
    actorEmail: 'ops.admin@boldtrading.internal',
    actorRole: 'operations_admin',
    actionCategory: 'key_rotation',
    targetResource: 'api_keys_vault:gemini_api',
    ipAddress: '10.0.0.24',
    timestamp: '14 Nov 2026, 12:05:00 IST',
    stateBefore: { provider: 'gemini_api', status: 'valid', quotaUsedPercent: 82 },
    stateAfter: { provider: 'gemini_api', status: 'valid', quotaUsedPercent: 0, lastRotated: '2026-11-14T06:35:00Z' },
  },
  {
    id: '2',
    eventHash: 'EVT_10483_8f434346648f6b96',
    actorEmail: 'compliance.lead@boldtrading.internal',
    actorRole: 'compliance_lead',
    actionCategory: 'strategy_approval',
    targetResource: 'strategy_reviews:VWAP_reclaim_v2.1',
    ipAddress: '10.0.0.32',
    timestamp: '14 Nov 2026, 11:30:15 IST',
    stateBefore: { status: 'pending', reviewedBy: null },
    stateAfter: { status: 'approved', reviewedBy: 'compliance.lead@boldtrading.internal' },
  },
  {
    id: '3',
    eventHash: 'EVT_10482_f1287c934b9d8821',
    actorEmail: 'ops.admin@boldtrading.internal',
    actorRole: 'operations_admin',
    actionCategory: 'feature_flag',
    targetResource: 'feature_flags:GLOBAL_FREE_TIER_OVERRIDE',
    ipAddress: '10.0.0.24',
    timestamp: '14 Nov 2026, 09:38:00 IST',
    stateBefore: { isEnabled: true },
    stateAfter: { isEnabled: true, verifiedForLaunch: true },
  },
  {
    id: '4',
    eventHash: 'EVT_10481_2d7116487e0b5f12',
    actorEmail: 'system',
    actorRole: 'automated_watcher',
    actionCategory: 'kill_switch',
    targetResource: 'adapter_status:NSE_P2_ALERT',
    ipAddress: '127.0.0.1',
    timestamp: '14 Nov 2026, 09:36:00 IST',
    stateBefore: { latency: 210, status: 'healthy' },
    stateAfter: { latency: 680, status: 'degraded' },
  },
];

export const AuditTrailScreen: React.FC = () => {
  const [search, setSearch] = useState('');
  const [expandedId, setExpandedId] = useState<string | null>(null);

  const filtered = AUDIT_LOGS.filter(log => 
    log.eventHash.toLowerCase().includes(search.toLowerCase()) ||
    log.actorEmail.toLowerCase().includes(search.toLowerCase()) ||
    log.ipAddress.includes(search) ||
    log.targetResource.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="min-w-0">
          <h1 className="text-xl font-bold tracking-tight text-text-primary flex items-center gap-2">
            <FileText className="h-5 w-5 text-primary-bullish shrink-0" />
            <span>Tamper-Evident Audit Trail</span>
          </h1>
          <p className="text-xs text-text-secondary mt-0.5">
            Immutable SHA-256 cryptographic trail recording all key rotations, strategy decisions, and permission updates.
          </p>
        </div>

        <div className="relative w-full md:w-72 shrink-0">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-text-muted" />
          <Input
            placeholder="Search hash, email, or IP..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-9 text-xs"
          />
        </div>
      </div>

      <Card>
        <CardContent className="p-0 overflow-x-auto">
          <table className="w-full text-left border-collapse min-w-[720px]">
            <thead>
              <tr className="border-b border-border-subtle bg-bg-app text-[11px] font-semibold uppercase tracking-wider text-text-secondary">
                <th className="py-3 px-4 w-8"></th>
                <th className="py-3 px-4">Event Hash ID</th>
                <th className="py-3 px-4">Actor Email</th>
                <th className="py-3 px-4">Action</th>
                <th className="py-3 px-4">Target Resource</th>
                <th className="py-3 px-4">IP Address</th>
                <th className="py-3 px-4 text-right">Timestamp (IST)</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border-subtle text-xs font-mono">
              {filtered.map((log) => {
                const isExpanded = expandedId === log.id;
                return (
                  <React.Fragment key={log.id}>
                    <tr 
                      onClick={() => setExpandedId(isExpanded ? null : log.id)}
                      className="hover:bg-surface-elevated/40 transition-colors cursor-pointer"
                    >
                      <td className="py-3 px-4 text-text-muted">
                        {isExpanded ? <ChevronDown className="h-3.5 w-3.5" /> : <ChevronRight className="h-3.5 w-3.5" />}
                      </td>
                      <td className="py-3 px-4 font-semibold text-primary-bullish whitespace-nowrap">
                        {log.eventHash}
                      </td>
                      <td className="py-3 px-4 font-sans text-text-primary whitespace-nowrap">
                        {log.actorEmail}
                      </td>
                      <td className="py-3 px-4 whitespace-nowrap">
                        <Badge variant="neutral">{log.actionCategory.replace('_', ' ')}</Badge>
                      </td>
                      <td className="py-3 px-4 text-text-secondary whitespace-nowrap">
                        {log.targetResource}
                      </td>
                      <td className="py-3 px-4 text-text-muted whitespace-nowrap">
                        {log.ipAddress}
                      </td>
                      <td className="py-3 px-4 text-right text-text-secondary font-sans whitespace-nowrap">
                        {log.timestamp}
                      </td>
                    </tr>

                    {/* Expandable JSON State Diffs */}
                    {isExpanded && (
                      <tr className="bg-surface-elevated/20">
                        <td colSpan={7} className="p-4 border-t border-border-subtle/50">
                          <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                            <div className="rounded border border-border-subtle bg-surface-card p-3 min-w-0">
                              <span className="text-[10px] font-semibold uppercase tracking-wider text-text-muted block mb-1">State Before Mutation</span>
                              <pre className="text-[11px] text-text-secondary overflow-x-auto whitespace-pre-wrap break-all p-2 bg-surface-elevated/50 rounded">
                                {JSON.stringify(log.stateBefore, null, 2)}
                              </pre>
                            </div>
                            <div className="rounded border border-border-subtle bg-surface-card p-3 min-w-0">
                              <span className="text-[10px] font-semibold uppercase tracking-wider text-primary-bullish block mb-1">State After Mutation</span>
                              <pre className="text-[11px] text-primary-bullish overflow-x-auto whitespace-pre-wrap break-all p-2 bg-surface-elevated/50 rounded">
                                {JSON.stringify(log.stateAfter, null, 2)}
                              </pre>
                            </div>
                          </div>
                        </td>
                      </tr>
                    )}
                  </React.Fragment>
                );
              })}
            </tbody>
          </table>
        </CardContent>
      </Card>
    </div>
  );
};