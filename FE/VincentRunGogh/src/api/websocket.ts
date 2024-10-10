// src/websocket.ts
import { writable } from 'svelte/store';
import { Client } from '@stomp/stompjs';
import type { IFrame } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { formatTimeToHMS } from '@/utils/formatter';
import { realTimePositions } from '@/stores/drawingStore';

let stompClient: Client | null = null;

export function connectWebSocket() {
  // onConnectedCallback: () => void,
  // onDisconnectCallback: () => void
  const accessToken = localStorage.getItem('accessToken');
  const headers = { Authorization: `Bearer ${accessToken}` };
  return new Promise((resolve, reject) => {
    const socket = new SockJS(import.meta.env.VITE_WEB_SOCKET_URL);

    // STOMP 클라이언트 생성
    stompClient = new Client({
      webSocketFactory: () => socket,
      debug: (str) => {
        // console.log(str);
      },
      connectHeaders: headers,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: (frame: IFrame) => {
        console.log('소켓 연결!:', frame);
        // onConnectedCallback();
      },
      onDisconnect: () => {
        console.log('Disconnected');
        // onDisconnectCallback();
      },
      onStompError: (frame: IFrame) => {
        console.error('Broker reported error:', frame.headers['message']);
        console.error('Additional details:', frame.body);
        restartSockJS();
      },
    });

    // 클라이언트 연결
    stompClient.activate();
  });
}

export function sendRealTimePosition(location: Object, nickname: string) {
  if (stompClient && stompClient.connected) {
    const accessToken = localStorage.getItem('accessToken');
    const headers = { Authorization: `Bearer ${accessToken}` };
    const data = { ...location, time: formatTimeToHMS() };
    // console.log('Sending data:', data);
    realTimePositions.update((prev) => {
      return [...prev, data];
    });
    console.log('Sending data:', data);
    stompClient.publish({
      destination: `/pub/running/${nickname}`,
      headers: headers,
      body: JSON.stringify(data),
    });
  }
}

// 연결 해제 함수
export function disconnectWebSocket() {
  if (stompClient) {
    stompClient.deactivate();
  }
}

// 재연결 함수
function restartSockJS() {
  console.log('Attempting to restart SockJS...');
  const socket = new SockJS(import.meta.env.VITE_WEB_SOCKET_URL);
  if (stompClient) {
    stompClient.webSocketFactory = () => socket;
    stompClient.activate();
  }
}
