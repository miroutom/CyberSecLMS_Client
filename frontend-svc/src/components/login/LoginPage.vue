<template>
  <div class="registration-page">
    <div class="registration-container">
      <div class="logo_container">
        <img src="../../assets/icons/logo.png" alt="logo" />
      </div>
      <form @submit.prevent>
        <div class="input-group">
          <input type="text" id="username" required placeholder=" " />
          <label for="username">Номер телефона/email:</label>
        </div>
        <div class="input-group">
          <input type="password" id="password" required placeholder=" " />
          <label for="password">Пароль:</label>
        </div>
        <div class="button-group">
          <button type="submit">Зарегистрироваться</button>
          <router-link to="/auth" class="login-link"
            >Уже есть аккаунт? Войти</router-link
          >
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
@import "../../assets/styles/enter.css";
</style>
