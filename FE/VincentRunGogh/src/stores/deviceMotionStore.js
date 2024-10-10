import { writable } from 'svelte/store';

// Threshold and timeout settings
const MIN_STEP_THRESHOLD = 20;
const MAX_STEP_THRESHOLD = 40;
const STEP_TIME_DELAY = 500; // in milliseconds
const PREV_POINTS = 75;
// State and historic data
let lastStepTime = 0;
let historicMotion = { x: [], y: [], z: [] };
let historicOrientation = { x: [], y: [], z: [] };
export const stepCount = writable(0);
export const deviceOrientation = writable({ alpha: 0, beta: 0, gamma: 0 });

// // Functions to handle motion and orientation
// function motion(event) {
//   const { accelerationIncludingGravity } = event;
//   if (accelerationIncludingGravity) {
//     updateMotionData(
//       accelerationIncludingGravity.x,
//       accelerationIncludingGravity.y,
//       accelerationIncludingGravity.z
//     );
//   }
// }

// function orientation(event) {
//   const { alpha, beta, gamma } = event;
//   deviceOrientation.set({ alpha, beta, gamma });
//   updateOrientationData(alpha, beta, gamma);
// }

// function updateMotionData(x, y, z) {
//   historicMotion.x.push(x);
//   historicMotion.y.push(y);
//   historicMotion.z.push(z);
//   trimData();
//   calculateSteps();
// }

// function updateOrientationData(alpha, beta, gamma) {
//   historicOrientation.x.push(alpha);
//   historicOrientation.y.push(beta);
//   historicOrientation.z.push(gamma);
//   trimData(historicOrientation);
// }

// function trimData() {
//   Object.keys(historicMotion).forEach((key) => {
//     if (historicMotion[key].length > 500) {
//       historicMotion[key].shift();
//     }
//   });
// }

// function calculateSteps() {
//   const now = Date.now();
//   if (now - lastStepTime > stepTimeout) {
//     const movement = calculateAverageMovement(historicMotion, prevPoint);
//     if (movement > 10) {
//       alert(movement);
//     }
//     if (movement > MIN_STEP_THRESHOLD && movement < MAX_STEP_THRESHOLD) {
//       stepCount.update((n) => n + 1);
//       lastStepTime = now;
//     }
//   }
// }

// function calculateAverageMovement(historic, points) {
//   return (
//     Object.keys(historic).reduce((acc, key) => {
//       let data = historic[key].slice(-points);
//       let sum = data.reduce((sum, val) => sum + Math.abs(val), 0);
//       return acc + sum / data.length;
//     }, 0) / 3
//   );
// }

// Setup and clear event listeners
export function setupMotionEventListeners() {
  window.addEventListener('deviceorientation', orientation);
}
let setpIntervalId; // 여기에서 intervalId를 선언

export function setupStepEventListeners() {
  window.addEventListener('devicemotion', motion);
  setpIntervalId = window.setInterval(updateStatus, 100);
}
export function stopStepEventListeners() {
  window.removeEventListener('devicemotion', motion);
  if (setpIntervalId) {
    clearInterval(setpIntervalId); // intervalId를 사용하여 인터벌 중지
    setpIntervalId = null; // intervalId를 재설정
  }
}
export function clearMotionEventListeners() {
  window.removeEventListener('devicemotion', motion);
  window.removeEventListener('deviceorientation', orientation);
  if (setpIntervalId) {
    clearInterval(setpIntervalId); // intervalId를 사용하여 인터벌 중지
    setpIntervalId = null; // intervalId를 재설정
  }
  deviceOrientation.set({ alpha: 0, beta: 0, gamma: 0 });
}

function updateStatus() {
  // 움직임 및 방향 데이터를 기반으로 상태를 설정
  const now = Date.now();
  if (now - lastStepTime > STEP_TIME_DELAY) {
    // 최근 75개의 데이터를 기준으로 전체 움직임을 계산
    let movement = mostRecentMovementOverall(PREV_POINTS);
    if (movement > MIN_STEP_THRESHOLD) stepCount.update((n) => n + 1);
    lastStepTime = now;
  }
}

// 기록된 데이터를 바탕으로 전체 움직임을 계산하는 함수
function mostRecentMovementOverall(numberOfHistoricPoints) {
  // x, y, z축의 움직임 평균을 계산하여 반환
  return (
    (mostRecentMovement(historicMotion['x'], numberOfHistoricPoints, true) +
      mostRecentMovement(historicMotion['y'], numberOfHistoricPoints, true) +
      mostRecentMovement(historicMotion['z'], numberOfHistoricPoints, true)) /
    3.0
  );
}

// 특정 축의 움직임을 계산하는 함수
function mostRecentMovement(array, numberOfHistoricPoints, removeNegatives) {
  if (array.length > numberOfHistoricPoints) {
    let totalSum = 0;
    for (var toCount = 0; toCount < numberOfHistoricPoints; toCount++) {
      let currentElement = array[array.length - toCount - 1];

      // 최신 데이터를 더 큰 비중으로 계산
      currentElement *= 1 - toCount / numberOfHistoricPoints;

      // 음수 값을 제거해야 하는 경우, 음수를 양수로 변환
      if (currentElement < 0 && removeNegatives) currentElement = currentElement * -1;

      // 너무 작은 값은 무시
      if (currentElement > 0.1 || currentElement < -0.1) totalSum += currentElement;
    }
    return (totalSum * 100) / numberOfHistoricPoints; // 평균을 반환
  }
  return 0;
}

// 기기 움직임 이벤트를 처리하는 함수
function motion(event) {
  // 각 축의 움직임 값을 업데이트

  historicMotion['x'].push(event.acceleration.x);
  historicMotion['y'].push(event.acceleration.y);
  historicMotion['z'].push(event.acceleration.z);
}

// 기기 방향 이벤트를 처리하는 함수
function orientation(event) {
  const { alpha, beta, gamma } = event;
  deviceOrientation.set({ alpha, beta, gamma });
}
