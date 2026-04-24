import { createApp } from 'vue';
import axios from 'axios';
import App from './App.vue';
import router from './router';
import './assets/style.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap';

axios.defaults.withCredentials = true;

const app = createApp(App);
app.use(router);
app.mount('#app')
