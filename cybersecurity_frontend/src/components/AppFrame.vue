<template>
  <iframe :src="taskPath" frameborder="0" @load="extractCode"></iframe>
</template>

<script>
export default {
  props: ["taskPath"],
  methods: {
    extractCode() {
      const iframeDocument = this.$el.contentWindow.document;
      const codeElement = iframeDocument.getElementById("code");
      const initialCode = codeElement ? codeElement.textContent : "";

      // getting task description
      const descriptionElement =
        iframeDocument.getElementById("task-description");
      const descriptionHTML = descriptionElement
        ? descriptionElement.innerHTML
        : "";

      this.$emit("code-updated", {
        code: initialCode,
        description: descriptionHTML,
      });
    },
  },
};
</script>
