import { config } from "@vue/test-utils"

// Глобальные настройки для тестов
config.global.mocks = {
  $route: {
    params: {},
  },
  $router: {
    push: jest.fn(),
  },
}
