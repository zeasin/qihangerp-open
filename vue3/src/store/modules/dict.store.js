import { store } from "@/store";
import DictAPI from "@/api/system/dict.api";

export const useDictStore = defineStore("dict", () => {
  // 字典数据缓存
  const dictCache = useStorage("dict_cache", {});

  // 请求队列（防止重复请求）
  const requestQueue = {};

  /**
   * 缓存字典数据
   * @param dictCode 字典编码
   * @param data 字典项列表
   */
  const cacheDictItems = (dictCode, data) => {
    dictCache.value[dictCode] = data;
  };

  /**
   * 加载字典数据（如果缓存中没有则请求）
   * @param dictCode 字典编码
   */
  const loadDictItems = async (dictCode) => {
    if (dictCache.value[dictCode]) return;
    // 防止重复请求
    if (!requestQueue[dictCode]) {
      requestQueue[dictCode] = DictAPI.getDictItems(dictCode).then((data) => {
        cacheDictItems(dictCode, data);
        Reflect.deleteProperty(requestQueue, dictCode);
      });
    }
    await requestQueue[dictCode];
  };

  /**
   * 获取字典项列表
   * @param dictCode 字典编码
   * @returns 字典项列表
   */
  const getDictItems = (dictCode) => {
    return dictCache.value[dictCode] || [];
  };

  /**
   * 清空字典缓存
   */
  const clearDictCache = () => {
    dictCache.value = {};
  };

  return {
    loadDictItems,
    getDictItems,
    clearDictCache,
  };
});

export function useDictStoreHook() {
  return useDictStore(store);
} 