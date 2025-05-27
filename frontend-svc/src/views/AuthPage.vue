<template>
  <div class="registration-page">
    <div class="registration-container">
      <div class="logo_container">
        <img src="@/assets/icons/logo.png" alt="logo" />
      </div>
      <form @submit.prevent="handleSubmit">
        <div class="input-group">
          <input
            type="text"
            id="username"
            v-model="formData.username"
            required
            placeholder=" "
          />
          <label for="username">Номер телефона/email:</label>
        </div>
        <div class="input-group">
          <input
            type="password"
            id="password"
            v-model="formData.password"
            required
            placeholder=" "
          />
          <label for="password">Пароль:</label>
        </div>
        <div class="button-group">
          <button type="submit" class="login-button" :disabled="isLoading">
            {{ isLoading ? "Вход..." : "Войти" }}
          </button>
          <router-link to="/register" class="login-link">
            Нет аккаунта? Зарегистрироваться
          </router-link>
        </div>
        <div v-if="error" class="error-message">
          {{ error }}
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from "vue"
import { useRouter } from "vue-router"
import { authService } from "@/services/authService"

export default {
  name: "AuthPage",
  setup() {
    const router = useRouter()
    const isLoading = ref(false)
    const error = ref("")
    const formData = ref({
      username: "",
      password: "",
    })

    const handleSubmit = async () => {
      try {
        isLoading.value = true
        error.value = ""

        const response = await authService.login(formData.value)

        // Store the token in localStorage
        localStorage.setItem("token", response.token)

        // Redirect to home page or dashboard
        router.push("/")
      } catch (err) {
        error.value = err.error || "Ошибка при входе"
      } finally {
        isLoading.value = false
      }
    }

    return {
      formData,
      isLoading,
      error,
      handleSubmit,
    }
  },
}
</script>

<style scoped>
@import "@/assets/styles/enter.css";

.error-message {
  color: #ff4444;
  margin-top: 10px;
  text-align: center;
}

.login-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
</style>
