'use client';

import React from 'react';
import Link from 'next/link';
import { ArrowLeft, ShieldCheck, History } from 'lucide-react';

export default function AdminAuditPage() {
  const auditLogs = [
    {
      id: 'AUDIT-8941',
      admin: 'admin-super-1 (Super Admin)',
      action: 'SUSPEND_PLAYER',
      target: 'USER #p-4 (RuleBreaker)',
      reason: 'Attempting automated botting script on game rounds',
      timestamp: '2026-11-17 12:45:10 UTC',
    },
    {
      id: 'AUDIT-8940',
      admin: 'admin-support-1 (Support Admin)',
      action: 'RESOLVE_TICKET',
      target: 'TICKET #TICK-101',
      reason: 'Explained 24h streak claim window to player',
      timestamp: '2026-11-17 12:30:22 UTC',
    },
    {
      id: 'AUDIT-8939',
      admin: 'admin-ops-1 (Operations Admin)',
      action: 'ENABLE_MAINTENANCE',
      target: 'GAME #rocket-rescue',
      reason: 'Routine database index re-optimization',
      timestamp: '2026-11-17 11:15:00 UTC',
    },
    {
      id: 'AUDIT-8938',
      admin: 'admin-super-1 (Super Admin)',
      action: 'APPROVE_GAME_VERSION',
      target: 'GAME #mines-grid (v1.2.0)',
      reason: 'Passed independent Chi-Square statistical validation',
      timestamp: '2026-11-17 10:00:15 UTC',
    },
  ];

  return (
    <div className="flex-1 p-8 max-w-7xl mx-auto w-full">
      <div className="flex items-center justify-between pb-6 border-b border-gray-800">
        <div>
          <Link
            href="/"
            className="inline-flex items-center gap-2 text-sm text-gray-400 hover:text-white mb-2 transition-colors"
          >
            <ArrowLeft className="h-4 w-4" /> Back to Overview
          </Link>
          <h1 className="text-2xl font-bold tracking-tight text-white flex items-center gap-2">
            <ShieldCheck className="h-6 w-6 text-emerald-400" />
            Immutable Administrative Audit Trail
          </h1>
          <p className="text-sm text-gray-400 mt-1">
            Every privileged administrative action is permanently recorded and tamper-resistant.
          </p>
        </div>
      </div>

      <div className="mt-8 bg-[#121824] border border-gray-800 rounded-xl overflow-hidden shadow-lg">
        <table className="w-full text-left text-sm text-gray-300">
          <thead className="bg-[#1A2234] text-xs uppercase text-gray-400 border-b border-gray-800">
            <tr>
              <th className="px-5 py-3.5">Log ID</th>
              <th className="px-5 py-3.5">Administrator</th>
              <th className="px-5 py-3.5">Action Executed</th>
              <th className="px-5 py-3.5">Target Entity</th>
              <th className="px-5 py-3.5">Details & Reason</th>
              <th className="px-5 py-3.5 text-right">Timestamp (UTC)</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-800">
            {auditLogs.map((log) => (
              <tr key={log.id} className="hover:bg-[#1A2234]/40 transition-colors">
                <td className="px-5 py-4 font-mono text-xs text-amber-400">{log.id}</td>
                <td className="px-5 py-4 font-medium text-white">{log.admin}</td>
                <td className="px-5 py-4">
                  <span className="px-2 py-0.5 text-xs font-mono rounded bg-purple-950 text-purple-300 border border-purple-800">
                    {log.action}
                  </span>
                </td>
                <td className="px-5 py-4 text-cyan-400 font-medium">{log.target}</td>
                <td className="px-5 py-4 text-gray-300 text-xs">{log.reason}</td>
                <td className="px-5 py-4 text-right font-mono text-xs text-gray-400">
                  {log.timestamp}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
