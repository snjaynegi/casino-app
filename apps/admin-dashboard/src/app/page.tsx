import React from 'react';
import Link from 'next/link';
import {
  Users,
  Gamepad2,
  Coins,
  ShieldCheck,
  Activity,
  AlertTriangle,
  ArrowRight,
  ClipboardList,
} from 'lucide-react';

export default function AdminDashboardPage() {
  const metrics = [
    {
      title: 'Total Registered Players',
      value: '28,490',
      change: '+12% this week',
      icon: Users,
      color: 'text-cyan-400',
    },
    {
      title: 'Active Game Sessions',
      value: '412',
      change: 'Live across 8 games',
      icon: Gamepad2,
      color: 'text-purple-400',
    },
    {
      title: 'Virtual Coins In Circulation',
      value: '184.2M VCOIN',
      change: 'Append-only ledger verified',
      icon: Coins,
      color: 'text-amber-400',
    },
    {
      title: 'RNG Integrity & Audits',
      value: '100% Valid',
      change: 'Zero tampering flags',
      icon: ShieldCheck,
      color: 'text-emerald-400',
    },
  ];

  const recentGames = [
    {
      name: 'Rocket Rescue',
      category: 'Crash',
      activePlayers: 184,
      rtp: '97.0%',
      status: 'Operational',
    },
    {
      name: 'Frog Frenzy',
      category: 'Arcade',
      activePlayers: 86,
      rtp: '96.5%',
      status: 'Operational',
    },
    {
      name: 'Deep Dive',
      category: 'Arcade',
      activePlayers: 62,
      rtp: '97.2%',
      status: 'Operational',
    },
    {
      name: 'Mines Grid',
      category: 'Instant',
      activePlayers: 80,
      rtp: '98.0%',
      status: 'Operational',
    },
  ];

  return (
    <div className="flex-1 p-8 max-w-7xl mx-auto w-full">
      {/* Top Header */}
      <div className="flex items-center justify-between pb-6 border-b border-gray-800">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white flex items-center gap-2">
            <Activity className="h-6 w-6 text-amber-400" />
            Casino Platform Operations Portal
          </h1>
          <p className="text-sm text-gray-400 mt-1">
            Real-time telemetry, player governance, and Provably Fair audit monitoring.
          </p>
        </div>
        <div className="flex items-center gap-3">
          <span className="px-3 py-1 text-xs font-semibold rounded-full bg-emerald-950 text-emerald-400 border border-emerald-800 flex items-center gap-1.5">
            <span className="h-2 w-2 rounded-full bg-emerald-400 animate-pulse"></span>
            System Healthy
          </span>
          <span className="px-3 py-1 text-xs font-semibold rounded-full bg-purple-950 text-purple-300 border border-purple-800">
            Super Admin
          </span>
        </div>
      </div>

      {/* Navigation Quick Links */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-6">
        <Link
          href="/players"
          className="p-4 bg-[#121824] hover:bg-[#1A2234] border border-gray-800 rounded-xl transition-all flex items-center justify-between group"
        >
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-lg bg-cyan-950/60 text-cyan-400 border border-cyan-800">
              <Users className="h-5 w-5" />
            </div>
            <div>
              <h3 className="font-semibold text-white text-sm">Player Governance</h3>
              <p className="text-xs text-gray-400">Search, status, and suspensions</p>
            </div>
          </div>
          <ArrowRight className="h-4 w-4 text-gray-500 group-hover:text-white transition-colors" />
        </Link>

        <Link
          href="/games"
          className="p-4 bg-[#121824] hover:bg-[#1A2234] border border-gray-800 rounded-xl transition-all flex items-center justify-between group"
        >
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-lg bg-purple-950/60 text-purple-400 border border-purple-800">
              <Gamepad2 className="h-5 w-5" />
            </div>
            <div>
              <h3 className="font-semibold text-white text-sm">Games Management</h3>
              <p className="text-xs text-gray-400">RTP & maintenance controls</p>
            </div>
          </div>
          <ArrowRight className="h-4 w-4 text-gray-500 group-hover:text-white transition-colors" />
        </Link>

        <Link
          href="/audit"
          className="p-4 bg-[#121824] hover:bg-[#1A2234] border border-gray-800 rounded-xl transition-all flex items-center justify-between group"
        >
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-lg bg-emerald-950/60 text-emerald-400 border border-emerald-800">
              <ClipboardList className="h-5 w-5" />
            </div>
            <div>
              <h3 className="font-semibold text-white text-sm">Audit Trail</h3>
              <p className="text-xs text-gray-400">Immutable administrative logs</p>
            </div>
          </div>
          <ArrowRight className="h-4 w-4 text-gray-500 group-hover:text-white transition-colors" />
        </Link>
      </div>

      {/* Metrics Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mt-6">
        {metrics.map((m, idx) => {
          const Icon = m.icon;
          return (
            <div
              key={idx}
              className="bg-[#121824] border border-gray-800/80 rounded-xl p-5 shadow-lg"
            >
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-400 font-medium">{m.title}</span>
                <Icon className={`h-5 w-5 ${m.color}`} />
              </div>
              <div className="mt-3 text-2xl font-bold text-white">{m.value}</div>
              <div className="mt-1 text-xs text-gray-400">{m.change}</div>
            </div>
          );
        })}
      </div>

      {/* Active Games Table */}
      <div className="mt-8 bg-[#121824] border border-gray-800/80 rounded-xl p-6 shadow-lg">
        <h2 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
          <Gamepad2 className="h-5 w-5 text-purple-400" />
          Active Game Modules Telemetry
        </h2>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm text-gray-300">
            <thead className="bg-[#1A2234] text-xs uppercase text-gray-400 border-b border-gray-800">
              <tr>
                <th className="px-4 py-3">Game Title</th>
                <th className="px-4 py-3">Category</th>
                <th className="px-4 py-3">Active Players</th>
                <th className="px-4 py-3">Target RTP</th>
                <th className="px-4 py-3">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-800">
              {recentGames.map((g, i) => (
                <tr key={i} className="hover:bg-[#1A2234]/50 transition-colors">
                  <td className="px-4 py-3 font-medium text-white">{g.name}</td>
                  <td className="px-4 py-3 text-gray-400">{g.category}</td>
                  <td className="px-4 py-3 text-cyan-400 font-semibold">{g.activePlayers}</td>
                  <td className="px-4 py-3 text-amber-400">{g.rtp}</td>
                  <td className="px-4 py-3">
                    <span className="px-2 py-0.5 text-xs rounded bg-emerald-900/60 text-emerald-300 border border-emerald-700/50">
                      {g.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Compliance & Restriction Banner */}
      <div className="mt-8 p-4 bg-amber-950/30 border border-amber-800/60 rounded-xl flex items-start gap-3">
        <AlertTriangle className="h-5 w-5 text-amber-400 flex-shrink-0 mt-0.5" />
        <div>
          <h4 className="text-sm font-semibold text-amber-300">
            Non-Monetary Operating Compliance Enforcement
          </h4>
          <p className="text-xs text-amber-200/80 mt-0.5 leading-relaxed">
            All game sessions use non-redeemable virtual entertainment currency (VCOIN). Real-money
            betting, cash withdrawals, and peer-to-peer transfers are strictly disabled in
            accordance with jurisdictional compliance policies.
          </p>
        </div>
      </div>
    </div>
  );
}
