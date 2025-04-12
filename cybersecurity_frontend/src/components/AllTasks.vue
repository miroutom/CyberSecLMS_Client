<template>
  <div class="main-container">
    <TheHeader />
    <div class="content-wrapper">
      <TheSideBar />
      <main class="all-tasks">
        <div
          class="card-container"
          v-for="vulnerability in vulnerabilities"
          :key="vulnerability.name"
        >
          <router-link
            :to="{
              name: 'VulnerabilityTasks',
              params: { vulnerability: vulnerability.name },
            }"
            class="card-link"
          >
            <div class="vulnerability-card">
              <div
                class="card-top"
                :style="{
                  background: vulnerability.gradient,
                }"
              >
                <h2
                  class="card-title"
                  :style="{ backgroundImage: vulnerability.titleColor }"
                >
                  {{ vulnerability.name }}
                </h2>
              </div>
              <div class="card-content">
                <p class="card-description">{{ vulnerability.description }}</p>
                <div
                  class="task-count"
                  :style="{ color: vulnerability.accentColor }"
                >
                  <span>{{ vulnerability.taskCount }} заданий</span>
                  <hr
                    class="line"
                    :style="{ borderColor: vulnerability.accentColor }"
                  />
                </div>
              </div>
            </div>
          </router-link>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import TheHeader from "./TheHeader.vue";
import TheSideBar from "./TheSideBar.vue";

export default {
  components: {
    TheHeader,
    TheSideBar,
  },
  name: "AllTasks",
  data() {
    return {
      vulnerabilities: [
        {
          name: "XSS",
          gradient: "linear-gradient(180deg, #F2F2FF 0%, #3764ED 100%)",
          titleColor:
            "linear-gradient(90deg, #3764ED 0%, #2C4FBC 50%, #1F3987 100%)",
          description:
            "XSS уязвимости - внедрение вредоносных скриптов на веб-страницы.",
          taskCount: 10,
          accentColor: "#3764ED",
        },
        {
          name: "CSRF",
          gradient: "linear-gradient(180deg, #F2F2FF 0%, #16B593 100%)",
          titleColor:
            "linear-gradient(90deg, #04916E 0%, #046E4C 50%, #004A2E 100%)",
          description: "CSRF уязвимости - подделка межсайтовых запросов.",
          taskCount: 15,
          accentColor: "#3AE8C5",
        },
        {
          name: "SQL Injection",
          gradient: "linear-gradient(180deg, #FEF3E9 0%, #F9A866 100%)",
          titleColor:
            "linear-gradient(90deg, #F88F3A 0%, #DE7620 50%, #C6691D 100%)",
          description:
            "SQL Injection - внедрение вредоносного кода для манипуляции базой данных.",
          taskCount: 12,
          accentColor: "#F9A866",
        },
        // ... other vulnerabilities
      ],
    };
  },
};
</script>

<style scoped>
.main-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  font-family: "Arial", sans-serif;
}
.content-wrapper {
  display: flex;
  flex-grow: 1;
}

.all-tasks {
  flex-grow: 1;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  padding: 20px;
  margin-top: 10px;
}

.card-link {
  text-decoration: none;
}

.card-container {
  display: flex;
  flex-direction: column;
}

.vulnerability-card {
  width: 300px;
  height: 400px;
  background: #f2f2ff;
  border-radius: 30px;
  overflow: hidden;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  display: flex;
  flex-direction: column;
  font-family: "Montserrat", sans-serif;
}

.vulnerability-card:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2); /* Увеличиваем размытие и смещение тени */
}

.card-top {
  min-height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.card-content {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.card-title {
  font-weight: 800;
  font-size: 40px;
  line-height: 49px;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  padding: 20px;
  text-align: center;
}

.card-description {
  font-weight: 600;
  font-size: 16px;
  line-height: 20px;
  color: #000000;
  margin-bottom: 20px;
}
.task-count {
  font-weight: 600;
  font-size: 14px;
  line-height: 17px;
  text-align: center;
  margin-bottom: 10px;
}
.line {
  width: 85px;
  height: 0px;
  border: 1px solid;
  margin: 5px auto 0;
}
</style>
