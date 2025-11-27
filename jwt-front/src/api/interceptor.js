import {useAuthStore} from "@/stores/auth.js";
import router from "@/routers/index.js";
import {API_ENDPOINTS} from "@/api/endpoints.js";
import {authApi} from "@/api/index.js";
import axios from "axios";

let isRefreshing = false;
let failedQueue = [];

const setupRequestInterceptor = (api) => {
    api.interceptors.request.use((config) => {
        const authStore = useAuthStore();

        if(authStore.existsAccessToken()) {
            config.headers["Authorization"] = authStore.getAccessToken();
        }

        return config;
    }, (error) => {
        return Promise.reject(error);
    })
}

const setupResponseInterceptor = (api) => {
    api.interceptors.response.use((config) => {
        return config;
    }, async (error) => {
        const originalRequest = error.config;

        if (error.response.status === 401 && !originalRequest._retry) {
            return handleUnauthorizedError(originalRequest, api);
        }
    })
}

const handleLogout = async () => {
    const authStore = useAuthStore();

    await authStore.logout();
    await router.push("/login");
}

const addQueue = async(originalRequest, api) => {
    return new Promise((resolve, reject) => {
        failedQueue.push({resolve, reject})
    }).then((token) => {
        if (originalRequest.headers) {
            originalRequest.headers.Authorization = token;
        }

        return api(originalRequest); // 새로 받은 토큰으로 재요청
    }).catch((err) => {Promise.reject()})
}

const handleUnauthorizedError = async(originalRequest, api) => {

    // 리프레시 요청했는데 401인 경우
    // 리프레시 토큰도 만료가 된 상태 -> 로그아웃
    if (originalRequest.url.includes(API_ENDPOINTS.AUTH.REFRESH_TOKEN)) {
        await handleLogout();
        return Promise.resolve();
    }

    if (isRefreshing) {
        await addQueue(originalRequest, api);
    }

    return tokenRefreshing(originalRequest, api);
}

const tokenRefreshing = async (originalRequest, api) => {
    originalRequest._retry = true;
    isRefreshing = true;

    try {

        const refreshInstance = axios.create({
            baseURL: authApi.defaults.baseURL,
            withCredentials: true, // refresh token 사용
        })
        const refreshResponse = await refreshInstance.get(API_ENDPOINTS.AUTH.REFRESH_TOKEN);
        const newAccessToken = refreshResponse.headers["authorization"];

        if (newAccessToken) {
            const authStore = useAuthStore();
            authStore.setAccessToken(newAccessToken);

            originalRequest.headers.Authorization = newAccessToken;
        }

        setNewToken(null, newAccessToken);
        return api(originalRequest); // 원래 요청 다시 날리기

    } catch(error) {
        await handleLogout();
        return Promise.reject();
    } finally {
        isRefreshing = false;
    }
}
