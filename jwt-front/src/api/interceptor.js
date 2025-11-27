import {useAuthStore} from "@/stores/auth.js";
import router from "@/routers/index.js";
import {API_ENDPOINTS} from "@/api/endpoints.js";

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

const handleUnauthorizedError = async(originalRequest, api) => {

    // 리프레시 요청했는데 401인 경우
    // 리프레시 토큰도 만료가 된 상태 -> 로그아웃
    if (originalRequest.url.includes(API_ENDPOINTS.AUTH.REFRESH_TOKEN)) {
        await handleLogout();
        return Promise.resolve();
    }

    if (isRefreshing) {

    }
}