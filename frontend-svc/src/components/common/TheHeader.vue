<template>
  <header class="header">
    <div class="header-title">
      <span>{{ activeTabTitle }}</span>
    </div>
    <div class="user-info">
      <span class="user-name">User Name</span>
      <img
        src="../../assets/login_background.png"
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
            <router-link to="/auth" class="dropdown-link">
              <button class="logout-button">Выход</button>
            </router-link>
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
    }
  },
  computed: {
    activeTabTitle() {
      switch (this.$route.path) {
        case "/":
          return "Все курсы"
        case "/my_courses":
          return "Мои курсы"
        case "/statistics":
          return "Статистика"
        case "/profile":
          return "Профиль"
        case "/tasks/XSS":
          return "XSS"
        case "/tasks/CSRF":
          return "CSRF"
        default:
          return "null"
      }
    },
  },
  methods: {
    toggleDropdown() {
      this.isOpen = !this.isOpen
    },
  },
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 1rem 2rem;
  background-color: #f2f2ff;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
}

.header-title {
  margin: 0 auto;
}

.user-info {
  display: flex;
  align-items: center;
  white-space: nowrap;
}

.header-title span {
  font-family: "Montserrat";
  font-style: normal;
  font-weight: 600;
  font-size: 1.5rem;
  line-height: 1.2;
  color: #3764ed;
}

.user-name {
  font-family: "Montserrat";
  font-style: normal;
  font-weight: 600;
  font-size: 1rem;
  line-height: 1.2;
  color: #8a8a92;
  white-space: nowrap;
}

.user-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  margin-left: 10px;
  margin-right: 10px;
}

.dropdown {
  position: relative;
  cursor: pointer;
}

.dropdown-popup {
  position: absolute;
  top: 200%;
  right: 0;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  z-index: 1000;
  width: auto;
  height: auto;
  min-width: 150px;
  padding: 10px;
}

.logout-button {
  background-color: #dc0404;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  width: 100%;
  font-weight: bold;
}

.logout-button:hover {
  background-color: #c00101;
}
</style>
