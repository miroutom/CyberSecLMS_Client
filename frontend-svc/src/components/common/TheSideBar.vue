<template>
  <div class="sidebar-wrapper">
    <div class="sidebar">
      <router-link to="/">
        <img src="../../assets/icons/logo.png" alt="logo" class="logo" />
      </router-link>
      <div class="nav-links">
        <router-link to="/" class="sidebar-item">
          <svg
            width="40"
            height="40"
            viewBox="0 0 40 40"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M5 15.1252L20 3.4585L35 15.1252V33.4585C35 34.3426 34.6488 35.1904 34.0237 35.8155C33.3986 36.4406 32.5507 36.7918 31.6667 36.7918H8.33333C7.44928 36.7918 6.60143 36.4406 5.97631 35.8155C5.35119 35.1904 5 34.3426 5 33.4585V15.1252Z"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
            <path
              d="M15 36.7917V20.125H25V36.7917"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
          Все курсы
        </router-link>

        <router-link to="/my_courses" class="sidebar-item">
          <svg
            width="40"
            height="40"
            class="icon"
            viewBox="0 0 40 40"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M35.2332 7.68345C34.3819 6.83179 33.3712 6.15619 32.2588 5.69525C31.1464 5.23432 29.954 4.99707 28.7499 4.99707C27.5457 4.99707 26.3534 5.23432 25.2409 5.69525C24.1285 6.15619 23.1178 6.83179 22.2665 7.68345L20.4999 9.45011L18.7332 7.68345C17.0137 5.96396 14.6816 4.99796 12.2499 4.99796C9.81815 4.99796 7.48603 5.96396 5.76654 7.68345C4.04705 9.40293 3.08105 11.7351 3.08105 14.1668C3.08105 16.5985 4.04705 18.9306 5.76654 20.6501L7.53321 22.4168L20.4999 35.3834L33.4665 22.4168L35.2332 20.6501C36.0849 19.7989 36.7605 18.7881 37.2214 17.6757C37.6823 16.5633 37.9196 15.3709 37.9196 14.1668C37.9196 12.9626 37.6823 11.7703 37.2214 10.6579C36.7605 9.54542 36.0849 8.53471 35.2332 7.68345Z"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
          Мои курсы
        </router-link>

        <router-link to="/statistics" class="sidebar-item">
          <svg
            width="40"
            height="40"
            class="icon"
            viewBox="0 0 40 40"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M37.1663 20H30.4997L25.4997 35L15.4997 5L10.4997 20H3.83301"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
          Статистика</router-link
        >
        <router-link to="/profile" class="sidebar-item">
          <svg
            width="40"
            height="40"
            viewBox="0 0 40 40"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M33.8337 35V31.6667C33.8337 29.8986 33.1313 28.2029 31.881 26.9526C30.6308 25.7024 28.9351 25 27.167 25H13.8337C12.0655 25 10.3699 25.7024 9.11961 26.9526C7.86937 28.2029 7.16699 29.8986 7.16699 31.6667V35"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
            <path
              d="M20.4997 18.3333C24.1816 18.3333 27.1663 15.3486 27.1663 11.6667C27.1663 7.98477 24.1816 5 20.4997 5C16.8178 5 13.833 7.98477 13.833 11.6667C13.833 15.3486 16.8178 18.3333 20.4997 18.3333Z"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
          Профиль</router-link
        >
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "TheSidebar",
};
</script>

<style scoped>
.sidebar-wrapper {
  display: flex;
  height: 100%;
}

.sidebar {
  width: 10%;
  background-color: #f8f9fa;
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: fixed;
  top: 0;
}

.logo {
  width: 150px;
}

.nav-links {
  margin: auto;
}

.sidebar-item {
  width: 90%;
  font-family: "Arial", sans-serif;
  display: flex;
  align-items: center;
  padding: 10px;
  text-decoration: none;
  color: #979797;
  margin-bottom: 5px;
  border-radius: 20px;
}
.sidebar-item svg {
  margin-right: 10px;
}
.sidebar-item:hover {
  background-color: #007bff;
  color: white;
}
</style>
