<template>
  <header class="header">
    <div class="header-title">
      <span>{{ activeTabTitle }}</span>
    </div>
    <div class="user-info">
      <span>User Name</span>
      <img
        src="../../assets/icons/user-icon.png"
        alt="User Icon"
        class="user-icon"
      />
      <div class="dropdown">
        <svg
          @click="toggleDropdown"
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M6 9L12 15L18 9"
            stroke="black"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
        <div v-if="isOpen" class="dropdown-content">
          <a href="#">Настройки</a>
          <a href="#">Выход</a>
        </div>
      </div>
    </div>
  </header>
  <div v-if="isOpen" class="dropdown-popup">
    <div class="dropdown-popup-content">
      <a href="#">Настройки</a>
      <a href="#">Выход</a>
    </div>
  </div>
</template>

<script>
export default {
  name: "TheHeader",
  data() {
    return {
      isOpen: false,
    };
  },
  computed: {
    activeTabTitle() {
      switch (this.$route.path) {
        case "/":
          return "Все курсы";
        case "/my_courses":
          return "Мои курсы";
        case "/statistics":
          return "Статистика";
        case "/profile":
          return "Профиль";
        case "/tasks/XSS":
          return "XSS";
        case "/tasks/CSRF":
          return "CSRF";
        default:
          return "null";
      }
    },
  },
  methods: {
    toggleDropdown() {
      this.isOpen = !this.isOpen;
    },
  },
};
</script>

<style scoped>
.header {
  width: 85%;
  min-height: 8vh;
  margin-left: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f2f2ff;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
}

.header-title {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-grow: 1;
  text-align: center;
}

.header-title span {
  font-family: "Montserrat";
  font-style: normal;
  font-weight: 600;
  font-size: 1.5rem;
  line-height: 1.2;
  text-align: center;
  color: #3764ed;
}

.user-info {
  margin-left: auto;
  display: flex;
  align-items: center;
  margin-right: 10px;
}

.user-icon {
  width: 30%;
  aspect-ratio: 1/1;
  border-radius: 50%;
  margin-right: 10px;
}

.dropdown {
  position: relative;
  cursor: pointer;
}

.dropdown-content {
  display: none;
}

.dropdown-popup {
  position: absolute;
  top: 8%;
  right: 2%;
  width: 200px;
  background-color: #fff;
  border-radius: 20px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

.dropdown-popup-content {
  padding: 10px;
}

.dropdown-popup-content a {
  display: block;
  padding: 8px;
  text-decoration: none;
  color: black;
}

.dropdown-popup-content a:hover {
  background-color: #f2f2ff;
  border-radius: 10px;
}
</style>
