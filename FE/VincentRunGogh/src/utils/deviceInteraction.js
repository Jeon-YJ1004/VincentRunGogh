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
  if (!window.DeviceMotionEvent || !window.DeviceMotionEvent.requestPermission) {
    return alert("Your current device does not have access to the DeviceMotion event");
  }
  // Accelerometer를 사용해 걸음 수 측정하기
  if ('Accelerometer' in window) {
    const accelerometer = new Accelerometer({ frequency: 10 });

    let stepCount = 0;
    let lastReading = { x: 0, y: 0, z: 0 };

    accelerometer.addEventListener('reading', () => {
      const deltaX = Math.abs(accelerometer.x - lastReading.x);
      const deltaY = Math.abs(accelerometer.y - lastReading.y);
      const deltaZ = Math.abs(accelerometer.z - lastReading.z);

      const threshold = 1.2;

      if (deltaX > threshold || deltaY > threshold || deltaZ > threshold) {
        stepCount++;
        console.log(`걸음 수: ${stepCount}`);
      }

      lastReading = { x: accelerometer.x, y: accelerometer.y, z: accelerometer.z };
    });

    accelerometer.start();
  } else {
    console.error("Accelerometer가 지원되지 않습니다.");
  }

}