<template>
  <div class="main-container">
    <TheHeader />
    <div class="content-wrapper">
      <aside class="sidebar"><TheSideBar /></aside>
      <div class="vulnerability-tasks">
        <div v-if="loading" class="loading">Загрузка задач...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <div v-else class="task-cards-container">
          <TaskCard v-for="task in tasks" :key="task.id" :task="task" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import TheHeader from "@/components/common/TheHeader.vue"
import TheSideBar from "@/components/common/TheSideBar.vue"
import TaskCard from "@/components/task-list/TaskCard.vue"
import { taskService } from "@/services/taskService"

export default {
  components: {
    TheHeader,
    TheSideBar,
    TaskCard,
  },
  name: "TaskList",
  data() {
    return {
      tasks: [],
      loading: false,
      error: null,
    }
  },
  computed: {
    vulnerability() {
      return this.$route.params.vulnerability
    },
  },
  async created() {
    try {
      this.loading = true
      const response = await taskService.getTasksByVulnerability(
        this.vulnerability
      )
      this.tasks = response.tasks
    } catch (error) {
      this.error = error.error || "Failed to load tasks"
      console.error("Error loading tasks:", error)
    } finally {
      this.loading = false
    }
  },
}
</script>

<style scoped>
.content-wrapper {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 20px;
}

.vulnerability-tasks {
  padding: 20px;
  margin-left: 40px;
}

.task-cards-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-gap: 50px;
}

.loading,
.error {
  text-align: center;
  padding: 20px;
  font-size: 1.2em;
}

.error {
  color: #ff4444;
}
</style>
