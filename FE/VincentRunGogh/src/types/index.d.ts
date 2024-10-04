// declare module`html2canvas`;
// declare module 'leaflet' {
//   namespace Control {
//     function extend(props: any): { new (...args: any[]): any } & typeof Control;
//   }
// }
declare module 'leaflet-hotline' {
  import * as L from 'leaflet';

  interface HotlineOptions extends L.PolylineOptions {
    weight?: number;
    outlineWidth?: number;
    outlineColor?: string;
    palette?: { [stop: number]: string };
    min?: number;
    max?: number;
  }

  export function hotline(
    latlngs: Array<[number, number, number]>,
    options: HotlineOptions
  ): L.Polyline;
}
declare module 'svelte-spa-router';
declare module 'svelte-fullcalendar';

interface UserAuth {
  id: string | null;
  nickname: string | null;
}
interface User extends UserAuth {
  height: number | null;
  weight: number | null;
  profile: string | null;
}
