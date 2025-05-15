import { createRouter, createWebHashHistory } from "vue-router";
import TaskPage from "@/views/TaskPage.vue";
import AllTasks from "@/views/AllTasks.vue";
import LoginPage from "@/views/LoginPage.vue";
import AuthorizationPage from "@/views/AuthorizationPage.vue";
import MyCourses from "@/views/MyCourses.vue";
import TaskList from "@/views/TaskList.vue";
import ProfilePage from "@/views/ProfilePage.vue";
import TestComponent from "@/views/TestComponent.vue";

const routes = [
  {
    path: "/test",
    name: "TestComponent",
    component: TestComponent,
  },
  {
    path: "/login",
    name: "LoginPage",
    component: LoginPage,
  },
  {
    path: "/auth",
    name: "Authorization",
    component: AuthorizationPage,
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
  {
    path: "/profile",
    name: "ProfilePage",
    component: ProfilePage,
    props: true,
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
