import { totalDistance, averageSpeed, elapsedTime } from '@/stores/drawingStore';
import type { LatLngExpression, LatLng } from 'leaflet';
import { get } from 'svelte/store'; // Svelte store와 get 함수 가져오기

// 두 좌표 간의 거리를 계산하는 함수 (Haversine 공식 사용)
function getDistanceFromLatLonInKm(lat1: number, lon1: number, lat2: number, lon2: number): number {
  const R = 6371; // 지구 반지름 (단위: km)
  const dLat = (lat2 - lat1) * (Math.PI / 180);
  const dLon = (lon2 - lon1) * (Math.PI / 180);
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(lat1 * (Math.PI / 180)) *
      Math.cos(lat2 * (Math.PI / 180)) *
      Math.sin(dLon / 2) *
      Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  const distance = R * c;
  return distance;
}

// 경과 시간과 좌표 목록을 받아 총 이동 거리와 평균 속도를 업데이트하는 함수
export function updateDistanceAndSpeed(
  posList: { latlng: L.LatLng; time: number; speed?: number }[]
) {
  totalDistance.update((totalDistance) => {
    // 새로운 좌표가 추가된 경우, 마지막 좌표와 새로운 좌표 간 거리 및 속도 계산
    if (posList.length > 1) {
      const lastPosData = posList[posList.length - 2]; // 이전 좌표 데이터
      const newPosData = posList[posList.length - 1]; // 새 좌표 데이터
      const lastPos = lastPosData.latlng;
      const newPos = newPosData.latlng;

      // Leaflet의 distanceTo 메서드를 사용하여 거리 계산 (미터 단위 반환)
      const distance = lastPos.distanceTo(newPos) / 1000; // km로 변환
      const timeDiff = (newPosData.time - lastPosData.time) / 1000; // 초 단위 시간 차이 계산

      // 속도 계산 (m/s를 km/h로 변환)
      const speed = (distance / timeDiff) * 3600;

      // posList에 속도 저장
      newPosData.speed = speed;

      // 총 이동 거리 업데이트
      totalDistance += distance;
    }

    return totalDistance;
  });

  // 평균 속도 계산 (총 이동 거리 / 경과 시간)
  averageSpeed.update(() => {
    return get(elapsedTime) > 0 ? get(totalDistance) / (get(elapsedTime) / 3600) : 0; // km/h로 계산
  });

  // posList에 저장된 새 좌표 및 속도 반환
  return { posList, currentSpeed: posList[posList.length - 1].speed || 0 };
}
export function formatSecToMMSS(seconds: number) {
  const minutes = Math.floor(seconds / 60);
  const secs = seconds % 60;

  const minutesString = minutes.toString().padStart(2, '0');
  const secondsString = secs.toString().padStart(2, '0');

  return `${minutesString}:${secondsString}`;
}
