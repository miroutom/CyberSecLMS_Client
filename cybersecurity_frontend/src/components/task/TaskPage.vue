<template>
  <div class="task-page-container">
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
        <AppFrame :taskPath="currentTask.path" @code-updated="updateCode" />
      </div>
      <div class="code-editor">
        <CodeEditor
          v-model="currentTask.code"
          :language="currentTask.language"
        />
        <button class="execute-button" @click="executeCode">
          Execute Code
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import AppFrame from "./AppFrame.vue";
import CodeEditor from "./CodeEditor.vue";

export default {
  components: { AppFrame, CodeEditor },
  data() {
    return {
      tasks: [
        {
          title: "Задание 1: XSS",
          path: "/vulnerable-app/xss.html",
          code: "", // Initial code is logged from html file
          language: "html",
          description: "",
        },
        {
          title: "Задание 2: CSRF",
          path: "/vulnerable-app/csrf.html",
          code: "",
          language: "javascript",
          description: "",
        },
      ],
      currentTaskIndex: 0,
    };
  },
  computed: {
    currentTask() {
      return this.tasks[this.currentTaskIndex];
    },
  },
  methods: {
    previousTask() {
      this.currentTaskIndex = Math.max(0, this.currentTaskIndex - 1);
    },
    nextTask() {
      this.currentTaskIndex = Math.min(
        this.tasks.length - 1,
        this.currentTaskIndex + 1
      );
    },
    updateCode(data) {
      this.currentTask.code = data.code;
      this.currentTask.description = data.description; // saving description
    },
    executeCode() {
      console.log("Executing code:", this.currentTask.code);
      // Add your code execution logic here
      // For example, you might send the code to a backend API
      alert(`Code executed! Check console for details.\nLanguage: ${this.currentTask.language}
  },
};
</script>

<style scoped>
body {
  font-family: sans-serif;
  background-color: #d13c3c;
  color: #333;
  margin: 0;
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

.execute-button {
  position: absolute;
  bottom: 15px;
  right: 15px;
  background-color: #28a745;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s ease;
}

.execute-button:hover {
  background-color: #218838;
  transform: translateY(-2px);
  box-shadow: 0 2px 5px rgba(0,0,0,0.2);

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
</style>
