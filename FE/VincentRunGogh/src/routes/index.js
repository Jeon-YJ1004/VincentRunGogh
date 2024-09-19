// import 컴포넌트
// @ts-ignore
import {
  DrawingDetailPage,
  DrawingPage,
  MakeRoutePage,
  DrawingCapturePage,
  CalendarPage,
  RouteListPage,
  HomePage,
} from '../pages';
export default {
  // 링크 : 컴포넌트
  // :param, *any
  '/makeroute': MakeRoutePage,
  '/drawingmap': DrawingPage,
  '/drawingcapture': DrawingCapturePage,
  '/calendar': CalendarPage,
  '/drawingdetail/:id': DrawingDetailPage,
  '/': HomePage,
  '/routelist': RouteListPage,
};
