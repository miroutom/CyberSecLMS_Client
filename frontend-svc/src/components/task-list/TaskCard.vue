<template>
  <router-link
    :to="{
      name: 'TaskPage',
      params: { taskId: task.id, vulnerability: task.vulnerability },
    }"
    class="task-card"
    :style="{ backgroundColor: cardColor }"
  >
    <div class="task-card-content">
      <img :src="icon" alt="" class="task-icon" />
      <div class="text-content">
        <h3 class="task-title">{{ task.title }}</h3>
        <p class="task-description">{{ task.description }}</p>
      </div>
      <img
        src="@/assets/icons/chevron-right.svg"
        alt="chevron-right"
        class="chevron-icon"
      />
    </div>
  </router-link>
</template>

<script>
export default {
  name: "TaskCard",
  props: {
    task: {
      type: Object,
      required: true,
    },
  },
  computed: {
    icon() {
      switch (this.task.vulnerability) {
        case "XSS":
          return require("@/assets/icons/xss-icon.png");
        case "CSRF":
          return require("@/assets/icons/csrf-icon.png");
        default:
          return require("@/assets/icons/xss-icon.png");
      }
    },
    cardColor() {
      switch (this.task.vulnerability) {
        case "XSS":
          return "#F2F2FF";
        case "CSRF":
          return "#F1FFFF";
        default:
          return "#95a5a6";
      }
    },
  },
};
</script>

<style scoped>
.task-card {
  width: 550px;
  height: 250px;
  filter: drop-shadow(10px 10px 10px rgba(0, 0, 0, 0.1));
  border-radius: 20px;
  overflow: hidden;
  transition: transform 0.3s ease;
  text-decoration: none;
}

.task-card-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.task-icon {
  width: 200px;
  height: 200px;
}

.text-content {
  display: flex;
  flex-direction: column;
  margin-left: 20px;
  width: 215px;
}

.task-title {
  font-family: "Montserrat", sans-serif;
  font-weight: 700;
  font-size: 32px;
  line-height: 39px;
  margin-bottom: 10px;
  color: #3764ed;
}

.task-description {
  font-family: "Montserrat", sans-serif;
  font-weight: 400;
  font-size: 20px;
  line-height: 24px;
  color: #3764ed;
}

.chevron-icon {
  position: absolute;
  bottom: 20px;
  right: 20px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #3764ed;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

.chevron-link img {
  width: 48px;
  height: 48px;
}

.task-card:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.chevron-link:hover {
  filter: brightness(70%);
}
</style>
