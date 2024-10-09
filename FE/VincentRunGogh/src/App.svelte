<script lang="ts">
  import './styles/app.css';
  import './styles/calendar.css';

  import Router, { replace } from 'svelte-spa-router';
  import { userStore } from '@/stores/userStore';
  import {
    DrawingDetailPage,
    DrawingPage,
    MakeRoutePage,
    DrawingCapturePage,
    CalendarPage,
    RouteListPage,
    HomePage,
    CommunityPage,
    MyStoragePage,
    LoginPage,
    SignUpPage,
    MyHealthPage,
    ProgressPage,
    SignUpProfilePage,
    HistoryPage,
  } from '@/pages';

  // userStore.initialize();

  // let isAuthenticated = false;

  // userStore.subscribe(($user) => {
  //   isAuthenticated = $user !== null;
  //   if (isAuthenticated) {
  //     replace('/'); // 로그인 상태면 홈 페이지로 이동
  //   } else {
  //     replace('/login'); // 비로그인 상태면 로그인 페이지로 이동
  //   }
  // });

  // $: routes = {
  //   '/makeroute': !isAuthenticated ? LoginPage : MakeRoutePage,
  //   '/drawingmap': !isAuthenticated ? LoginPage : DrawingPage,
  //   '/drawingcapture': !isAuthenticated ? LoginPage : DrawingCapturePage,
  //   '/calendar': !isAuthenticated ? LoginPage : CalendarPage,
  //   '/drawingdetail': !isAuthenticated ? LoginPage : DrawingDetailPage,
  //   '/': !isAuthenticated ? LoginPage : HomePage,
  //   '/routelist': !isAuthenticated ? LoginPage : RouteListPage,
  //   '/community': !isAuthenticated ? LoginPage : CommunityPage,
  //   '/community/mystorage': !isAuthenticated ? LoginPage : MyStoragePage,
  //   '/myhealth': !isAuthenticated ? LoginPage : MyHealthPage,
  //   '/progress': !isAuthenticated ? LoginPage : ProgressPage,

  //   '/signup': isAuthenticated ? HomePage : SignUpPage,
  //   '/signup/profile': isAuthenticated ? HomePage : SignUpProfilePage,
  //   '/login': isAuthenticated ? HomePage : LoginPage,
  //   '*': isAuthenticated ? HomePage : LoginPage,
  // };
  let routes = {
    '/makeroute': MakeRoutePage,
    '/drawingmap': DrawingPage,
    '/drawingcapture': DrawingCapturePage,
    '/calendar': CalendarPage,
    '/drawingdetail': DrawingDetailPage,
    '/': HomePage,
    '/routelist': RouteListPage,
    '/community': CommunityPage,
    '/community/mystorage': MyStoragePage,
    '/myhealth': MyHealthPage,
    '/progress': ProgressPage,
    '/history': HistoryPage,

    '/signup': SignUpPage,
    '/signup/profile': SignUpProfilePage,
    '/login': LoginPage,
    '*': LoginPage,
  };

  // 새로고침 방지
  window.addEventListener(
    'touchmove',
    (event) => {
      event.preventDefault();
    },
    { passive: false }
  );
</script>

<Router {routes} />
