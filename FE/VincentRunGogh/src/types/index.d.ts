// declare module `html2canvas`
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
