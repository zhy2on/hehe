<script setup>

import {ref} from "vue";
import {useAuthStore} from "@/stores/auth.js";
import router from "@/routers/index.js";

const authStore = useAuthStore();

const loginForm = ref({
  email: "",
  password: "",
});

const handleLogin = async (event) => {
  event.preventDefault();

  await authStore.login(loginForm.value)
      .then(() => {
        router.push("/main")
      })
      .catch((error) => {
        console.log(error)
      })
}

</script>

<template>
  <h1>로그인 페이지</h1>

  <form @submit="handleLogin">
    <input v-model="loginForm.email"/>
    <input v-model="loginForm.password"/>
    <button>로그인</button>
  </form>

</template>

<style scoped>

</style>