import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from '@/routers';
import pinia from '@/stores';

const app = createApp(App);
app.use(router)
app.use(pinia) // .use(객체)를 이용해 추가.

app.mount("#app") // #app 위치에 마운트 -> 돔을 구성.
