import { ref } from "vue";

/**
 * 页面操作 Hook
 * @returns {Object} 页面操作相关的方法和引用
 */
function usePage() {
  const searchRef = ref();
  const contentRef = ref();
  const addModalRef = ref();
  const editModalRef = ref();

  /**
   * 搜索
   * @param {Object} queryParams - 查询参数
   */
  function handleQueryClick(queryParams) {
    const filterParams = contentRef.value?.getFilterParams();
    contentRef.value?.fetchPageData({ ...queryParams, ...filterParams }, true);
  }

  /**
   * 重置
   * @param {Object} queryParams - 查询参数
   */
  function handleResetClick(queryParams) {
    const filterParams = contentRef.value?.getFilterParams();
    contentRef.value?.fetchPageData({ ...queryParams, ...filterParams }, true);
  }

  /**
   * 新增
   */
  function handleAddClick() {
    //显示添加表单
    addModalRef.value?.setModalVisible();
  }

  /**
   * 编辑
   * @param {Object} row - 行数据
   */
  function handleEditClick(row) {
    //显示编辑表单 根据数据进行填充
    editModalRef.value?.setModalVisible(row);
  }

  /**
   * 表单提交
   */
  function handleSubmitClick() {
    //根据检索条件刷新列表数据
    const queryParams = searchRef.value?.getQueryParams();
    contentRef.value?.fetchPageData(queryParams, true);
  }

  /**
   * 导出
   */
  function handleExportClick() {
    // 根据检索条件导出数据
    const queryParams = searchRef.value?.getQueryParams();
    contentRef.value?.exportPageData(queryParams);
  }

  /**
   * 搜索显隐
   */
  function handleSearchClick() {
    searchRef.value?.toggleVisible();
  }

  /**
   * 涮选数据
   * @param {Object} filterParams - 过滤参数
   */
  function handleFilterChange(filterParams) {
    const queryParams = searchRef.value?.getQueryParams();
    contentRef.value?.fetchPageData({ ...queryParams, ...filterParams }, true);
  }

  return {
    searchRef,
    contentRef,
    addModalRef,
    editModalRef,
    handleQueryClick,
    handleResetClick,
    handleAddClick,
    handleEditClick,
    handleSubmitClick,
    handleExportClick,
    handleSearchClick,
    handleFilterChange,
  };
}

export default usePage; 