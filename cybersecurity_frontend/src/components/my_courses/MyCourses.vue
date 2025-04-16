<template>
  <div class="main-container">
    <TheHeader />
    <div class="content-wrapper">
      <TheSideBar />
      <main class="all-tasks">
        <MyCourseCard
          v-for="vulnerability in vulnerabilities"
          :key="vulnerability.name"
          :title="vulnerability.name"
          :description="vulnerability.description"
          :courseId="vulnerability.name"
          :gradient="vulnerability.gradient"
          :title-color="vulnerability.titleColor"
          :accent-color="vulnerability.accentColor"
          :progress="vulnerability.progress"
        />
      </main>
    </div>
  </div>
</template>

<script>
import TheHeader from "../common/TheHeader.vue";
import TheSideBar from "../common/TheSideBar.vue";
import MyCourseCard from "./MyCourseCard.vue";

export default {
  components: {
    TheHeader,
    TheSideBar,
    MyCourseCard,
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
          progress: 50,
          accentColor: "#3764ED",
        },
        {
          name: "CSRF",
          gradient: "linear-gradient(180deg, #F2F2FF 0%, #16B593 100%)",
          titleColor:
            "linear-gradient(90deg, #04916E 0%, #046E4C 50%, #004A2E 100%)",
          description: "CSRF уязвимости - подделка межсайтовых запросов.",
          progress: 15,
          accentColor: "#3AE8C5",
        },
        {
          name: "SQL Injection",
          gradient: "linear-gradient(180deg, #FEF3E9 0%, #F9A866 100%)",
          titleColor:
            "linear-gradient(90deg, #F88F3A 0%, #DE7620 50%, #C6691D 100%)",
          description:
            "SQL Injection - внедрение вредоносного кода для манипуляции базой данных.",
          progress: 40,
          accentColor: "#F9A866",
        },
        {
          name: "XSS",
          gradient: "linear-gradient(180deg, #F2F2FF 0%, #3764ED 100%)",
          titleColor:
            "linear-gradient(90deg, #3764ED 0%, #2C4FBC 50%, #1F3987 100%)",
          description:
            "XSS уязвимости - внедрение вредоносных скриптов на веб-страницы.",
          progress: 50,
          accentColor: "#3764ED",
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
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 20px;
}

.all-tasks {
  flex-grow: 1;
  padding: 20px;
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 50px;
}
</style>
