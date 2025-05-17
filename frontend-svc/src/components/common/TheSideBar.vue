<template>
  <div class="sidebar-wrapper">
    <div class="sidebar" :class="{ 'mobile-view': !isDesktop }">
      <router-link to="/" class="logo-link">
        <img src="../../assets/icons/logo.png" alt="logo" class="logo" />
      </router-link>
      <div class="nav-links">
        <router-link to="/" class="sidebar-item" active-class="active">
          <IconHome />
          <span v-if="isDesktop">Все курсы</span>
        </router-link>

        <router-link
          to="/my_courses"
          class="sidebar-item"
          active-class="active"
        >
          <IconMyCourses />
          <span v-if="isDesktop">Мои курсы</span>
        </router-link>

        <router-link to="/profile" class="sidebar-item" active-class="active">
          <IconProfile />
          <span v-if="isDesktop">Профиль</span>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from "vue";
import IconHome from "./icons/IconHome.vue";
import IconMyCourses from "./icons/IconMyCourses.vue";
import IconProfile from "./icons/IconProfile.vue";

export default {
  components: {
    IconHome,
    IconMyCourses,
    IconProfile,
  },
  setup() {
    const isDesktop = ref(window.innerWidth >= 768);

    const handleResize = () => {
      isDesktop.value = window.innerWidth >= 768;
    };

    onMounted(() => {
      window.addEventListener("resize", handleResize);
    });

    onUnmounted(() => {
      window.removeEventListener("resize", handleResize);
    });

    return {
      isDesktop,
    };
  },
};
</script>

<style scoped>
.sidebar-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  height: 100%;
  width: 200px; /* Фиксированная ширина на десктопе */
  background-color: #f8f9fa;
  padding: 1.25rem;
  transition: width 0.3s ease-in-out;
  z-index: 10;
  display: flex;
  flex-direction: column;
}

.sidebar {
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: space-between;
}

.logo-link {
  display: flex;
  align-items: center;
  margin-bottom: 2rem;
}

.logo {
  max-width: 100%;
  height: auto;
}

.nav-links {
  display: flex;
  flex-direction: column; /* По умолчанию вертикальное расположение */
  justify-content: center;
  flex-grow: 1;
  gap: 2rem;
}

.sidebar-item {
  font-family: "Monserrat";
  display: flex;
  align-items: center;
  padding: 0.5rem 1rem;
  gap: 1rem;
  font-weight: 600;
  text-decoration: none;
  color: #767474; /* Цвет текста по умолчанию */
  border-radius: 3rem; /* Закругленные углы */
}

.sidebar-item:hover {
  color: white;
  background-color: #007bff;
}

.sidebar-item.active {
  color: white;
  background-color: #007bff; /* Синий фон для активной ссылки */
}

@media (max-width: 767px) {
  .sidebar-wrapper {
    width: 100%;
    height: auto;
    bottom: 0;
    left: 0;
    right: 0;
    top: auto;
    padding: 1rem 0;
    background-color: #f8f9fa;
  }

  .sidebar {
    width: 100%;
    padding: 0;
    flex-direction: column; /* На мобильных - вертикальное расположение */
    align-items: stretch; /* Растягиваем элементы по ширине */
  }

  .nav-links {
    flex-direction: row;
    justify-content: space-around;
    flex-wrap: wrap; /* Позволяем элементам переноситься на новую строку, если не помещаются */
    padding: 0.5rem 0; /* Добавляем небольшие отступы сверху и снизу */
  }

  .sidebar-item {
    flex-direction: column;
    align-items: center;
    padding: 0.5rem;
    gap: 0.25rem;
    width: 25%; /* Задаем ширину для каждого элемента на мобильных - 25% для 4 элементов в ряд */
    box-sizing: border-box; /* Чтобы padding не увеличивал общую ширину */
  }

  .logo-link,
  .logo,
  .sidebar-item span {
    display: none;
  }
}
</style>
