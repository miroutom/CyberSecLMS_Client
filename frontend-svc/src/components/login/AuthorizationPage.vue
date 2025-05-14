<template>
  <div class="registration-page">
    <div class="auth-container">
      <div class="logo_container">
        <img src="../../assets/icons/logo.png" alt="logo" />
      </div>
      <form @submit.prevent="login">
        <div class="input-group">
          <input
            type="text"
            id="username"
            v-model="username"
            required
            placeholder=" "
          />
          <label for="username">Номер телефона/email:</label>
        </div>
        <div class="input-group">
          <input
            type="password"
            id="password"
            v-model="password"
            required
            placeholder=" "
          />
          <label for="password">Пароль:</label>
        </div>
        <div class="button-group">
          <button type="submit">Войти</button>
        </div>
        <div v-if="error" class="error">{{ error }}</div>
      </form>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      username: "",
      password: "",
      error: null,
    };
  },
  methods: {
    login() {
      axios
        .post("/api/login", {
          //backend URL
          username: this.username,
          password: this.password,
        })
        .then((response) => {
          if (response.status === 200) {
            // Successful login
            localStorage.setItem("token", response.data.token); // Saving token
            localStorage.setItem("userName", response.data.username);
            this.$router.push("/"); // Redirecting to main page
            this.error = null;
          } else {
            this.error = "Неожиданный ответ сервера";
          }
        })
        .catch((error) => {
          if (error.response && error.response.status === 401) {
            this.error = "Неверный логин или пароль";
          } else if (error.response) {
            this.error = "Ошибка: " + error.response.data.message;
          } else {
            this.error = "Ошибка при авторизации";
          }
        });
    },
  },
};
</script>

<style scoped>
@import "../../assets/styles/enter.css";
</style>
