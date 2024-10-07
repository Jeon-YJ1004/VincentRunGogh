import { formatSecToMS } from './formatter';

export function getPace(distance: number, timeInSeconds: number) {
  if (distance <= 0 || timeInSeconds <= 0) {
    return '00:00'; // 거리나 시간이 0 이하면 유효하지 않음
  }

  const paceInSeconds = timeInSeconds / distance; // 1킬로미터 달리는데 걸린 시간(초)
  return formatSecToMS(Math.floor(paceInSeconds));
}
