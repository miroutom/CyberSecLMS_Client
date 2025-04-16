<template>
  <header class="header">
    <img src="../../assets/icons/logo.png" alt="logo" class="logo" />
    <div class="header-title">
      <span>{{ activeTabTitle }}</span>
      <div class="underline"></div>
    </div>
    <div class="user-info">
      <img src="user-icon.png" alt="User Icon" class="user-icon" />
      <span>User Name</span>
    </div>
  </header>
</template>

<script>
export default {
  name: "TheHeader",
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
};
</script>

<style scoped>
.header {
  width: 100%;
  height: 80px;
  display: grid;
  grid-template-columns: auto 1fr auto;
  justify-content: center;
  align-items: center;
  background-color: #f2f2ff;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.logo {
  padding: 20px;
  width: 150px;
}

.header-title {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-self: center;
  position: relative;
}

.header-title span {
  font-family: "Montserrat";
  font-style: normal;
  font-weight: 600;
  font-size: 24px;
  line-height: 29px;
  text-align: center;
  color: #3764ed;
}

.underline {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #3764ed;
}

.user-info {
  margin-left: auto;
  display: flex;
  align-items: center;
}

.user-icon {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-right: 10px;
}
</style>
