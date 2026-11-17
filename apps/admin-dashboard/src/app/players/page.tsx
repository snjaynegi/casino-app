'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { ArrowLeft, Search, ShieldAlert, UserCheck } from 'lucide-react';

interface PlayerRow {
  id: string;
  username: string;
  email: string;
  status: 'ACTIVE' | 'SUSPENDED';
  level: number;
  balance: number;
}

export default function AdminPlayersPage() {
  const [search, setSearch] = useState('');
  const [players, setPlayers] = useState<PlayerRow[]>([
    {
      id: 'p-1',
      username: 'StarPilot',
      email: 'star@casino.test',
      status: 'ACTIVE',
      level: 14,
      balance: 845000,
    },
    {
      id: 'p-2',
      username: 'AbyssDiver',
      email: 'diver@casino.test',
      status: 'ACTIVE',
      level: 9,
      balance: 620000,
    },
    {
      id: 'p-3',
      username: 'NeonFrog',
      email: 'frog@casino.test',
      status: 'ACTIVE',
      level: 6,
      balance: 489000,
    },
    {
      id: 'p-4',
      username: 'RuleBreaker',
      email: 'bad@actor.test',
      status: 'SUSPENDED',
      level: 1,
      balance: 50,
    },
  ]);

  const toggleStatus = (id: string) => {
    setPlayers((prev) =>
      prev.map((p) => {
        if (p.id === id) {
          const nextStatus = p.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE';
          return { ...p, status: nextStatus };
        }
        return p;
      }),
    );
  };

  const filtered = players.filter(
    (p) =>
      p.username.toLowerCase().includes(search.toLowerCase()) ||
      p.email.toLowerCase().includes(search.toLowerCase()),
  );

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
            Player Account Governance
          </h1>
          <p className="text-sm text-gray-400 mt-1">
            Search, inspect activity history, and manage policy enforcement status.
          </p>
        </div>
      </div>

      {/* Search Input */}
      <div className="mt-6 max-w-md relative">
        <Search className="h-4 w-4 text-gray-400 absolute left-3 top-3.5" />
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search player username or email..."
          className="w-full pl-9 pr-4 py-2.5 bg-[#121824] border border-gray-800 rounded-lg text-sm text-white placeholder-gray-500 focus:outline-none focus:border-amber-400"
        />
      </div>

      {/* Table */}
      <div className="mt-6 bg-[#121824] border border-gray-800 rounded-xl overflow-hidden shadow-lg">
        <table className="w-full text-left text-sm text-gray-300">
          <thead className="bg-[#1A2234] text-xs uppercase text-gray-400 border-b border-gray-800">
            <tr>
              <th className="px-5 py-3.5">Player Username</th>
              <th className="px-5 py-3.5">Email</th>
              <th className="px-5 py-3.5">Level</th>
              <th className="px-5 py-3.5">Virtual Balance</th>
              <th className="px-5 py-3.5">Status</th>
              <th className="px-5 py-3.5 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-800">
            {filtered.map((player) => (
              <tr key={player.id} className="hover:bg-[#1A2234]/40 transition-colors">
                <td className="px-5 py-4 font-semibold text-white">{player.username}</td>
                <td className="px-5 py-4 text-gray-400">{player.email}</td>
                <td className="px-5 py-4 text-cyan-400 font-medium">Lv {player.level}</td>
                <td className="px-5 py-4 font-bold text-amber-400">
                  {player.balance.toLocaleString()} VCOIN
                </td>
                <td className="px-5 py-4">
                  <span
                    className={`px-2.5 py-1 text-xs rounded-full font-semibold border ${
                      player.status === 'ACTIVE'
                        ? 'bg-emerald-950 text-emerald-300 border-emerald-800'
                        : 'bg-red-950 text-red-300 border-red-800'
                    }`}
                  >
                    {player.status}
                  </span>
                </td>
                <td className="px-5 py-4 text-right">
                  <button
                    onClick={() => toggleStatus(player.id)}
                    className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded text-xs font-semibold transition-colors ${
                      player.status === 'ACTIVE'
                        ? 'bg-red-900/40 text-red-300 hover:bg-red-800/60 border border-red-700/50'
                        : 'bg-emerald-900/40 text-emerald-300 hover:bg-emerald-800/60 border border-emerald-700/50'
                    }`}
                  >
                    {player.status === 'ACTIVE' ? (
                      <>
                        <ShieldAlert className="h-3.5 w-3.5" /> Suspend
                      </>
                    ) : (
                      <>
                        <UserCheck className="h-3.5 w-3.5" /> Reactivate
                      </>
                    )}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
