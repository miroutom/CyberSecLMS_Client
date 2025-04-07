<template>
  <MonacoEditor
    v-model="code"
    :options="editorOptions"
    @editorDidMount="editorDidMount"
    @change="logChange"
    language="javascript"
  />
</template>

<script>
import MonacoEditor from "vue-monaco-editor";

export default {
  name: "CodeEditor",
  components: {
    MonacoEditor,
  },
  data() {
    return {
      code: '// Напишите ваш код здесь\nconsole.log("Hello, Vulnerable App!");', // Initial code
      editorOptions: {
        selectOnLineNumbers: true,
        roundedSelection: false,
        readOnly: false,
        cursorStyle: "line",
        automaticLayout: true,
        theme: "vs-dark",
      },
    };
  },
  methods: {
    editorDidMount(editor, monaco) {
      console.log("Monaco Editor is ready!", editor, monaco);
    },
    logChange(newCode) {
      this.$emit("code-changed", newCode);
    },
  },
};
</script>

<style scoped>
.monaco-editor-container {
  height: 100%;
  display: flex;
}
</style>
