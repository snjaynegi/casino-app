import { Injectable, NotFoundException } from '@nestjs/common';
import { GameMetadata } from '@casino/shared-types';

export interface GameDetailDto extends GameMetadata {
  rtp: string;
  volatility: 'LOW' | 'MEDIUM' | 'HIGH';
  rules: string[];
  maxMultiplier: string;
}

@Injectable()
export class GamesService {
  private readonly games: GameDetailDto[] = [
    {
      id: 'rocket-rescue',
      title: 'Rocket Rescue',
      category: 'CRASH',
      description:
        'A rocket travels through an animated space environment. Eject before collision with asteroid fields to bank your multiplied virtual coins!',
      minBet: 10,
      maxBet: 5000,
      isActive: true,
      thumbnailUrl: '/assets/games/rocket-rescue.webp',
      rtp: '97.0%',
      volatility: 'MEDIUM',
      maxMultiplier: '10,000x',
      rules: [
        'Place your virtual coin bet before the rocket initiates launch countdown.',
        'As the rocket climbs through space, the win multiplier increases continuously.',
        'Click EJECT at any moment to lock in your multiplied virtual coin payout.',
        'If the rocket crashes into space debris before you eject, the bet is lost.',
        '100% Provably Fair: Every round outcome is independently verifiable via SHA-256 commit-reveal.',
      ],
    },
    {
      id: 'frog-frenzy',
      title: 'Frog Frenzy',
      category: 'ARCADE',
      description:
        'Guide your cyber frog across moving neon lily pads. Each successful jump compounds your virtual coin multiplier with rising hazard risk.',
      minBet: 10,
      maxBet: 2500,
      isActive: true,
      thumbnailUrl: '/assets/games/frog-frenzy.webp',
      rtp: '96.5%',
      volatility: 'LOW',
      maxMultiplier: '500x',
      rules: [
        'Select your bet and initiate the crossing challenge.',
        'Each forward hop successfully landing on a lily pad increases your multiplier.',
        'Cash out your banked coins at any step or jump further for higher multipliers.',
        'A single fall into the cyber swamp resets the round.',
      ],
    },
    {
      id: 'deep-dive',
      title: 'Deep Dive',
      category: 'ARCADE',
      description:
        'Submerge into the ocean abyss to uncover sunken treasure chests while managing submarine pressure.',
      minBet: 20,
      maxBet: 5000,
      isActive: true,
      thumbnailUrl: '/assets/games/deep-dive.webp',
      rtp: '97.2%',
      volatility: 'HIGH',
      maxMultiplier: '2,500x',
      rules: [
        'Navigate deeper into the oceanic trenches tier by tier.',
        'Unopened chests contain multipliers, bonus oxygen, or kraken hazards.',
        'Collect your sunken winnings before your hull integrity fails.',
      ],
    },
    {
      id: 'sky-elevator',
      title: 'Sky Elevator',
      category: 'ARCADE',
      description: 'Ascend a futuristic mega-tower floor by floor with rising floor bonuses.',
      minBet: 10,
      maxBet: 3000,
      isActive: true,
      thumbnailUrl: '/assets/games/sky-elevator.webp',
      rtp: '96.8%',
      volatility: 'HIGH',
      maxMultiplier: '1,000x',
      rules: [
        'Choose whether to exit at the current floor or ride to higher penthouse tiers.',
        'Compounding floor rewards multiply your virtual coin stake at every floor level.',
      ],
    },
    {
      id: 'neon-bounce',
      title: 'Neon Bounce',
      category: 'ARCADE',
      description: 'Physics-driven arcade bounce game with reactive neon multipliers.',
      minBet: 10,
      maxBet: 4000,
      isActive: true,
      thumbnailUrl: '/assets/games/neon-bounce.webp',
      rtp: '97.5%',
      volatility: 'MEDIUM',
      maxMultiplier: '750x',
      rules: [
        'Launch the cyber sphere into the multiplier pins and bumpers.',
        'Multiplier bumpers light up and compound the final score upon collection.',
      ],
    },
    {
      id: 'mines-grid',
      title: 'Mines Grid',
      category: 'INSTANT',
      description:
        'Classic 5x5 grid game with configurable mine density and Provably Fair random generation.',
      minBet: 10,
      maxBet: 10000,
      isActive: true,
      thumbnailUrl: '/assets/games/mines-grid.webp',
      rtp: '98.0%',
      volatility: 'MEDIUM',
      maxMultiplier: '5,000x',
      rules: [
        'Configure how many hidden mines (1 to 24) are placed on the 25-tile grid.',
        'Uncover diamond tiles to multiply your stake. Cash out anytime.',
        'Revealing a hidden mine detonates the round.',
      ],
    },
    {
      id: 'plinko-physics',
      title: 'Plinko Physics',
      category: 'INSTANT',
      description: 'Multi-row Galton board with server-side authoritative peg bounce physics.',
      minBet: 10,
      maxBet: 5000,
      isActive: true,
      thumbnailUrl: '/assets/games/plinko.webp',
      rtp: '97.0%',
      volatility: 'MEDIUM',
      maxMultiplier: '1,000x',
      rules: [
        'Select risk tier (Low, Medium, High) and row depth (8 to 16 rows).',
        'Drop balls from the top pin funnel into high-multiplier edge pockets.',
      ],
    },
    {
      id: 'cyber-dice',
      title: 'Cyber Dice',
      category: 'INSTANT',
      description:
        'Provably Fair precision dice roll with custom win chance sliders from 1% to 95%.',
      minBet: 10,
      maxBet: 10000,
      isActive: true,
      thumbnailUrl: '/assets/games/dice.webp',
      rtp: '98.5%',
      volatility: 'LOW',
      maxMultiplier: '990x',
      rules: [
        'Set your target prediction roll (Roll Over / Roll Under).',
        'CSPRNG rolls a number between 0.00 and 99.99 with audited house edge.',
      ],
    },
  ];

  public getAllGames(category?: string, query?: string): GameDetailDto[] {
    let result = this.games.filter((g) => g.isActive);

    if (category && category !== 'ALL') {
      result = result.filter((g) => g.category.toUpperCase() === category.toUpperCase());
    }

    if (query && query.trim().length > 0) {
      const q = query.toLowerCase().trim();
      result = result.filter(
        (g) => g.title.toLowerCase().includes(q) || g.description.toLowerCase().includes(q),
      );
    }

    return result;
  }

  public getGameById(id: string): GameDetailDto {
    const game = this.games.find((g) => g.id === id);
    if (!game) {
      throw new NotFoundException(`Game with id '${id}' not found`);
    }
    return game;
  }

  public getGameConfig(id: string) {
    const game = this.getGameById(id);
    return {
      gameId: game.id,
      minBet: game.minBet,
      maxBet: game.maxBet,
      rtp: game.rtp,
      volatility: game.volatility,
      currencyCode: 'VCOIN',
      provablyFair: true,
      hashAlgorithm: 'HMAC-SHA512',
    };
  }
}
