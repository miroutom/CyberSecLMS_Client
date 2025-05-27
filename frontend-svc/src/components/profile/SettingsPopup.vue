<template>
  <div class="settings-popup" v-if="isVisible">
    <div class="popup-content">
      <h2 class="popup-title">Мой Профиль</h2>
      <form @submit.prevent="saveSettings">
        <div class="form-group">
          <label for="username">Username:</label>
          <input type="text" id="username" v-model="formData.username" />
        </div>
        <div class="form-group">
          <label for="email">Email:</label>
          <input type="email" id="email" v-model="formData.email" />
        </div>
        <div class="form-group">
          <label for="phone">Телефон:</label>
          <input type="tel" id="phone" v-model="formData.phone" />
        </div>
        <div class="buttons">
          <button type="submit" class="save-button" :disabled="isLoading">
            Сохранить
          </button>
          <button type="button" @click="closePopup" class="cancel-button">
            Отмена
          </button>
        </div>
      </form>
      <div v-if="error" class="error-message">
        {{ error }}
      </div>
    </div>
  </div>
</template>

<script>
import { profileService } from "@/services/profileService"

export default {
  name: "SettingsPopup",
  props: {
    isVisible: {
      type: Boolean,
      default: false,
    },
    initialData: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      formData: {
        username: "",
        email: "",
        phone: "",
      },
      isLoading: false,
      error: null,
    }
  },
  watch: {
    initialData: {
      immediate: true,
      handler(newData) {
        this.formData = { ...newData }
      },
    },
  },
  methods: {
    async saveSettings() {
      try {
        this.isLoading = true
        this.error = null

        const response = await profileService.updateProfile(this.formData)

        if (response.success) {
          this.$emit("save", response.profile)
          this.closePopup()
        }
      } catch (error) {
        this.error = error.error || "Failed to save settings"
        console.error("Error saving settings:", error)
      } finally {
        this.isLoading = false
      }
    },
    closePopup() {
      this.$emit("close")
    },
  },
}
</script>

<style scoped>
.settings-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.popup-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  max-width: 500px;
  width: 90%;
}

.popup-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #3764ed;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  color: #3764ed;
  font-weight: 600;
}

input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

.buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.save-button,
.cancel-button {
  padding: 8px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
}

.save-button {
  background-color: #3764ed;
  color: white;
}

.cancel-button {
  background-color: #eee;
  color: #333;
}

.save-button:hover {
  background-color: #2d4fc7;
}

.cancel-button:hover {
  background-color: #ddd;
}

.save-button:disabled {
  background-color: #a0a0a0;
  cursor: not-allowed;
}

.error-message {
  color: #ff4444;
  margin-top: 10px;
  text-align: center;
}
</style>
