<template>
  <div class="article-card">
    <div class="article-card__content">
      <div class="article-card__icon">
        <img src="../../assets/icons/theory.svg" alt="Icon" />
      </div>
      <h2 class="article-card__title">Заголовок статьи</h2>
      <p class="article-card__excerpt">
        Пример текста статьи. Lorem ipsum dolor sit amet, consectetur adipiscing
        elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
        Ut enim ad minim veniam, quis...
      </p>
      <div class="article-card__read-more">
        <span>Читать</span>
        <svg
          width="24"
          height="24"
          viewBox="0 0 48 48"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <g clip-path="url(#clip0_323_1008)">
            <path
              d="M18 36L30 24L18 12"
              stroke="#3ae8c5"
              stroke-width="4"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </g>
          <defs>
            <clipPath id="clip0_323_1008">
              <rect width="48" height="48" fill="white" />
            </clipPath>
          </defs>
        </svg>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "MaterialCardPreview",
};
</script>

<style scoped>
.article-card {
  width: 300px;
  height: 300px;
  background: #f1ffff;
  box-shadow: 10px 10px 10px rgba(0, 0, 0, 0.1);
  border-radius: 20px;
  display: block;
  text-decoration: none;
  color: inherit;
  position: relative;
}

.article-card__content {
  padding: 10px 20px 10px 20px;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.article-card__icon {
  width: 40px;
  height: 40px;
}

.article-card__icon img {
  width: 100%;
  height: 100%;
}

.article-card__title {
  font-family: "Montserrat", sans-serif;
  font-weight: 700;
  font-size: 24px;
  line-height: 29px;
  color: #3ae8c5;
  margin: 0px;
}

.article-card__excerpt {
  font-family: "Montserrat", sans-serif;
  font-weight: 300;
  font-size: 16px;
  line-height: 1.4;
}

.article-card__read-more {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  font-family: "Montserrat", sans-serif;
  font-weight: 300;
  font-size: 14px;
  line-height: 17px;
  color: #3ae8c5;
}
.article-card__read-more svg {
  margin-left: 10px;
  color: #3ae8c5;
}
</style>
