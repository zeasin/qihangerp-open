import { hasPerm } from "./permission";

// 全局注册 directive
export function setupDirective(app) {
  // 使 v-hasPerm 在所有组件中都可用
  app.directive("hasPerm", hasPerm);
} 