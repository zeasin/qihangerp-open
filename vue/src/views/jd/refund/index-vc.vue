<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
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
      <el-form-item label="退款单号" prop="id">
        <el-input
          v-model="queryParams.id"
          placeholder="请输入退款单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客单编号" prop="customOrderId">
        <el-input
          v-model="queryParams.orderId"
          placeholder="请输入客单编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="退款类型" prop="customerExpect">
        <el-select v-model="queryParams.customerExpect" placeholder="请选择状态" clearable @change="handleQuery">
          <el-option label="仅退款" value="11" ></el-option>
          <el-option label="退货退款" value="10"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          :loading="pullLoading"
          type="danger"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handlePull"
        >API拉取新退款</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-refresh"
          size="mini"
          :disabled="multiple"
          @click="handlePushOms"
        >手动将选中退款推送到售后中心</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="taoRefundList" @selection-change="handleSelectionChange">
       <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="${comment}" align="center" prop="id" /> -->
      <el-table-column label="退款单号" align="center" prop="id" />

      <el-table-column label="店铺" align="center" prop="shopId" >
        <template slot-scope="scope">
         {{shopList.find(x=>x.id === scope.row.shopId).name}}
        </template>
      </el-table-column>
<!--      <el-table-column label="商品" width="350">-->
<!--        <template slot-scope="scope">-->
<!--          <el-row v-for="item in scope.row.itemList" :key="item.id" :gutter="20">-->
<!--            <div style="float: left;display: flex;align-items: center;">-->
<!--              <div style="margin-left:10px">-->
<!--                <p>{{item.wareName}}</p>-->
<!--                <p>退款金额：{{amountFormatter(null,null,item.returnsPrice,0)}}&nbsp;-->
<!--                </p>-->
<!--                <p>-->
<!--                  退货数量： <el-tag size="small">x {{item.returnsNum}}</el-tag>-->
<!--                </p>-->
<!--              </div>-->
<!--            </div>-->
<!--          </el-row>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column label="客单编号" prop="customOrderId" ></el-table-column>
      <el-table-column label="退款类型" align="center" prop="orderState" >
        <template slot-scope="scope">
          <el-tag size="small" v-if="scope.row.orderState === 29 && (scope.row.operatorState===5 || scope.row.operatorState===10 )"> 仅退款</el-tag>
          <el-tag size="small" v-if="scope.row.orderState === 29 && scope.row.operatorState===16"> 退货退款</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="退款商品" prop="commodityName" ></el-table-column>
      <el-table-column label="退款数量" prop="commodityNum" ></el-table-column>
      <el-table-column label="退款金额" prop="roApplyFee" :formatter="amountFormatter"></el-table-column>
      <el-table-column label="退款理由" prop="roReason" ></el-table-column>
      <el-table-column label="审核意见" prop="approvalSuggestion" ></el-table-column>
      <el-table-column label="申请时间" align="center" prop="roApplyDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.roApplyDate) }}</span>
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


  </div>
</template>

<script>
import {listRefund, pullRefund, pushOms} from "@/api/jdvc/refund";
import { listShop } from "@/api/shop/shop";
import {MessageBox} from "element-ui";
import {isRelogin} from "@/utils/request";
export default {
  name: "RefundJdvc",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 淘宝退款订单表格数据
      taoRefundList: [],
      shopList:[],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      pullLoading: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        refundId: null,
        afterSalesType: null,
        tid: null,
        oid: null,
        buyerNick: null,
        totalFee: null,
        payment: null,
        refundFee: null,
        created: null,
        modified: null,
        orderStatus: null,
        status: null,
        goodStatus: null,
        num: null,
        hasGoodReturn: null,
        reason: null,
        desc: null,
        logisticsCompany: null,
        logisticsCode: null,
        sendTime: null,
        auditStatus: null,
        auditTime: null,
        receivedTime: null,
        address: null,
        createOn: null,
        shopId: null,
        erpGoodsId: null,
        erpGoodsSpecId: null,
        specNumber: null,
        refundPhase: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        num: [
          { required: true, message: "退货数量不能为空", trigger: "blur" }
        ],
        logisticsCompany: [
          { required: true, message: "不能为空", trigger: "change" }
        ],
        logisticsCode: [
          { required: true, message: "不能为空", trigger: "blur" }
        ],
        sendTime: [
          { required: true, message: "不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    listShop({type: 280}).then(response => {
      this.shopList = response.rows;
      if (this.shopList && this.shopList.length > 0) {
        this.queryParams.shopId = this.shopList[0].id
      }
      this.getList();
    });
    // this.getList();
  },
  methods: {
    amountFormatter(row, column, cellValue, index) {
      return '￥' + parseFloat(cellValue).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
    },
    /** 查询淘宝退款订单列表 */
    getList() {
      this.loading = true;
      listRefund(this.queryParams).then(response => {
        this.taoRefundList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        refundId: null,
        afterSalesType: null,
        tid: null,
        oid: null,
        refundPhase: null
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    handlePushOms(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否手动推送到OMS？').then(function() {
        return pushOms({ids:ids});
      }).then(() => {
        // this.getList();
        this.$modal.msgSuccess("推送成功");
      }).catch(() => {});
    },
    handlePull() {
      if(this.queryParams.shopId){
        this.pullLoading = true
        pullRefund({shopId:this.queryParams.shopId,updType:0}).then(response => {
          console.log('拉取JDVC退货接口返回=====',response)
          if(response.code === 1401) {
            MessageBox.confirm('Token已过期，需要重新授权！请前往店铺列表重新获取授权！', '系统提示', { confirmButtonText: '前往授权', cancelButtonText: '取消', type: 'warning' }).then(() => {
              this.$router.push({path:"/shop/shop_list",query:{type:5}})
              // isRelogin.show = false;
              // store.dispatch('LogOut').then(() => {
              // location.href = response.data.tokenRequestUrl+'?shopId='+this.queryParams.shopId
              // })
            }).catch(() => {
              isRelogin.show = false;
            });

            // return Promise.reject('无效的会话，或者会话已过期，请重新登录。')
          }else{
            this.$modal.msgSuccess(JSON.stringify(response));
            this.pullLoading = false
          }

        })
      }else{
        this.$modal.msgSuccess("请先选择店铺");
      }

      // this.$modal.msgSuccess("请先配置API");
    }
  }
};
</script>
