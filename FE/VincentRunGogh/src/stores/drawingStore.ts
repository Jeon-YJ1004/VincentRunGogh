import { writable, get, derived } from 'svelte/store';
import type { Writable } from 'svelte/store';
import { LatLng } from 'leaflet';
import L from 'leaflet';

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
  drawingPositionList?: Position[][];
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
export const route = writable([]);
export const posList = writable([]);
export const isRouteDrawing = writable<boolean>(false);
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
export const realTimePositions = writable(loadFromLocalStorage('realTimePositions', []));

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
realTimePositions.subscribe((value) => {
  localStorage.setItem('realTimePositions', JSON.stringify(value));
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
export function updateLastPosSpeed(speed: number) {
  posList.update((list) => {
    if (list.length > 0) {
      // 마지막 요소의 참조를 가져와서 speed를 수정합니다.
      const lastIndex = list.length - 1;
      const lastPos = { ...list[lastIndex], speed: speed };
      // 기존의 list를 복사하고 마지막 요소만 업데이트된 새로운 객체로 교체합니다.
      return [...list.slice(0, lastIndex), lastPos];
    }
    return list;
  });
}
// 경과 시간 및 좌표 목록 업데이트 함수
export function updateDistanceAndSpeed() {
  totalDistance.update((currentDistance) => {
    const positions = get(posList); // 스토어에서 현재 위치 목록을 가져옵니다.
    if (positions.length > 1) {
      const lastPosData = positions[positions.length - 2];
      const newPosData = positions[positions.length - 1];
      const lastPos = lastPosData.latlng;
      const newPos = newPosData.latlng;

      const distance = lastPos.distanceTo(newPos);
      const timeDiff = (newPosData.time - lastPosData.time) / 1000; // 초로 변환
      console.log('updateDistanceAndSpeed에서 distance: ' + distance + ' timeDiff: ' + timeDiff);
      if (timeDiff > 0 && distance > 0) {
        const speed = distance / 1000 / (timeDiff / 3600);
        updateLastPosSpeed(speed); // 여기에서 속도 업데이트 함수 호출

        const paceSeconds = 3600 / speed;
        const paceMinutes = Math.floor(paceSeconds / 60);
        const remainingSeconds = Math.round(paceSeconds % 60);
        const minutesString = paceMinutes.toString().padStart(2, '0');
        const secondsString = remainingSeconds.toString().padStart(2, '0');
        currentPace.set(`${minutesString}:${secondsString}`);
        console.log(`그래서 pace는 ${minutesString}:${secondsString}`);
      } else {
        currentPace.set('-');
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
  realTimePositions.set([]);
  isRouteDrawing.set(false);

  localStorage.removeItem('realTimePositions');
  localStorage.removeItem('drawingStore');
  localStorage.removeItem('totalDistance');
  localStorage.removeItem('elapsedTime');
}

// export async function getMotion() {
//   const threshold = 2.0; // 변화를 감지할 가속도 임계값
//   let lastReading = { x: 0, y: 0, z: 0 };
//   let lastIncrementTime = Date.now();
//   let stepDetectionTimeout = 250;
//   function incrementStepCount(x, y, z) {
//     const now = Date.now();
//     const timeSinceLastIncrement = now - lastIncrementTime;

//     if (timeSinceLastIncrement < stepDetectionTimeout) {
//       return; // 너무 빠르게 걸음수가 증가하지 않도록 제한
//     }

//     const deltaX = Math.abs(x - lastReading.x);
//     const deltaY = Math.abs(y - lastReading.y);
//     const deltaZ = Math.abs(z - lastReading.z);

//     if (deltaX > threshold || deltaY > threshold || deltaZ > threshold) {
//       stepCount.update((n) => n + 1); // Svelte store의 값을 증가
//       lastIncrementTime = now; // 마지막 걸음 증가 시간 업데이트
//     }

//     console.log('stepCount: ' + get(stepCount));
//     lastReading = { x, y, z };
//   }

//   if ('Accelerometer' in window) {
//     const accelerometer = new Accelerometer({ frequency: 10 });
//     accelerometer.addEventListener('reading', () => {
//       incrementStepCount(accelerometer.x, accelerometer.y, accelerometer.z);
//     });
//     accelerometer.start();
//   } else if (DeviceMotionEvent.requestPermission) {
//     const permission = await DeviceMotionEvent.requestPermission();
//     if (permission === 'granted') {
//       window.addEventListener(
//         'devicemotion',
//         (event) => {
//           if (event.accelerationIncludingGravity) {
//             const { x, y, z } = event.accelerationIncludingGravity;
//             incrementStepCount(x, y, z);
//           }
//         },
//         true
//       );
//     } else {
//       alert('Permission to access motion sensors was denied.');
//     }
//   } else {
//     window.addEventListener(
//       'devicemotion',
//       (event) => {
//         if (event.accelerationIncludingGravity) {
//           const { x, y, z } = event.accelerationIncludingGravity;
//           incrementStepCount(x, y, z);
//         }
//       },
//       true
//     );
//   }
// }
