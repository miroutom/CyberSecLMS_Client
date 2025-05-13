<template>
  <header class="header">
    <div class="header-title">
      <span>{{ activeTabTitle }}</span>
    </div>
    <div class="user-info">
      <span class="user-name">User Name</span>
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
            stroke="#8a8a92"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
        <div v-if="isOpen" class="dropdown-popup">
          <div class="dropdown-popup-content">
            <router-link to="/auth" class="dropdown-link">Выход</router-link>
          </div>
        </div>
      </div>
    </div>
  </header>
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f2f2ff;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  padding: 0 2% 0 2%;
  width: 90%;
  margin-left: auto;
  min-height: 10vh;
  box-sizing: border-box;
}

.header-title {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  flex-grow: 1;
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
  display: flex;
  align-items: center;
  white-space: nowrap;
}

.user-name {
  font-family: "Montserrat";
  font-style: normal;
  font-weight: 600;
  font-size: 1rem;
  line-height: 1.2;
  color: #8a8a92;
  margin-right: 5%;
  white-space: nowrap;
}

.user-icon {
  width: 8vh;
  height: 8vh;
  border-radius: 50%;
  margin-right: 5%;
}

.dropdown {
  position: relative;
  cursor: pointer;
}

.dropdown-popup {
  position: absolute;
  top: 150%;
  right: 0;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  z-index: 1000;
  width: 10vw;
}

.dropdown-popup-content {
  padding: 10px;
}

.dropdown-link {
  display: block;
  padding: 8px 0 8px 0;
  text-decoration: none;
  color: white;
  border-radius: 5px;
  background-color: rgb(220, 4, 4);
  font-weight: bold;
  width: 100%;
  text-align: center;
}

.dropdown-link:hover {
  background-color: rgb(192, 1, 1);
}
</style>
