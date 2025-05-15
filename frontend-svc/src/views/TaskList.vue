<template>
  <div class="main-container">
    <TheHeader />
    <div class="content-wrapper">
      <aside class="sidebar"><TheSideBar /></aside>
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
import tasksData from "@/tasks.js";
import TheHeader from "@/components/common/TheHeader.vue";
import TheSideBar from "@/components/common/TheSideBar.vue";
import TaskCard from "@/components/task-list/TaskCard.vue";

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
