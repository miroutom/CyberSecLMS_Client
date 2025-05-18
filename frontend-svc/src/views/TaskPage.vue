<template>
  <div class="app-layout">
    <TheHeader />
    <div class="content-wrapper">
      <aside class="sidebar"><TheSideBar /></aside>
      <main class="task-page-container">
        <div v-if="loading" class="loading">Загрузка задач...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <template v-else>
          <div class="navigation">
            <button @click="previousTask" :disabled="currentTaskIndex === 0">
              Предыдущее
            </button>
            <span>{{ currentTask.title }}</span>
            <button
              @click="nextTask"
              :disabled="currentTaskIndex === tasks.length - 1"
            >
              Следующее
            </button>
          </div>
          <div class="task-description">
            <div v-html="currentTask.description"></div>
          </div>
          <div class="task-content">
            <div class="app-frame">
              <AppFrame
                :taskPath="currentTask.path"
                @code-updated="updateCode"
              />
            </div>
            <div class="code-editor">
              <CodeEditor
                v-model="currentTask.code"
                :language="currentTask.language"
              />
            </div>
          </div>
        </template>
      </main>
    </div>
  </div>
</template>

<script>
import TheHeader from "@/components/common/TheHeader.vue"
import TheSideBar from "@/components/common/TheSideBar.vue"
import AppFrame from "@/components/task/AppFrame.vue"
import CodeEditor from "@/components/task/CodeEditor.vue"
import { taskService } from "@/services/taskService"

export default {
  components: { TheHeader, TheSideBar, AppFrame, CodeEditor },
  data() {
    return {
      tasks: [],
      currentTaskIndex: 0,
      loading: false,
      error: null,
    }
  },
  computed: {
    currentTask() {
      return this.tasks[this.currentTaskIndex] || {}
    },
    vulnerability() {
      return this.$route.params.vulnerability
    },
  },
  methods: {
    previousTask() {
      this.currentTaskIndex = Math.max(0, this.currentTaskIndex - 1)
    },
    nextTask() {
      this.currentTaskIndex = Math.min(
        this.tasks.length - 1,
        this.currentTaskIndex + 1
      )
    },
    updateCode(data) {
      this.currentTask.code = data.code
      this.currentTask.description = data.description
    },
    async fetchTasks() {
      try {
        this.loading = true
        const response = await taskService.getTasksByVulnerability(
          this.vulnerability
        )
        this.tasks = response.tasks.map((task) => ({
          ...task,
          code: "", // Initialize code property for each task
        }))
      } catch (error) {
        this.error = error.error || "Failed to load tasks"
        console.error("Error loading tasks:", error)
      } finally {
        this.loading = false
      }
    },
  },
  async created() {
    await this.fetchTasks()
  },
}
</script>

<style scoped>
.app-layout {
  display: grid;
  grid-template-rows: auto 1fr; /* Хедер, затем контент */
  grid-template-columns: 1fr;
  min-height: 100vh;
  /* Убираем потенциальные margin и padding по умолчанию */
  margin: 0;
  padding: 0;
}

.content-wrapper {
  display: grid;
  grid-template-columns: 200px 1fr; /* Сайдбар и основной контент */
  gap: 20px;
  padding: 20px;
  /* Занимаем все доступное пространство */
  height: 100%;
  width: 100%;
  box-sizing: border-box; /* Чтобы padding не увеличивал размер */
}

.task-page-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  margin: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
}

.navigation {
  background-color: #fff;
  padding: 15px 20px;
  border-bottom: 1px solid #ddd;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.navigation button {
  width: 25%;
  background-color: #3764ed;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.navigation button:hover {
  background-color: #0056b3;
}

.navigation button:disabled {
  background-color: #ccc;
  cursor: default;
}

.navigation span {
  font-weight: bold;
}

.task-description {
  padding: 20px;
  background-color: #fff;
  border-bottom: 1px solid #ddd;
}

.task-content {
  display: flex;
  flex: 1;
  gap: 20px;
  padding: 20px;
  background-color: #fff;
}

.app-frame,
.code-editor {
  flex: 1;
  height: 100%;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.app-frame iframe {
  width: 100%;
  height: 100%;
  border: none;
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
