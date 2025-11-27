import {defineStore} from "pinia";
import {computed, ref} from "vue";
import {authApi, resourceApi} from "@/api";
import {API_ENDPOINTS} from "@/api/endpoints.js"; // 알아서 index.js를 찾는다.

export const useAuthStore = defineStore("auth", () => {

    const accessToken = ref(null);

    const login = async (loginRequest) => {
        await authApi.post(API_ENDPOINTS.AUTH.LOGIN, loginRequest)
            .then((res) => {
                let token = res.headers["authorization"];

                accessToken.value = token;
                authApi.defaults.headers.common["Authorization"] = token;
                resourceApi.defaults.headers.common["Authorization"] = token;
            })
            .catch((err) => {
                throw err;
            })
    }

    const existsAccessToken = () => {
        return accessToken.value;
    }

    const refresh = async () => {
        await authApi.get(API_ENDPOINTS.AUTH.REFRESH_TOKEN)
            .then((res) => {
                let token = res.headers["authorization"];

                accessToken.value = token;
                authApi.defaults.headers.common["Authorization"] = token;
                resourceApi.defaults.headers.common["Authorization"] = token;
            })

    }

    const getAccessToken = () => {
        return accessToken.value;
    }

    const logout = async () => {
        await authApi.post(API_ENDPOINTS.AUTH.LOGOUT)
            .then(() => {})
            .catch((err) => {})
    }

    return {
        login,
        refresh,
        existsAccessToken,
        getAccessToken,
        logout,
    }
})
