<template>
  <div class="settings-popup" v-if="isVisible">
    <div class="popup-content">
      <h2 class="popup-title">Настройки</h2>
      <form @submit.prevent="saveSettings">
        <div class="form-group">
          <label for="photo">Фото профиля:</label>
          <input type="file" id="photo" @change="handlePhotoUpload" />
          <img
            v-if="profilePhoto"
            :src="profilePhoto"
            alt="Фото профиля"
            class="profile-photo"
          />
        </div>
        <div class="form-group">
          <label for="firstName">Имя:</label>
          <input type="text" id="firstName" v-model="firstName" />
        </div>
        <div class="form-group">
          <label for="lastName">Фамилия:</label>
          <input type="text" id="lastName" v-model="lastName" />
        </div>
        <div class="form-group">
          <label for="email">Email:</label>
          <input type="email" id="email" v-model="email" />
        </div>
        <div class="form-group">
          <label for="university">Университет:</label>
          <input type="text" id="university" v-model="university" />
        </div>
        <div class="form-group">
          <label for="course">Курс:</label>
          <input type="text" id="course" v-model="course" />
        </div>
        <div class="buttons">
          <button type="submit" class="save-button">Сохранить</button>
          <button type="button" @click="closePopup" class="cancel-button">
            Отмена
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: "SettingsPopup",
  props: {
    isVisible: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      profilePhoto: null,
      firstName: "",
      lastName: "",
      email: "",
      university: "",
      course: "",
    };
  },
  methods: {
    handlePhotoUpload(event) {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.profilePhoto = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    },
    saveSettings() {
      console.log("Сохраняем настройки:", this.$data);
      this.closePopup();
    },
    closePopup() {
      this.$emit("close");
      this.profilePhoto = null;
    },
  },
};
</script>

<style scoped>
.settings-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000; /* */
}

.popup-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  max-width: 500px; /* */
}

.popup-title {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input[type="text"],
input[type="email"],
input[type="file"] {
  /* */
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

.profile-photo {
  max-width: 150px;
  margin-top: 10px;
  display: block;
}

.buttons {
  display: flex;
  justify-content: flex-end; /* */
  margin-top: 20px;
}

.save-button,
.cancel-button {
  padding: 8px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.save-button {
  background-color: #3764ed; /* */
  color: white;
  margin-left: 10px; /* */
}

.cancel-button {
  background-color: #eee;
}
</style>
