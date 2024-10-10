export async function getOrientation() {
  if (!window.DeviceOrientationEvent || !window.DeviceOrientationEvent.requestPermission) {
    return alert("Your current device does not have access to the DeviceOrientation event");
  }

  let permission = await window.DeviceOrientationEvent.requestPermission();
  if (permission !== "granted") {
    return alert("You must grant access to the device's sensor for this demo");
  }
}

window.addEventListener("deviceorientation", function (e) {
  let requestBtn = document.querySelector("#get-orientation");
  if (requestBtn) { requestBtn.remove(); }

  document.getElementById('alpha').innerHTML = e.alpha.toFixed(1) + "°"; //angle of motion around the Z axis
  document.getElementById('beta').innerHTML = e.beta.toFixed(1) + "°"; //angle of motion around the X axis
  document.getElementById('gamma').innerHTML = e.gamma.toFixed(1) + "°"; //angle of motion around the Y axis
  document.getElementById('orientation').innerHTML = Math.abs(e.beta) > Math.abs(e.gamma) ? "portrait" : "landscape";
});



export async function getMotion() {
  if (!window.DeviceMotionEvent) {
    return alert("Your device does not support motion detection.");
  }

  let stepCount = 0;
  let lastReading = { x: 0, y: 0, z: 0 };
  const threshold = 1.2;

  const updateStepCount = (x, y, z) => {
    const deltaX = Math.abs(x - lastReading.x);
    const deltaY = Math.abs(y - lastReading.y);
    const deltaZ = Math.abs(z - lastReading.z);

    if (deltaX > threshold || deltaY > threshold || deltaZ > threshold) {
      stepCount++;
      console.log(`걸음 수: ${stepCount}`);
    }

    lastReading = { x, y, z };
  };

  // Accelerometer 사용 시
  if ('Accelerometer' in window) {
    const accelerometer = new Accelerometer({ frequency: 10 });
    accelerometer.addEventListener('reading', () => {
      updateStepCount(accelerometer.x, accelerometer.y, accelerometer.z);
    });
    accelerometer.start();
  } else if (DeviceMotionEvent.requestPermission) {
    // DeviceMotion 사용 시 (iOS 13+)
    const permission = await DeviceMotionEvent.requestPermission();
    if (permission === 'granted') {
      window.addEventListener('devicemotion', (event) => {
        if (event.accelerationIncludingGravity) {
          const { x, y, z } = event.accelerationIncludingGravity;
          updateStepCount(x, y, z);
        }
      }, true);
    } else {
      alert("Permission to access motion sensors was denied.");
    }
  } else {
    // 기타 경우
    window.addEventListener('devicemotion', (event) => {
      if (event.accelerationIncludingGravity) {
        const { x, y, z } = event.accelerationIncludingGravity;
        updateStepCount(x, y, z);
      }
    }, true);
  }
}
