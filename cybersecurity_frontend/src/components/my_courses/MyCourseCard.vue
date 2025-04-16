<template>
  <router-link
    :to="{ name: 'TaskList', params: { vulnerability: courseId } }"
    class="card-link"
  >
    <div class="vulnerability-card">
      <div class="card-top" :style="{ background: gradient }">
        <h2 class="card-title" :style="{ backgroundImage: titleColor }">
          {{ title }}
        </h2>
      </div>
      <div class="card-content">
        <p class="card-description">{{ description }}</p>
        <div class="progress-wrapper">
          <div class="progress-container" :style="{ color: accentColor }">
            <div
              class="progress-bar"
              :style="{ width: progress + '%', backgroundColor: accentColor }"
            ></div>
          </div>
          <span class="progress-text" :style="{ color: accentColor }"
            >{{ progress }}%</span
          >
        </div>
        <button
          class="continue-button"
          :style="{ backgroundColor: accentColor }"
        >
          Продолжить
        </button>
      </div>
    </div>
  </router-link>
</template>

<script>
export default {
  name: "MyCourseCard",
  props: {
    courseId: {
      type: String,
      required: true,
    },
    title: {
      type: String,
      required: true,
    },
    description: {
      type: String,
      required: true,
    },
    gradient: {
      type: String,
      required: true,
    },
    titleColor: {
      type: String,
      required: true,
    },
    accentColor: {
      type: String,
      required: true,
    },
    progress: {
      type: Number,
      required: true,
    },
  },
};
</script>

<style scoped>
.vulnerability-card {
  width: 300px;
  height: 400px;
  background: #f2f2ff;
  border-radius: 30px;
  overflow: hidden;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  display: flex;
  flex-direction: column;
  font-family: "Montserrat", sans-serif;
}

.vulnerability-card:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.card-top {
  min-height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.card-content {
  padding: 10px 20px 10px 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.card-title {
  font-weight: 800;
  font-size: 40px;
  line-height: 49px;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  padding: 20px;
  text-align: center;
}

.card-description {
  font-weight: 600;
  font-size: 16px;
  line-height: 20px;
  color: #000000;
  margin-bottom: 20px;
}

.progress-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.progress-container {
  width: 100%;
  height: 10px;
  background-color: #fffcfc;
  border-radius: 10px;
  border: 1px solid;
  margin-bottom: 10px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  border-radius: 10px;
  transition: width 0.5s ease-in-out;
}

.progress-text {
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 12px;
  color: white;
  z-index: 1;
}

.continue-button {
  padding: 10px 20px;
  border: none;
  border-radius: 10px;
  color: white;
  cursor: pointer;
  margin-top: 10px;
}

.continue-button:hover {
  filter: brightness(70%);
}

.card-link {
  text-decoration: none;
}
</style>
