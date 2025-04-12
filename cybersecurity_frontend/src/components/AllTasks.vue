<template>
  <div class="main-container">
    <TheHeader />
    <div class="content-wrapper">
      <TheSideBar />
      <main class="all-tasks">
        <router-link
          v-for="vulnerability in vulnerabilities"
          :key="vulnerability.name"
          :to="{
            name: 'VulnerabilityTasks',
            params: { vulnerability: vulnerability.name },
          }"
          class="vulnerability-card"
          :style="{ backgroundColor: vulnerability.color }"
        >
          {{ vulnerability.name }}
        </router-link>
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
        { name: "XSS", color: "#8ab5ff" },
        { name: "CSRF", color: "#a0efde" },
        { name: "SQL Injection", color: "#ffbe8a" },
        { name: "XSS", color: "#8ab5ff" },
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
}

.content-wrapper {
  display: flex;
  flex-grow: 1;
}

.all-tasks {
  flex-grow: 1;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  padding: 20px;
  margin-top: 10px;
  grid-auto-rows: 400px;
}

.vulnerability-card {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  border-radius: 15px;
  text-decoration: none;
  color: #333;
  transition: background-color 0.3s ease;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  font-weight: bold;
  font-size: 1.2em;
}

.vulnerability-card:hover {
  transform: translateY(-5px);
}
</style>
