<template>
  <div class="app-container">
    <div class="search-bar">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item prop="keywords" label="关键字">
          <el-input
            v-model="queryParams.keywords"
            placeholder="表名"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <template #icon>
              <Search />
            </template>
            搜索
          </el-button>
          <el-button @click="handleResetQuery">
            <template #icon>
              <Refresh />
            </template>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card shadow="never" class="table-container">
      <el-table
        ref="dataTableRef"
        v-loading="loading"
        :data="pageData"
        highlight-current-row
        border
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="表名" prop="tableName" min-width="100" />
        <el-table-column label="描述" prop="tableComment" width="150" />

        <el-table-column label="存储引擎" align="center" prop="engine" />

        <el-table-column label="排序规则" align="center" prop="tableCollation" />
        <el-table-column label="创建时间" align="center" prop="createTime" />

        <el-table-column fixed="right" label="操作" width="200">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleOpenDialog(scope.row.tableName)"
            >
              <template #icon>
                <MagicStick />
              </template>
              生成代码
            </el-button>

            <el-button
              v-if="scope.row.isConfigured === 1"
              type="danger"
              size="small"
              link
              @click="handleResetConfig(scope.row.tableName)"
            >
              <template #icon>
                <RefreshLeft />
              </template>
              重置配置
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-if="total > 0"
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="handleQuery"
      />
    </el-card>

    <el-drawer
      v-model="dialog.visible"
      :title="dialog.title"
      size="80%"
      @close="dialog.visible = false"
    >
      <el-steps :active="active" align-center finish-status="success" simple>
        <el-step title="基础配置" />
        <el-step title="字段配置" />
        <el-step title="预览生成" />
      </el-steps>

      <div class="mt-5">
        <el-form
          v-show="active == 0"
          :model="genConfigFormData"
          :label-width="100"
          :rules="genConfigFormRules"
        >
          <el-row>
            <el-col :span="12">
              <el-form-item label="表名" prop="tableName">
                <el-input v-model="genConfigFormData.tableName" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="业务名" prop="businessName">
                <el-input v-model="genConfigFormData.businessName" placeholder="用户" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="主包名" prop="packageName">
                <el-input v-model="genConfigFormData.packageName" placeholder="com.youlai.boot" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="模块名" prop="moduleName">
                <el-input v-model="genConfigFormData.moduleName" placeholder="system" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="实体名" prop="entityName">
                <el-input v-model="genConfigFormData.entityName" placeholder="User" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="作者">
                <el-input v-model="genConfigFormData.author" placeholder="youlai" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item>
                <template #label>
                  <div class="flex-y-between">
                    <span>上级菜单</span>
                    <el-tooltip effect="dark">
                      <template #content>
                        选择上级菜单，生成代码后会自动创建对应菜单。
                        <br />
                        注意1：生成菜单后需分配权限给角色，否则菜单将无法显示。
                        <br />
                        注意2：演示环境默认不生成菜单，如需生成，请在本地部署数据库。
                      </template>
                      <el-icon class="cursor-pointer">
                        <QuestionFilled />
                      </el-icon>
                    </el-tooltip>
                  </div>
                </template>

                <el-tree-select
                  v-model="genConfigFormData.parentMenuId"
                  placeholder="选择上级菜单"
                  :data="menuOptions"
                  check-strictly
                  :render-after-expand="false"
                  filterable
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <div v-show="active == 1" class="elTableCustom">
          <el-table
            v-loading="loading"
            row-key="id"
            :element-loading-text="loadingText"
            highlight--currentrow
            :data="genConfigFormData.fieldConfigs"
          >
            <el-table-column width="55" align="center">
              <el-icon class="cursor-move sortable-handle">
                <Rank />
              </el-icon>
            </el-table-column>

            <el-table-column label="列名" width="110">
              <template #default="scope">
                {{ scope.row.columnName }}
              </template>
            </el-table-column>

            <el-table-column label="列类型" width="80">
              <template #default="scope">
                {{ scope.row.columnType }}
              </template>
            </el-table-column>
            <el-table-column label="字段名" width="120">
              <template #default="scope">
                <el-input v-model="scope.row.fieldName" />
              </template>
            </el-table-column>
            <el-table-column label="字段类型" width="80">
              <template #default="scope">
                {{ scope.row.fieldType }}
              </template>
            </el-table-column>

            <el-table-column label="字段注释" min-width="100">
              <template #default="scope">
                <el-input v-model="scope.row.fieldComment" />
              </template>
            </el-table-column>

            <el-table-column label="最大长度" width="80">
              <template #default="scope">
                <el-input v-model="scope.row.maxLength" />
              </template>
            </el-table-column>

            <el-table-column width="70">
              <template #header>
                <div class="flex-y-center">
                  <span>查询</span>
                  <el-checkbox
                    v-model="isCheckAllQuery"
                    class="ml-1"
                    @change="toggleCheckAll('isShowInQuery', isCheckAllQuery)"
                  />
                </div>
              </template>
              <template #default="scope">
                <el-checkbox v-model="scope.row.isShowInQuery" :true-value="1" :false-value="0" />
              </template>
            </el-table-column>

            <el-table-column width="70">
              <template #header>
                <div class="flex-y-center">
                  <span>列表</span>
                  <el-checkbox
                    v-model="isCheckAllList"
                    class="ml-1"
                    @change="toggleCheckAll('isShowInList', isCheckAllList)"
                  />
                </div>
              </template>

              <template #default="scope">
                <el-checkbox v-model="scope.row.isShowInList" :true-value="1" :false-value="0" />
              </template>
            </el-table-column>

            <el-table-column width="70">
              <template #header>
                <div class="flex-y-center">
                  <span>表单</span>
                  <el-checkbox
                    v-model="isCheckAllForm"
                    class="ml-1"
                    @change="toggleCheckAll('isShowInForm', isCheckAllForm)"
                  />
                </div>
              </template>

              <template #default="scope">
                <el-checkbox v-model="scope.row.isShowInForm" :true-value="1" :false-value="0" />
              </template>
            </el-table-column>

            <el-table-column label="必填" width="70">
              <template #default="scope">
                <el-checkbox
                  v-if="scope.row.isShowInForm == 1"
                  v-model="scope.row.isRequired"
                  :true-value="1"
                  :false-value="0"
                />
                <span v-else>-</span>
              </template>
            </el-table-column>

            <el-table-column label="查询方式" min-width="120">
              <template #default="scope">
                <el-select
                  v-if="scope.row.isShowInQuery === 1"
                  v-model="scope.row.queryType"
                  placeholder="请选择"
                >
                  <el-option
                    v-for="(item, key) in queryTypeOptions"
                    :key="key"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
                <span v-else>-</span>
              </template>
            </el-table-column>

            <el-table-column label="表单类型" min-width="120">
              <template #default="scope">
                <el-select
                  v-if="scope.row.isShowInQuery === 1 || scope.row.isShowInForm === 1"
                  v-model="scope.row.formType"
                  placeholder="请选择"
                >
                  <el-option
                    v-for="(item, key) in formTypeOptions"
                    :key="key"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
                <span v-else>-</span>
              </template>
            </el-table-column>

            <el-table-column label="字典类型" min-width="100">
              <template #default="scope">
                <el-select
                  v-if="scope.row.formType === FormTypeEnum.SELECT.value"
                  v-model="scope.row.dictType"
                  placeholder="请选择"
                  clearable
                >
                  <el-option
                    v-for="item in dictOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <el-row v-show="active == 2">
          <el-col :span="6">
            <el-scrollbar max-height="72vh">
              <el-tree
                :data="treeData"
                default-expand-all
                highlight-current
                @node-click="handleFileTreeNodeClick"
              >
                <template #default="{ data }">
                  <div :class="`i-svg:${getFileTreeNodeIcon(data.label)}`" />
                  <span class="ml-1">{{ data.label }}</span>
                </template>
              </el-tree>
            </el-scrollbar>
          </el-col>
          <el-col :span="18">
            <el-scrollbar max-height="72vh">
              <div class="absolute z-36 right-5 top-2">
                <el-link type="primary" @click="handleCopyCode">
                  <el-icon>
                    <CopyDocument />
                  </el-icon>
                  一键复制
                </el-link>
              </div>

              <Codemirror
                ref="cmRef"
                v-model:value="code"
                :options="cmOptions"
                border
                :readonly="true"
                height="100%"
                width="100%"
              />
            </el-scrollbar>
          </el-col>
        </el-row>
      </div>

      <template #footer>
        <el-button v-if="active !== 0" type="success" @click="handlePrevClick">
          <el-icon>
            <Back />
          </el-icon>
          {{ prevBtnText }}
        </el-button>
        <el-button type="primary" @click="handleNextClick">
          {{ nextBtnText }}
          <el-icon v-if="active !== 2">
            <Right />
          </el-icon>
          <el-icon v-else>
            <Download />
          </el-icon>
        </el-button>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
defineOptions({
  name: "Codegen",
});

import Sortable from "sortablejs";
import "codemirror/mode/javascript/javascript.js";
import Codemirror from "codemirror-editor-vue3";
import { useClipboard } from "@vueuse/core";

import { FormTypeEnum } from "@/enums/codegen/form.enum";
import { QueryTypeEnum } from "@/enums/codegen/query.enum";

import GeneratorAPI from "@/api/codegen.api";
import DictAPI from "@/api/system/dict.api";
import MenuAPI from "@/api/system/menu.api";

const treeData = ref([]);

const queryFormRef = ref();
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
});

const loading = ref(false);
const loadingText = ref("loading...");

const pageData = ref([]);
const total = ref(0);

const dialog = reactive({
  visible: false,
  title: "",
});

const active = ref(0);
const prevBtnText = ref("上一步");
const nextBtnText = ref("下一步");

const genConfigFormData = ref({
  fieldConfigs: [],
});

const genConfigFormRules = {
  tableName: [{ required: true, message: "请输入表名", trigger: "blur" }],
  businessName: [{ required: true, message: "请输入业务名", trigger: "blur" }],
  packageName: [{ required: true, message: "请输入包名", trigger: "blur" }],
  moduleName: [{ required: true, message: "请输入模块名", trigger: "blur" }],
  entityName: [{ required: true, message: "请输入实体名", trigger: "blur" }],
};

const menuOptions = ref([]);
const dictOptions = ref([]);

const currentTableName = ref("");

const isCheckAllQuery = ref(false);
const isCheckAllList = ref(false);
const isCheckAllForm = ref(false);

const sortFlag = ref(null);

const queryTypeOptions = [
  { label: "=", value: QueryTypeEnum.EQ.value },
  { label: "≠", value: QueryTypeEnum.NE.value },
  { label: ">", value: QueryTypeEnum.GT.value },
  { label: "≥", value: QueryTypeEnum.GE.value },
  { label: "<", value: QueryTypeEnum.LT.value },
  { label: "≤", value: QueryTypeEnum.LE.value },
  { label: "LIKE", value: QueryTypeEnum.LIKE.value },
  { label: "BETWEEN", value: QueryTypeEnum.BETWEEN.value },
];

const formTypeOptions = [
  { label: "文本框", value: FormTypeEnum.INPUT.value },
  { label: "文本域", value: FormTypeEnum.TEXT_AREA.value },
  { label: "数字框", value: FormTypeEnum.INPUT_NUMBER.value },
  { label: "下拉框", value: FormTypeEnum.SELECT.value },
  { label: "单选框", value: FormTypeEnum.RADIO.value },
  { label: "复选框", value: FormTypeEnum.CHECK_BOX.value },
  { label: "日期选择器", value: FormTypeEnum.DATE.value },
  { label: "日期时间选择器", value: FormTypeEnum.DATE_TIME.value },
  { label: "图片上传", value: FormTypeEnum.IMAGE.value },
  { label: "文件上传", value: FormTypeEnum.FILE.value },
  { label: "富文本编辑器", value: FormTypeEnum.EDITOR.value },
];

const cmRef = ref();
const code = ref("");
const copied = ref(false);

const cmOptions = {
  mode: "text/javascript",
  theme: "base16-dark",
  lineNumbers: true,
  line: true,
  styleActiveLine: true,
  matchBrackets: true,
  indentUnit: 2,
  tabSize: 2,
  lineWrapping: true,
  readOnly: true,
};

watch(active, () => {
  if (active.value === 0) {
    prevBtnText.value = "取消";
    nextBtnText.value = "下一步";
  } else if (active.value === 1) {
    prevBtnText.value = "上一步";
    nextBtnText.value = "下一步";
  } else if (active.value === 2) {
    prevBtnText.value = "上一步";
    nextBtnText.value = "下载代码";
  }
});

watch(copied, () => {
  if (copied.value) {
    ElMessage.success("复制成功");
  }
});

watch(
  () => genConfigFormData.value.fieldConfigs,
  (newVal) => {
    newVal.forEach((fieldConfig) => {
      if (
        fieldConfig.fieldType &&
        fieldConfig.fieldType.includes("Date") &&
        fieldConfig.isShowInQuery === 1
      ) {
        fieldConfig.queryType = QueryTypeEnum.BETWEEN.value;
      }
    });
  },
  { deep: true, immediate: true }
);

const initSort = () => {
  if (sortFlag.value) {
    return;
  }
  const table = document.querySelector(".elTableCustom .el-table__body-wrapper tbody");
  sortFlag.value = Sortable.create(table, {
    group: "shared",
    animation: 150,
    ghostClass: "sortable-ghost",
    handle: ".sortable-handle",
    easing: "cubic-bezier(1, 0, 0, 1)",
    onEnd: (item) => {
      setNodeSort(item.oldIndex, item.newIndex);
    },
  });
};

const setNodeSort = (oldIndex, newIndex) => {
  let arr = Object.assign([], genConfigFormData.value.fieldConfigs);
  const currentRow = arr.splice(oldIndex, 1)[0];
  arr.splice(newIndex, 0, currentRow);
  arr.forEach((item, index) => {
    item.fieldSort = index + 1;
  });
  genConfigFormData.value.fieldConfigs = [];
  nextTick(async () => {
    genConfigFormData.value.fieldConfigs = arr;
  });
};

function handlePrevClick() {
  if (active.value === 2) {
    genConfigFormData.value = {
      fieldConfigs: [],
    };
    nextTick(() => {
      loading.value = true;
      GeneratorAPI.getGenConfig(currentTableName.value)
        .then((data) => {
          genConfigFormData.value = data;
        })
        .finally(() => {
          loading.value = false;
        });
    });
    initSort();
  }
  if (active.value-- <= 0) active.value = 0;
}

function handleNextClick() {
  if (active.value === 0) {
    const { tableName, packageName, businessName, moduleName, entityName } =
      genConfigFormData.value;
    if (!tableName || !packageName || !businessName || !moduleName || !entityName) {
      ElMessage.error("表名、业务名、包名、模块名、实体名不能为空");
      return;
    }
    initSort();
  }
  if (active.value === 1) {
    const tableName = genConfigFormData.value.tableName;
    if (!tableName) {
      ElMessage.error("表名不能为空");
      return;
    }
    loading.value = true;
    loadingText.value = "代码生成中，请稍后...";
    GeneratorAPI.saveGenConfig(tableName, genConfigFormData.value)
      .then(() => {
        handlePreview(tableName);
      })
      .then(() => {
        if (active.value++ >= 2) active.value = 2;
      })
      .finally(() => {
        loading.value = false;
        loadingText.value = "loading...";
      });
  } else {
    if (active.value++ >= 2) {
      active.value = 2;
    }
    if (active.value === 2) {
      const tableName = genConfigFormData.value.tableName;
      if (!tableName) {
        ElMessage.error("表名不能为空");
        return;
      }
      GeneratorAPI.download(tableName);
    }
  }
}

function handleQuery() {
  loading.value = true;
  GeneratorAPI.getTablePage(queryParams)
    .then((data) => {
      pageData.value = data.list;
      total.value = data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}

function handleResetQuery() {
  queryFormRef.value.resetFields();
  queryParams.pageNum = 1;
  handleQuery();
}

async function handleOpenDialog(tableName) {
  dialog.visible = true;
  active.value = 0;

  menuOptions.value = await MenuAPI.getOptions(true);

  currentTableName.value = tableName;
  DictAPI.getList().then((data) => {
    dictOptions.value = data;
    loading.value = true;
    GeneratorAPI.getGenConfig(tableName)
      .then((data) => {
        dialog.title = `${tableName} 代码生成`;
        genConfigFormData.value = data;

        checkAllSelected("isShowInQuery", isCheckAllQuery);
        checkAllSelected("isShowInList", isCheckAllList);
        checkAllSelected("isShowInForm", isCheckAllForm);

        if (genConfigFormData.value.id) {
          active.value = 2;
          handlePreview(tableName);
        } else {
          active.value = 0;
        }
      })
      .finally(() => {
        loading.value = false;
      });
  });
}

function handleResetConfig(tableName) {
  ElMessageBox.confirm("确定要重置配置吗？", "提示", {
    type: "warning",
  }).then(() => {
    GeneratorAPI.resetGenConfig(tableName).then(() => {
      ElMessage.success("重置成功");
      handleQuery();
    });
  });
}

const toggleCheckAll = (key, value) => {
  const fieldConfigs = genConfigFormData.value?.fieldConfigs;

  if (fieldConfigs) {
    fieldConfigs.forEach((row) => {
      row[key] = value ? 1 : 0;
    });
  }
};

const checkAllSelected = (key, isCheckAllRef) => {
  const fieldConfigs = genConfigFormData.value?.fieldConfigs || [];
  isCheckAllRef.value = fieldConfigs.every((row) => row[key] === 1);
};

function handlePreview(tableName) {
  treeData.value = [];
  GeneratorAPI.getPreviewData(tableName)
    .then((data) => {
      dialog.title = `代码生成 ${tableName}`;
      const tree = buildTree(data);
      treeData.value = [tree];

      const firstLeafNode = findFirstLeafNode(tree);
      if (firstLeafNode) {
        code.value = firstLeafNode.content || "";
      }
    })
    .catch(() => {
      active.value = 0;
    });
}

function buildTree(data) {
  const root = { label: "前后端代码", children: [] };

  data.forEach((item) => {
    const separator = item.path.includes("/") ? "/" : "\\";
    const parts = item.path.split(separator);

    const specialPaths = [
      "src" + separator + "main",
      "java",
      genConfigFormData.value.backendAppName,
      genConfigFormData.value.frontendAppName,
      (genConfigFormData.value.packageName + "." + genConfigFormData.value.moduleName).replace(
        /\./g,
        separator
      ),
    ];

    const mergedParts = [];
    let buffer = [];

    parts.forEach((part) => {
      buffer.push(part);
      const currentPath = buffer.join(separator);
      if (specialPaths.includes(currentPath)) {
        mergedParts.push(currentPath);
        buffer = [];
      }
    });

    mergedParts.forEach((part, index) => {
      mergedParts[index] = part.replace(/\\/g, "/");
    });

    if (buffer.length > 0) {
      mergedParts.push(...buffer);
    }

    let currentNode = root;

    mergedParts.forEach((part) => {
      let node = currentNode.children?.find((child) => child.label === part);
      if (!node) {
        node = { label: part, children: [] };
        currentNode.children?.push(node);
      }
      currentNode = node;
    });

    currentNode.children?.push({
      label: item.fileName,
      content: item?.content,
    });
  });

  return root;
}

function findFirstLeafNode(node) {
  if (!node.children || node.children.length === 0) {
    return node;
  }
  for (const child of node.children) {
    const leafNode = findFirstLeafNode(child);
    if (leafNode) {
      return leafNode;
    }
  }
  return null;
}

function handleFileTreeNodeClick(data) {
  if (!data.children || data.children.length === 0) {
    code.value = data.content || "";
  }
}

function getFileTreeNodeIcon(label) {
  if (label.endsWith(".java")) {
    return "java";
  }
  if (label.endsWith(".html")) {
    return "html";
  }
  if (label.endsWith(".vue")) {
    return "vue";
  }
  if (label.endsWith(".ts")) {
    return "typescript";
  }
  if (label.endsWith(".xml")) {
    return "xml";
  }
  return "file";
}

const { copy } = useClipboard();

const handleCopyCode = () => {
  if (code.value) {
    copy(code.value);
    copied.value = true;
    setTimeout(() => {
      copied.value = false;
    }, 2000);
  }
};

onMounted(() => {
  handleQuery();
  cmRef.value?.destroy();
});
</script>
