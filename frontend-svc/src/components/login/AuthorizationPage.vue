<template>
  <div class="registration-page">
    <div class="registration-container">
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
.registration-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  font-family: sans-serif;
  background-image: url("../../assets/login_background.png");
  background-size: cover;
  background-repeat: no-repeat;
}

.registration-container {
  background-color: #fff;
  padding: 30px;
  border-radius: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: 350px;
}

.logo_container {
  padding-bottom: 20px;
  text-align: center;
}

.input-group {
  position: relative;
  margin-bottom: 15px;
}

input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

label {
  position: absolute;
  top: 10px;
  left: 10px;
  color: #999;
  transition: 0.2s;
  pointer-events: none;
}

input:focus + label,
input:not(:placeholder-shown) + label {
  top: -12px;
  left: 8px;
  font-size: 12px;
  background-color: white;
  padding: 0 5px;
  color: #3764ed;
}

.button-group {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px;
}

button {
  background-color: #3764ed;
  color: #fff;
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  width: 100%;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #0056b3;
}

a {
  text-decoration: none;
  color: #3764ed;
  margin-top: 10px;
  font-size: 0.9em;
}

a:hover {
  text-decoration: underline;
}

.error {
  color: red;
  margin-top: 10px;
}
</style>
