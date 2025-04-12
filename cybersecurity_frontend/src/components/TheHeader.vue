<template>
  <header class="header">
    <img src="../assets/icons/logo.png" alt="logo" />
    <div class="user-info">
      <img src="user-icon.png" alt="User Icon" class="user-icon" />
      <span>User Name</span>
    </div>
  </header>
</template>

<script>
export default {
  name: "TheHeader",
};
</script>

<style scoped>
.header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.user-info {
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
