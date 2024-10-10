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
  if (seconds <= 0) return '-';
  if (seconds < 60) return `${seconds}초`;
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const secs = seconds % 60;

  const hoursString = hours > 0 ? `${hours}:` : '';
  const minutesString = minutes < 10 && hours > 0 ? `0${minutes}` : minutes.toString();
  const secondsString = secs.toString().padStart(2, '0');

  return `${hoursString}${minutesString}:${secondsString}`;
}
export function formatSecToMS(seconds: number) {
  const minutes = Math.floor(seconds / 60);
  const secs = Math.round(seconds % 60);

  const minutesString = minutes.toString().padStart(2, '0');
  const secondsString = secs.toString().padStart(2, '0');

  return `${minutesString}:${secondsString}`;
}

export function formatSecToH(seconds: number): string {
  if (seconds < 30) {
    return '30초미만';
  } else {
    let result = (seconds / 3600).toFixed(2);
    while (result.endsWith('0')) {
      result = result.substring(0, result.length - 1);
    }

    return result + ' 시간';
  }
}

export function formatDistanceFix2(number: number): string {
  if (number % 1 === 0) {
    return number.toString();
  }
  return parseFloat(number.toFixed(2)).toString();
}

export function formatToKoreanTime(datetimeStr: string, isFull: boolean | null): string {
  const date = new Date(datetimeStr);
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0'); // getMonth는 0부터 시작하므로 1을 추가
  const day = date.getDate().toString().padStart(2, '0');
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');

  if (isFull) {
    return `${year}.${month}.${day} ${hours}시 ${minutes}분`;
  }
  return `${hours}시 ${minutes}분`;
}
