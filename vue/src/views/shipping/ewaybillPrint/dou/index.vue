<template>
  <div class="app-container">
    <el-row>
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="订单号" prop="orderId">
          <el-input
            v-model="queryParams.orderId"
            placeholder="请输入订单号"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="店铺" prop="shopId">
          <el-select v-model="queryParams.shopId" placeholder="请选择店铺" clearable @change="handleQuery">
            <el-option
              v-for="item in shopList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
        <el-form-item>
          <el-select v-model="printParams.printer" placeholder="请选择打印机" clearable>
            <el-option
              v-for="item in printerList"
              :key="item.name"
              :label="item.name"
              :value="item.name">
            </el-option>
          </el-select>

        </el-form-item>
      </el-form>

    </el-row>


    <el-row :gutter="10" class="mb8">

      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-time"
          size="mini"
          :disabled="multiple"
          @click="handleGetEwaybillCode"
        >电子面单取号</el-button>
      </el-col>

      <el-col :span="1.5">

        <el-button
          type="success"
          plain
          :disabled="multiple"
          icon="el-icon-printer"
          size="mini"
          @click="handlePrintEwaybill"
        >电子面单打印</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-d-arrow-right"
          size="mini"
          :disabled="multiple"
          @click="handleShipSend"
        >电子面单发货</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
       <el-table-column type="selection" width="55" align="center" />
<!--      <el-table-column label="ID" align="center" prop="id" />-->
      <el-table-column label="订单号" align="center" prop="orderId" >
        <template slot-scope="scope">
          <p>{{scope.row.orderId}}</p>
          <el-tag  effect="plain">{{shopList.find(x=>x.id === scope.row.shopId).name}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="商品" width="450">
        <template slot-scope="scope">
          <el-table :data="scope.row.items" :show-header="false">
            <el-table-column label="商品" align="center" prop="productName" />
            <el-table-column label="SKU编码" align="center" prop="code" />
            <el-table-column label="数量" align="center" prop="itemNum" width="60">
              <template slot-scope="scope">
                <el-tag size="small">x {{scope.row.itemNum}}</el-tag>
              </template>
            </el-table-column>
          </el-table>
<!--          <el-row v-for="item in scope.row.items" :key="item.id" :gutter="20">-->

<!--            <div style="float: left;display: flex;align-items: center;" >-->

<!--              <div style="margin-left:10px">-->
<!--                <p>{{item.title}}</p>-->
<!--                <p>{{item.skuPropertiesName}}&nbsp;-->
<!--                  <el-tag size="small">x {{item.num}}</el-tag>-->
<!--                </p>-->

<!--              </div>-->
<!--            </div>-->
<!--          </el-row>-->
        </template>
      </el-table-column>
      <el-table-column label="下单时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="buyerMemo" >
        <template slot-scope="scope">
          <span v-if="scope.row.buyerMemo">买家备注:{{ scope.row.buyerWords }}</span>
          <span v-if="scope.row.remark">卖家备注:{{ scope.row.sellerWords }}</span>
        </template>
      </el-table-column>

      <el-table-column label="收件信息" align="left" prop="receiverState" >
        <template slot-scope="scope">
          <p>
            {{scope.row.maskPostReceiver}}&nbsp;{{scope.row.maskPostTel}}
          </p>
          <p>
            {{scope.row.provinceName}} &nbsp;{{scope.row.cityName}}&nbsp;{{scope.row.townName}} {{scope.row.streetName}}&nbsp;
          </p>
          <p>
            {{scope.row.maskPostAddress}}
          </p>
        </template>
      </el-table-column>
      <el-table-column label="面单号" align="center" prop="erpSendCode" />
      <el-table-column label="状态" align="center" prop="erpSendStatus" >
        <template slot-scope="scope">
          <el-tag size="small" v-if="scope.row.erpSendStatus==0">未取号</el-tag>
          <el-tag size="small" v-if="scope.row.erpSendStatus==1">已取号</el-tag>
          <el-tag size="small" v-if="scope.row.erpSendStatus==2">已打印</el-tag>
          <el-tag size="small" v-if="scope.row.erpSendStatus==3">已发货</el-tag>
          <el-tag size="small" v-if="scope.row.erpSendStatus==10">手动发货</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    <!-- 取号 -->
    <el-dialog title="取号" :visible.sync="getCodeOpen" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="电子面单账户" prop="accountId">
            <el-select v-model="form.accountId" placeholder="请选择电子面单账户" clearable>
              <el-option
                v-for="item in deliverList"
                :key="item.id"
                :label="item.company"
                :value="item.id">
                <span style="float: left">{{ item.company }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px" >{{item.netsiteName}}:{{item.amount}}</span>
              </el-option>
            </el-select>
          <el-button type="success" plain @click="updateWaybillAccount" >更新电子面单账户信息</el-button>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="getCodeOpenForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import '@riophae/vue-treeselect/dist/vue-treeselect.css'

import {listShop} from "@/api/shop/shop";
import {listOrder} from "@/api/dou/order";
import {
  getWaybillAccountList,
  pullWaybillAccount,
  getWaybillCode,
  getWaybillPrintData,
  pushWaybillPrintSuccess
} from "@/api/dou/ewaybill";

export default {
  name: "printDou",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      shopList: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 弹出层标题
      title: "",
      // 取号弹出
      getCodeOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderStatus: 2,
        erpSendStatus:-1,
        shopId: null
      },
      // 打印参数
      printParams: {
        deliver: null,
        printer: null
      },
      // 表单参数
      form: {},
      orderList: [],
      printerList: [],
      deliverList: [],
      // 表单校验
      rules: {
        accountId: [{ required: true, message: '请选择电子面单账户' }],
      }
    };
  },
  created() {
    this.openWs()
    listShop({type: 400}).then(response => {
      this.shopList = response.rows;
      if (this.shopList && this.shopList.length > 0) {
        this.queryParams.shopId = this.shopList[0].id
      }
      this.getList();
    });
  },
  methods: {
    /** 查询商品管理列表 */
    getList() {
      this.loading = true;
      listOrder(this.queryParams).then(response => {
        this.orderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.getCodeOpen = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        erpSkuId: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.orderId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    openWs() {
      this.$modal.msgError("开源版本未实现电子面单相关功能！请自行对接发货");
    },
    // 取号弹窗
    handleGetEwaybillCode() {
      const ids = this.ids;
      if (ids) {
        getWaybillAccountList({shopId: this.queryParams.shopId}).then(response => {
          this.deliverList = response.data;
          this.getCodeOpen = true
        });
      } else {
        this.$modal.msgError("请选择订单")
      }
    },
    // 更新电子面单信息
    updateWaybillAccount() {
      pullWaybillAccount({shopId: this.queryParams.shopId}).then(response => {
        this.deliverList = response.data;
      });
    },
    /** 取号提交按钮 */
    getCodeOpenForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const ids = this.ids;
          console.log('=========3333========', ids)
          if (ids) {
            console.log('===请求参数=====', {shopId: this.queryParams.shopId, ids: ids, accountId: this.form.accountId})
            getWaybillCode({
              shopId: this.queryParams.shopId,
              ids: ids,
              accountId: this.form.accountId
            }).then(response => {
              this.$modal.msgSuccess("取号成功")
              this.getList()
              this.getCodeOpen = false
            });
          } else {
            this.$modal.msgError("请选择订单")
          }
        }
      });
    },
    handlePrintEwaybill() {
      // if (!this.ws) {
      //   this.$modal.msgError('打印组件连接失败！请安装并启动微信视频号小单打印组件！');
      //   this.openWs()
      // }
      // if(!this.printParams.deliver){
      //   this.$modal.msgError('请选择快递公司！');
      //   return
      // }
      if (!this.printParams.printer) {
        this.$modal.msgError('请选择打印机！');
        return
      }
      const ids = this.ids;
      getWaybillPrintData({shopId: this.queryParams.shopId, ids: ids}).then(response => {
        console.log("======打印======", response.data)
        if (response.data) {
          this.$modal.msgError("开源版本未实现电子面单相关功能！请自行对接发货");
        }
      });


    },
    handleShipSend(){
      this.$modal.msgError("开源版本未实现平台发货！请自行对接发货");
    },

  }
};
</script>
