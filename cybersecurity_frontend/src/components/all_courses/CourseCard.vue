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
        <div class="task-count" :style="{ color: accentColor }">
          <span>{{ taskCount }} заданий</span>
          <hr class="line" :style="{ borderColor: accentColor }" />
        </div>
      </div>
    </div>
  </router-link>
</template>

<script>
export default {
  name: "CourseCard",
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
    taskCount: {
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
  padding: 20px;
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
.task-count {
  font-weight: 600;
  font-size: 14px;
  line-height: 17px;
  text-align: center;
  margin-bottom: 10px;
}
.line {
  width: 85px;
  height: 0px;
  border: 1px solid;
  margin: 5px auto 0;
}

.card-link {
  text-decoration: none;
}
</style>
