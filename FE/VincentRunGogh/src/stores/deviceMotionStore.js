import { writable } from 'svelte/store';

// Threshold and timeout settings
const stepThreshold = 2;
const stepTimeout = 250; // in milliseconds

// State and historic data
let lastStepTime = 0;
let historicMotion = { x: [], y: [], z: [] };
let historicOrientation = { x: [], y: [], z: [] };
export const stepCount = writable(0);
export const deviceOrientation = writable({ alpha: 0, beta: 0, gamma: 0 });

// Functions to handle motion and orientation
function motion(event) {
  const { accelerationIncludingGravity } = event;
  if (accelerationIncludingGravity) {
    updateMotionData(
      accelerationIncludingGravity.x,
      accelerationIncludingGravity.y,
      accelerationIncludingGravity.z
    );
  }
}

function orientation(event) {
  const { alpha, beta, gamma } = event;
  deviceOrientation.set({ alpha, beta, gamma });
  updateOrientationData(alpha, beta, gamma);
}

function updateMotionData(x, y, z) {
  historicMotion.x.push(x);
  historicMotion.y.push(y);
  historicMotion.z.push(z);
  trimData(historicMotion);
  calculateSteps();
}

function updateOrientationData(alpha, beta, gamma) {
  historicOrientation.x.push(alpha);
  historicOrientation.y.push(beta);
  historicOrientation.z.push(gamma);
  trimData(historicOrientation);
}

function trimData(historic) {
  Object.keys(historic).forEach((key) => {
    if (historic[key].length > 500) {
      historic[key].shift();
    }
  });
}

function calculateSteps() {
  const now = Date.now();
  if (now - lastStepTime > stepTimeout) {
    const movement = calculateAverageMovement(historicMotion, 75);
    if (movement > stepThreshold) {
      stepCount.update((n) => n + 1);
      lastStepTime = now;
    }
  }
}

function calculateAverageMovement(historic, points) {
  return (
    Object.keys(historic).reduce((acc, key) => {
      let data = historic[key].slice(-points);
      let sum = data.reduce((sum, val) => sum + Math.abs(val), 0);
      return acc + sum / data.length;
    }, 0) / 3
  );
}

// Setup and clear event listeners
export function setupMotionEventListeners() {
  window.addEventListener('deviceorientation', orientation);
}
export function setupStepEventListeners() {
  window.addEventListener('devicemotion', motion);
}
export function stopStepEventListeners() {
  window.removeEventListener('devicemotion', motion);
}
export function clearMotionEventListeners() {
  window.removeEventListener('devicemotion', motion);
  window.removeEventListener('deviceorientation', orientation);
  stepCount.set(0);
  deviceOrientation.set({ alpha: 0, beta: 0, gamma: 0 });
}
