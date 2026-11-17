import {
  ConnectedSocket,
  MessageBody,
  OnGatewayConnection,
  OnGatewayDisconnect,
  SubscribeMessage,
  WebSocketGateway,
  WebSocketServer,
} from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';
import { RealtimeService } from './realtime.service';
import { TokenService } from '../auth/jwt.service';

@WebSocketGateway({
  cors: {
    origin: '*',
  },
})
export class RealtimeGateway implements OnGatewayConnection, OnGatewayDisconnect {
  @WebSocketServer()
  server!: Server;

  constructor(
    private readonly realtimeService: RealtimeService,
    private readonly tokenService: TokenService,
  ) {}

  public handleConnection(client: Socket) {
    try {
      const authHeader = client.handshake.headers['authorization'] || client.handshake.auth?.token;
      if (authHeader) {
        const token = authHeader.replace('Bearer ', '');
        const payload = this.tokenService.verifyToken(token);
        client.data.user = payload;
      }
    } catch {
      // Allow guest/spectator connections
      client.data.user = null;
    }

    this.realtimeService.incrementPresence();
    this.broadcastPresence();

    // Send initial live state immediately to new client
    client.emit('sync_state', {
      roundState: this.realtimeService.getLiveRoundState(),
      onlineCount: this.realtimeService.getOnlineCount(),
      activeBets: this.realtimeService.getActiveBets(),
    });
  }

  public handleDisconnect(_client: Socket) {
    this.realtimeService.decrementPresence();
    this.broadcastPresence();
  }

  @SubscribeMessage('sync_state')
  public handleSyncState() {
    return {
      roundState: this.realtimeService.getLiveRoundState(),
      onlineCount: this.realtimeService.getOnlineCount(),
      activeBets: this.realtimeService.getActiveBets(),
    };
  }

  @SubscribeMessage('join_room')
  public handleJoinRoom(@ConnectedSocket() client: Socket, @MessageBody() room: string) {
    client.join(room);
    return { joined: room };
  }

  @SubscribeMessage('place_live_bet')
  public handlePlaceBet(
    @ConnectedSocket() client: Socket,
    @MessageBody() data: { betAmount: number },
  ) {
    const username = client.data.user?.username || `Guest_${client.id.substring(0, 5)}`;
    const bet = this.realtimeService.registerLiveBet(username, data.betAmount);
    this.server.emit('live_bet_placed', bet);
    return bet;
  }

  @SubscribeMessage('cashout_live_bet')
  public handleLiveCashout(
    @ConnectedSocket() client: Socket,
    @MessageBody() data: { multiplier: number; payout: number },
  ) {
    const username = client.data.user?.username || `Guest_${client.id.substring(0, 5)}`;
    const result = this.realtimeService.registerLiveCashout(username, data.multiplier, data.payout);
    if (result) {
      this.server.emit('live_bet_cashed_out', result);
    }
    return result;
  }

  private broadcastPresence() {
    if (this.server) {
      this.server.emit('presence_update', {
        onlineCount: this.realtimeService.getOnlineCount(),
      });
    }
  }
}
