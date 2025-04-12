import { createRouter, createWebHashHistory } from "vue-router";
import TaskPage from "@/components/TaskPage.vue";
import AllTasks from "@/components/AllTasks.vue";
import VulnerabilityTasks from "@/components/VulnerabilityTasks.vue";
import LoginPage from "@/components/LoginPage.vue";

const routes = [
  {
    path: "/",
    name: "AllTasks",
    component: AllTasks,
  },
  {
    path: "/login",
    name: "LoginPage",
    component: LoginPage,
  },
  {
    path: "/tasks/:vulnerability",
    name: "VulnerabilityTasks",
    component: VulnerabilityTasks,
    props: true,
  },
  {
    path: "/task/:vulnerability/:taskId",
    name: "TaskPage",
    component: TaskPage,
    props: true,
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
