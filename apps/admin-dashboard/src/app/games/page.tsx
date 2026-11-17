'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { ArrowLeft, Gamepad2, Power } from 'lucide-react';

interface GameRow {
  id: string;
  name: string;
  category: string;
  rtp: string;
  activePlayers: number;
  isMaintenance: boolean;
}

export default function AdminGamesPage() {
  const [games, setGames] = useState<GameRow[]>([
    {
      id: 'rocket-rescue',
      name: 'Rocket Rescue',
      category: 'Crash',
      rtp: '97.0%',
      activePlayers: 412,
      isMaintenance: false,
    },
    {
      id: 'frog-frenzy',
      name: 'Frog Frenzy',
      category: 'Arcade',
      rtp: '96.5%',
      activePlayers: 189,
      isMaintenance: false,
    },
    {
      id: 'deep-dive',
      name: 'Deep Dive',
      category: 'Arcade',
      rtp: '97.2%',
      activePlayers: 143,
      isMaintenance: false,
    },
    {
      id: 'sky-elevator',
      name: 'Sky Elevator',
      category: 'Arcade',
      rtp: '96.8%',
      activePlayers: 98,
      isMaintenance: false,
    },
    {
      id: 'neon-bounce',
      name: 'Neon Bounce',
      category: 'Arcade',
      rtp: '97.5%',
      activePlayers: 112,
      isMaintenance: false,
    },
    {
      id: 'mines-grid',
      name: 'Mines Grid',
      category: 'Instant',
      rtp: '98.0%',
      activePlayers: 215,
      isMaintenance: false,
    },
    {
      id: 'plinko-physics',
      name: 'Plinko Physics',
      category: 'Instant',
      rtp: '97.0%',
      activePlayers: 175,
      isMaintenance: false,
    },
    {
      id: 'cyber-dice',
      name: 'Cyber Dice',
      category: 'Instant',
      rtp: '98.5%',
      activePlayers: 88,
      isMaintenance: false,
    },
  ]);

  const toggleMaintenance = (id: string) => {
    setGames((prev) =>
      prev.map((g) => (g.id === id ? { ...g, isMaintenance: !g.isMaintenance } : g)),
    );
  };

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
            <Gamepad2 className="h-6 w-6 text-purple-400" />
            Game Modules Governance
          </h1>
          <p className="text-sm text-gray-400 mt-1">
            Toggle maintenance status, view RTP, and inspect active session loads.
          </p>
        </div>
      </div>

      <div className="mt-8 bg-[#121824] border border-gray-800 rounded-xl overflow-hidden shadow-lg">
        <table className="w-full text-left text-sm text-gray-300">
          <thead className="bg-[#1A2234] text-xs uppercase text-gray-400 border-b border-gray-800">
            <tr>
              <th className="px-5 py-3.5">Game Title</th>
              <th className="px-5 py-3.5">Format</th>
              <th className="px-5 py-3.5">Audited RTP</th>
              <th className="px-5 py-3.5">Active Players</th>
              <th className="px-5 py-3.5">Status</th>
              <th className="px-5 py-3.5 text-right">Maintenance Mode</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-800">
            {games.map((g) => (
              <tr key={g.id} className="hover:bg-[#1A2234]/40 transition-colors">
                <td className="px-5 py-4 font-semibold text-white">{g.name}</td>
                <td className="px-5 py-4 text-gray-400">{g.category}</td>
                <td className="px-5 py-4 text-amber-400 font-medium">{g.rtp}</td>
                <td className="px-5 py-4 text-cyan-400 font-semibold">{g.activePlayers}</td>
                <td className="px-5 py-4">
                  <span
                    className={`px-2.5 py-1 text-xs rounded-full font-semibold border ${
                      !g.isMaintenance
                        ? 'bg-emerald-950 text-emerald-300 border-emerald-800'
                        : 'bg-amber-950 text-amber-300 border-amber-800'
                    }`}
                  >
                    {!g.isMaintenance ? 'OPERATIONAL' : 'MAINTENANCE'}
                  </span>
                </td>
                <td className="px-5 py-4 text-right">
                  <button
                    onClick={() => toggleMaintenance(g.id)}
                    className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded text-xs font-semibold transition-colors ${
                      g.isMaintenance
                        ? 'bg-emerald-900/40 text-emerald-300 hover:bg-emerald-800/60 border border-emerald-700/50'
                        : 'bg-amber-900/40 text-amber-300 hover:bg-amber-800/60 border border-amber-700/50'
                    }`}
                  >
                    <Power className="h-3.5 w-3.5" />
                    {g.isMaintenance ? 'Resume Game' : 'Schedule Maintenance'}
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
