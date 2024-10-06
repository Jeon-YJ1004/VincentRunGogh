import { writable, get } from 'svelte/store';
import type { Writable } from 'svelte/store';
import { LatLng } from 'leaflet';
import L from 'leaflet';
import { getPace } from '@/utils/calculateFuc';

// 데이터 타입 정의
interface Position {
  lat: number;
  lng: number;
}

interface PositionData {
  latlng: L.LatLng;
  time: number;
  speed?: number;
}

interface DrawingInfo {
  title: string;
  drawingPositionList?: Position[];
  routePositionList?: Position[];
  routeId?: string;
  drawingId?: string;
  endInfo?: Object;
}

// 초기 상태 정의
const initialState: DrawingInfo = {
  title: '',
  drawingPositionList: [],
  routePositionList: [],
};

export const isLockScreen = writable<boolean>(false);
export const isPause = writable(false);
export const currentPace = writable('00:00');
export const posList = writable([]);
export const route = writable([]);

// 로컬 스토리지에서 데이터 가져오기
function loadFromLocalStorage(key, defaultValue) {
  const storedData = localStorage.getItem('drawingStore');
  if (typeof localStorage !== 'undefined') {
    const storedValue = localStorage.getItem(key);
    return storedValue ? JSON.parse(storedValue) : defaultValue;
  }
  return defaultValue;
}

// drawingStore 정의 및 로컬 스토리지에서 로드
export const drawingStore: Writable<DrawingInfo> = writable(
  loadFromLocalStorage('drawingStore', initialState)
);
export const totalDistance = writable(loadFromLocalStorage('totalDistance', 0));
export const elapsedTime = writable(loadFromLocalStorage('elapsedTime', 0));

// 스토어 구독하여 변경 시 로컬 스토리지에 저장
drawingStore.subscribe((value) => {
  localStorage.setItem('drawingStore', JSON.stringify(value));
});

totalDistance.subscribe((value) => {
  localStorage.setItem('totalDistance', JSON.stringify(value));
});

elapsedTime.subscribe((value) => {
  localStorage.setItem('elapsedTime', JSON.stringify(value));
});

// drawingStore 업데이트 함수
export function updateDrawingInfo(data: Partial<DrawingInfo>): void {
  const currentDrawingStore = get(drawingStore);

  drawingStore.set({
    title: data.title || '',
    drawingPositionList: data.drawingPositionList || [],
    routePositionList: data.routePositionList || [],
    drawingId: data.drawingId !== undefined ? data.drawingId : currentDrawingStore.drawingId,
    routeId: data.routeId || undefined,
  });
}

// 경과 시간 및 좌표 목록 업데이트 함수
export function updateDistanceAndSpeed(posList: PositionData[]) {
  totalDistance.update((currentDistance) => {
    if (posList.length > 1) {
      const lastPosData = posList[posList.length - 2];
      const newPosData = posList[posList.length - 1];
      const lastPos = lastPosData.latlng;
      const newPos = newPosData.latlng;

      const distance = lastPos.distanceTo(newPos) / 1000; // km로 변환
      const timeDiff = (newPosData.time - lastPosData.time) / 1000; // 초로 변환

      if (timeDiff > 0 && distance > 0) {
        const speed = distance / (timeDiff / 3600);
        const paceSeconds = 3600 / speed;
        const paceMinutes = Math.floor(paceSeconds / 60);
        const remainingSeconds = Math.round(paceSeconds % 60);

        currentPace.set(getPace(distance, timeDiff));
      }

      return currentDistance + distance;
    }
    return currentDistance;
  });
}

export function setDrawingPos(data: Object): void {
  drawingStore.update((current) => {
    current.endInfo = data;
    return current;
  });
}

// drawingStore 초기화 함수
export function resetDrawingStore(): void {
  drawingStore.set({ ...initialState });

  isLockScreen.set(false);
  isPause.set(false);
  totalDistance.set(0);
  currentPace.set('');
  elapsedTime.set(0);
  posList.set([]);
  route.set([]);
  localStorage.removeItem('drawingStore');
  localStorage.removeItem('totalDistance');
  localStorage.removeItem('elapsedTime');
}
