import { createRouter, createWebHashHistory } from "vue-router";
import TaskPage from "@/components/task/TaskPage.vue";
import AllTasks from "@/components/all_courses/AllTasks.vue";
import LoginPage from "@/components/login/LoginPage.vue";
import MyCourses from "@/components/my_courses/MyCourses.vue";
import TaskList from "@/components/task-list/TaskList.vue";

const routes = [
  {
    path: "/login",
    name: "LoginPage",
    component: LoginPage,
  },
  {
    path: "/",
    name: "AllTasks",
    component: AllTasks,
  },
  {
    path: "/my_courses",
    name: "MyCourses",
    component: MyCourses,
  },
  {
    path: "/tasks/:vulnerability",
    name: "TaskList",
    component: TaskList,
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
