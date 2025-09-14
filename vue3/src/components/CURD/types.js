/**
 * @typedef {Object} IObject
 * @property {any} [key] - 任意类型的值
 */

/**
 * @typedef {Object} IOperatData
 * @property {string} name - 操作名称
 * @property {IObject} row - 行数据
 * @property {IObject} column - 列数据
 * @property {number} $index - 索引
 */

/**
 * @typedef {('input'|'select'|'input-number'|'date-picker'|'time-picker'|'time-select'|'tree-select'|'input-tag'|'custom-tag'|'cascader')} ComponentType
 */

/**
 * @typedef {Object} ISearchConfig
 * @property {string} pageName - 页面名称(参与组成权限标识,如sys:user:xxx)
 * @property {boolean} [colon] - 标签冒号
 * @property {Array<Object>} formItems - 表单项
 * @property {boolean} [isExpandable] - 是否开启展开和收缩
 * @property {number} [showNumber] - 默认展示的表单项数量
 */

/**
 * @typedef {Object} IContentConfig
 * @property {string} pageName - 页面名称(参与组成权限标识,如sys:user:xxx)
 * @property {Object} [table] - table组件属性
 * @property {boolean|Object} [pagination] - pagination组件属性
 * @property {Function} indexAction - 列表的网络请求函数(需返回promise)
 * @property {Object} [request] - 默认的分页相关的请求参数
 * @property {Function} [parseData] - 数据格式解析的回调函数
 * @property {Function} [modifyAction] - 修改属性的网络请求函数(需返回promise)
 * @property {Function} [deleteAction] - 删除的网络请求函数(需返回promise)
 * @property {Function} [exportAction] - 后端导出的网络请求函数(需返回promise)
 * @property {Function} [exportsAction] - 前端全量导出的网络请求函数(需返回promise)
 * @property {string|Function} [importTemplate] - 导入模板
 * @property {Function} [importAction] - 后端导入的网络请求函数(需返回promise)
 * @property {Function} [importsAction] - 前端导入的网络请求函数(需返回promise)
 * @property {string} [pk] - 主键名(默认为id)
 * @property {Array<string|Object>} [toolbar] - 表格工具栏
 * @property {Array<string|Object>} [defaultToolbar] - 表格工具栏右侧图标
 * @property {Array<Object>} cols - table组件列属性
 */

/**
 * @typedef {Object} IModalConfig
 * @property {string} [pageName] - 页面名称
 * @property {string} [pk] - 主键名(主要用于编辑数据,默认为id)
 * @property {('dialog'|'drawer')} [component] - 组件类型
 * @property {Object} [dialog] - dialog组件属性
 * @property {Object} [drawer] - drawer组件属性
 * @property {Object} [form] - form组件属性
 * @property {Array<Object>} formItems - 表单项
 * @property {Function} [beforeSubmit] - 提交之前处理
 * @property {Function} formAction - 提交的网络请求函数(需返回promise)
 */

/**
 * @typedef {Object} IForm
 */

/**
 * @typedef {Array<Object>} IFormItems
 */

/**
 * @typedef {Object} IPageForm
 * @property {string} [pk] - 主键名(主要用于编辑数据,默认为id)
 * @property {Object} [form] - form组件属性
 * @property {Array<Object>} formItems - 表单项
 */

export {
  IObject,
  IOperatData,
  ComponentType,
  ISearchConfig,
  IContentConfig,
  IModalConfig,
  IForm,
  IFormItems,
  IPageForm
}; 