<template>
  <div class="profile-page">
    <TheHeader />
    <div class="content-wrapper">
      <aside class="sidebar"><TheSideBar /></aside>
      <main class="profile-content">
        <div v-if="loading" class="loading">Загрузка профиля...</div>
        <div v-else-if="error" class="error">
          {{ error }}
        </div>
        <section v-else class="user-info">
          <div class="welcome-message">
            <div class="welcome-text">
              <h2>Привет, {{ username }}!</h2>
              <p><span class="contact-label">Email:</span> {{ email }}</p>
              <p><span class="contact-label">Телефон:</span> {{ phone }}</p>
              <button class="settings-button" @click="showSettings = true">
                <img src="@/assets/icons/settings.svg" alt="Settings" />
                Настройки профиля
              </button>
            </div>
            <img
              src="@/assets/icons/welcome-image.png"
              alt=""
              class="welcome-image"
            />
          </div>

          <div class="stats-security">
            <div class="security-basics">
              <h3>Изучай кибербезопасность:</h3>
              <div class="cards-row">
                <MaterialsCard
                  v-for="material in materials"
                  :key="material.title"
                  :title="material.title"
                  :description="material.description"
                  :progress="material.progress"
                />
              </div>
            </div>
            <div class="user-stats">
              <h3>Статистика</h3>
              <div class="stat">
                <span class="stat-label">Решенные задачи:</span>
                <span class="stat-value">{{ solvedTasks }}</span>
              </div>
              <div class="stat">
                <span class="stat-label">Пройденные курсы:</span>
                <span class="stat-value">{{ completedCourses }}</span>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>
    <SettingsPopup
      :isVisible="showSettings"
      :initialData="{ username, email, phone }"
      @close="showSettings = false"
      @save="handleSettingsSave"
    />
  </div>
</template>

<script>
import TheHeader from "@/components/common/TheHeader.vue"
import TheSideBar from "@/components/common/TheSideBar.vue"
import MaterialsCard from "@/components/profile/MaterialsCard.vue"
import SettingsPopup from "@/components/profile/SettingsPopup.vue"
import { profileService } from "@/services/profileService"

export default {
  components: {
    TheHeader,
    TheSideBar,
    MaterialsCard,
    SettingsPopup,
  },
  data() {
    return {
      username: "",
      email: "",
      phone: "",
      solvedTasks: 0,
      completedCourses: 0,
      materials: [],
      showSettings: false,
      loading: false,
      error: null,
    }
  },
  async created() {
    try {
      this.loading = true
      const response = await profileService.getProfile()
      const {
        username,
        email,
        phone,
        solvedTasks,
        completedCourses,
        materials,
      } = response.profile

      this.username = username
      this.email = email
      this.phone = phone
      this.solvedTasks = solvedTasks
      this.completedCourses = completedCourses
      this.materials = materials
    } catch (error) {
      this.error = error.error || "Failed to load profile"
      console.error("Error loading profile:", error)
    } finally {
      this.loading = false
    }
  },
  methods: {
    async handleSettingsSave(newData) {
      try {
        // Here you would typically make an API call to update the profile
        // For now, we'll just update the local data
        this.username = newData.username
        this.email = newData.email
        this.phone = newData.phone

        // If you have a profile service, you would call it like this:
        // await profileService.updateProfile(newData)
      } catch (error) {
        console.error("Error updating profile:", error)
        // Handle error appropriately
      }
    },
  },
}
</script>

<style scoped>
.profile-page {
  display: grid;
  grid-template-rows: auto 1fr;
  grid-template-columns: 1fr;
}

.content-wrapper {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 20px;
  padding: 20px;
}

.profile-content {
  flex: 1;
  padding: 20px;
}

.user-info {
  /* styles user-info */
}

.welcome-message {
  margin-left: auto;
  margin-bottom: 20px;
  margin-top: 50px;
  background-color: #f2f2ff;
  border-radius: 20px;
  padding: 0px 20px 0px 20px;
  display: flex;
  align-items: center;
}

.welcome-text {
  color: #3764ed;
}

.welcome-image {
  margin-top: -80px;
  height: 300px;
  width: 400px;
  margin-left: auto;
}

.contact-label {
  font-weight: bold;
  color: #3764ed;
}

.settings-button {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border: none;
  color: #3764ed;
  cursor: pointer;
  background-color: transparent;
  font-family: "Montserrat", sans-serif;
  font-weight: 600;
  font-size: 16px;
  margin-top: 10px;
  transition: all 0.3s ease;
}

.settings-button img {
  height: 20px;
  width: 20px;
  margin-right: 8px;
}

.settings-button:hover {
  text-decoration: underline;
  transform: scale(1.05);
}

.stats-security {
  display: flex;
  gap: 20px;
  margin-left: auto;
}

.security-basics {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 20px;
  height: 400px;
  width: 700px;
  margin-left: auto;
}

h3 {
  font-weight: 700;
  font-size: 24px;
  color: #3764ed;
}

.cards-row {
  display: flex;
  gap: 20px;
}

.user-stats {
  background-color: rgb(226, 225, 225);
  width: 450px;
  height: 400px;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.loading {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
  color: #666;
}

.error {
  text-align: center;
  padding: 2rem;
  color: #dc3545;
  font-size: 1.2rem;
}
</style>
