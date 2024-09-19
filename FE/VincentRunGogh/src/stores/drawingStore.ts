import { LatLng } from 'leaflet';
import { type Readable, writable } from 'svelte/store';
import type { Writable } from 'svelte/store';

export const isLockScreen = writable<boolean>(false);
export const isPause = writable(false);
export const totalDistance = writable(0);
export const averageSpeed = writable(0);
export const elapsedTime = writable(0);
export const posList = writable([
  { latlng: { lat: 36.3718192, lng: 127.4102336 } as LatLng, time: 1726651413273, speed: 3 },
  { latlng: { lat: 36.3715, lng: 127.4102336 } as LatLng, time: 1726651413275, speed: 5 },
  { latlng: { lat: 36.3711, lng: 127.4112336 } as LatLng, time: 1726651413275, speed: 10 },
]);
export const route = writable([]);
