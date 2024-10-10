import './styles/app.css';
import './styles/calendar.css';
import App from './App.svelte';
import { Chart, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
Chart.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const app = new App({
  target: document.getElementById('app')!,
});

export default app;
