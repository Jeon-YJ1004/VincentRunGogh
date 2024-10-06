export function formatTimeToHMS(): string {
  const now = new Date();
  let hours = now.getHours().toString();
  let minutes = now.getMinutes().toString();
  let seconds = now.getSeconds().toString();

  // 시간, 분, 초가 한 자리수라면 0을 앞에 추가
  hours = hours.padStart(2, '0');
  minutes = minutes.padStart(2, '0');
  seconds = seconds.padStart(2, '0');

  return `${hours}:${minutes}:${seconds}`;
}
export function formatSecToHMS(seconds: number): string {
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const secs = seconds % 60;

  const hoursString = hours > 0 ? `${hours}:` : '';
  const minutesString = minutes < 10 && hours > 0 ? `0${minutes}` : minutes.toString();
  const secondsString = secs.toString().padStart(2, '0');

  return `${hoursString}${minutesString}:${secondsString}`;
}

export function formatSecToH(seconds: number): string {
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  return `${hours > 0 ? hours + 'h' : ''} ${minutes < 10 ? '0' + minutes : minutes}${minutes > 0 ? 'min' : ''}`;
}

export function formatDistanceFix2(number: number): string {
  if (number % 1 === 0) {
    return number.toString();
  }
  return parseFloat(number.toFixed(2)).toString();
}
