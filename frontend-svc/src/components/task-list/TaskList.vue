<template>
  <div class="main-container">
    <TheHeader />
    <div class="content-wrapper">
      <TheSideBar />
      <div class="vulnerability-tasks">
        <div class="task-cards-container">
          <TaskCard
            v-for="task in filteredTasks"
            :key="task.title"
            :task="task"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import tasksData from "../tasks.js"; // importing tasks data
import TheHeader from "../common/TheHeader.vue";
import TheSideBar from "../common/TheSideBar.vue";
import TaskCard from "./TaskCard.vue";

export default {
  components: {
    TheHeader,
    TheSideBar,
    TaskCard,
  },
  name: "TaskList",
  props: {},
  data() {
    return {
      allTasks: tasksData,
    };
  },
  computed: {
    vulnerability() {
      return this.$route.params.vulnerability;
    },
    filteredTasks() {
      return this.allTasks.filter(
        (task) => task.vulnerability === this.vulnerability
      );
    },
  },
};
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
</style>
