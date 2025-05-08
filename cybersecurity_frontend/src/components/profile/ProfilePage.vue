<template>
  <div class="profile-page">
    <TheHeader />
    <div class="content-wrapper">
      <TheSideBar />
      <main class="profile-content">
        <section class="user-info">
          <div class="welcome-message">
            <div class="welcome-text">
              <h2>Привет, {{ username }}!</h2>
              <p><span class="contact-label">Email:</span> {{ email }}</p>
              <p><span class="contact-label">Телефон:</span> {{ phone }}</p>
              <button
                @click="
                  showSettings = true;
                  console.log(showSettings);
                "
                class="settings-button"
              >
                <img src="../../assets/icons/settings.svg" alt="Настройки" />
                Настройки
              </button>
            </div>
            <img
              src="../../assets/icons/welcome-image.png "
              alt=""
              class="welcome-image"
            />
          </div>

          <div class="stats-security">
            <div class="security-basics">
              <h3>Изучай кибербезопасность:</h3>
              <div class="cards-row">
                <MaterialsCard />
                <MaterialsCard />
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
    <SettingsPopup :isVisible="showSettings" @close="showSettings = false" />
  </div>
</template>

<script>
import TheHeader from "../common/TheHeader.vue";
import TheSideBar from "../common/TheSideBar.vue";
import MaterialsCard from "./MaterialsCard.vue";
import SettingsPopup from "./SettingsPopup.vue";

export default {
  components: {
    TheHeader,
    TheSideBar,
    MaterialsCard,
    SettingsPopup,
  },
  data() {
    return {
      username: "User Name",
      email: "user@example.com",
      phone: "+7 (123) 456-78-90",
      solvedTasks: 120,
      completedCourses: 5,
      showSettings: false,
    };
  },
};
</script>

<style scoped>
.profile-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.content-wrapper {
  display: flex;
  flex: 1;
  gap: 20px;
  padding: 20px 20px 20px 0;
}

.profile-content {
  flex: 1;
  padding: 20px;
}

.user-info {
  /* styles user-info */
}

.welcome-message {
  width: 85%;
  margin-left: auto;
  margin-bottom: 20px;
  margin-top: 70px;
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
  font-size: 20px;
}

.settings-button img {
  height: 24px;
  width: 24px;
  margin-right: 10px;
}

.settings-button:hover {
  text-decoration: underline;
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

.cards-row {
  display: flex;
  gap: 20px;
}
.learn-more {
  /* link styles */
}
.user-stats {
  background-color: red;
  width: 450px;
  height: 400px;
  border-radius: 20px;
}
.stat {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}
</style>
