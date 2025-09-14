import defaultSettings from "@/settings";
import { SidebarColor, ThemeMode } from "@/enums/settings/theme.enum";
import { LayoutMode } from "@/enums/settings/layout.enum";
import { generateThemeColors, applyTheme, toggleDarkMode, toggleSidebarColor } from "@/utils/theme";

export const useSettingsStore = defineStore("setting", () => {
  // 基本设置
  const settingsVisible = ref(false);
  // 标签视图
  const tagsView = useStorage("tagsView", defaultSettings.tagsView);
  // 侧边栏 Logo
  const sidebarLogo = useStorage("sidebarLogo", defaultSettings.sidebarLogo);
  // 侧边栏配色方案 (经典蓝/极简白)
  const sidebarColorScheme = useStorage(
    "sidebarColorScheme",
    defaultSettings.sidebarColorScheme
  );
  // 布局
  const layout = useStorage("layout", defaultSettings.layout);
  // 水印
  const watermarkEnabled = useStorage(
    "watermarkEnabled",
    defaultSettings.watermarkEnabled
  );

  // 主题
  const themeColor = useStorage("themeColor", defaultSettings.themeColor);
  const theme = useStorage("theme", defaultSettings.theme);

  //  监听主题变化
  watch(
    [theme, themeColor],
    ([newTheme, newThemeColor]) => {
      toggleDarkMode(newTheme === ThemeMode.DARK);
      const colors = generateThemeColors(newThemeColor, newTheme);
      applyTheme(colors);
    },
    { immediate: true }
  );

  //  监听浅色侧边栏配色方案变化
  watch(
    [sidebarColorScheme],
    ([newSidebarColorScheme]) => {
      toggleSidebarColor(newSidebarColorScheme === SidebarColor.CLASSIC_BLUE);
    },
    { immediate: true }
  );

  // 设置映射
  const settingsMap = {
    tagsView,
    sidebarLogo,
    sidebarColorScheme,
    layout,
    watermarkEnabled,
  };

  function changeSetting({ key, value }) {
    const setting = settingsMap[key];
    if (setting) setting.value = value;
  }

  function changeTheme(val) {
    theme.value = val;
  }

  function changeSidebarColor(val) {
    sidebarColorScheme.value = val;
  }

  function changeThemeColor(color) {
    themeColor.value = color;
  }

  function changeLayout(val) {
    layout.value = val;
  }

  return {
    settingsVisible,
    tagsView,
    sidebarLogo,
    sidebarColorScheme,
    layout,
    themeColor,
    theme,
    watermarkEnabled,
    changeSetting,
    changeTheme,
    changeThemeColor,
    changeLayout,
    changeSidebarColor,
  };
}); 