<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="店铺" prop="name">
        <el-select v-model="queryParams.shopId" placeholder="请选择店铺" clearable @change="handleQuery">
          <el-option
            v-for="item in shopList"
            :key="item.id"
            :label="item.name"
            :value="item.id">
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 1">淘宝天猫</span>
            <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 2">京东POP</span>
            <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 3">抖店</span>
            <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 4">拼多多</span>
            <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 5">京东自营</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="订单号" prop="orderNums">
        <el-input
          v-model="queryParams.orderNums"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="快递单号" prop="waybillCode">
        <el-input
          v-model="queryParams.waybillCode"
          placeholder="请输入快递单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleShipping"
        >手动订单发货</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleShippingLog"
        >ERP发货推送记录</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" >
<!--      <el-table-column type="selection" width="55" align="center" />-->
      <el-table-column label="主订单" align="center" prop="orderNums" />
      <el-table-column label="子订单" align="center" prop="subOrderNums" />
      <el-table-column label="店铺" align="center" prop="shopId" >
        <template slot-scope="scope">
          <span>{{ shopList.find(x=>x.id === scope.row.shopId).name  }}</span>
        </template>
      </el-table-column>
      <el-table-column label="类型" align="center" prop="shippingType" >
        <template slot-scope="scope">
          <el-tag size="small" v-if="scope.row.shippingType === 1">订单发货</el-tag>
          <el-tag size="small" v-if="scope.row.shippingType === 2">商品补发</el-tag>
          <el-tag size="small" v-if="scope.row.shippingType === 3">商品换货</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="快递公司" align="center" prop="logisticsCompany" />
       <el-table-column label="快递单号" align="center" prop="waybillCode" />
       <el-table-column label="备注" align="center" prop="remark" />
       <el-table-column label="发货时间" align="center" prop="createTime" >
         <template slot-scope="scope">
           <span>{{ parseTime(scope.row.createTime) }}</span>
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
    <!-- 对话框 -->
    <el-dialog title="手动发货" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px" inline>
        <el-row>
        <el-form-item label="店铺" prop="shopId">
          <el-select v-model="form.shopId" placeholder="请选择店铺" style="width: 300px;" clearable @change="shopChange">
            <el-option
              v-for="item in shopList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              <span style="float: left">{{ item.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 1">淘宝天猫</span>
              <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 2">京东POP</span>
              <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 3">抖店</span>
              <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 4">拼多多</span>
              <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 5">京东自营</span>
            </el-option>
          </el-select>
        </el-form-item>
        </el-row>
        <el-row>
        <el-form-item label="快递公司" prop="shipCompany">
          <el-select v-model="form.shipCompany"  style="width: 300px;" placeholder="请选择快递公司" clearable >
            <el-option
              v-for="item in logisticsList"
              :key="item.id"
              :label="item.name"
              :value="item.code">
            </el-option>
          </el-select>
        </el-form-item>
        </el-row>
        <el-row>
          <el-form-item label="收件人" prop="receiver">
            <el-select v-model="form.receiver"  style="width: 680px;" filterable remote reserve-keyword placeholder="请选择收件人"
                       :remote-method="searchOrderConsignee" :loading="consigneeListLoading" @change="consigneeChange">
              <el-option v-for="item in consigneeList" :key="item.id"
                         :label="item.receiverName + ' - ' + item.receiverMobile + ' ' + item.province + ' ' + item.city + ' ' + item.town + ' ' + item.address "
                         :value="item">
              </el-option>
            </el-select>
          </el-form-item>
        </el-row>
        <el-row>
        <el-form-item label="快递单号"  prop="shipCode">
          <el-input v-model="form.shipCode" placeholder="请输入快递单号" style="width: 300px;"/>
        </el-form-item>
        </el-row>

<!--        <el-form-item label="快递费用" prop="shipFee">-->
<!--          <el-input v-model="form.shipFee" placeholder="请输入快递费用" />-->
<!--        </el-form-item>-->
<!--        <el-form-item label="发货人" prop="shipOperator">-->
<!--          <el-input v-model="form.shipOperator" placeholder="请输入发货人" />-->
<!--        </el-form-item>-->
<!--        <el-form-item label="包裹重量" prop="packageWeight">-->
<!--          <el-input v-model="form.packageWeight" placeholder="请输入包裹重量" />-->
<!--        </el-form-item>-->
<!--        <el-form-item label="包裹长度" prop="packageLength">-->
<!--          <el-input v-model="form.packageLength" placeholder="包裹长度" />-->
<!--        </el-form-item>-->
<!--        <el-form-item label="包裹宽度" prop="packageWidth">-->
<!--          <el-input v-model="form.width" placeholder="请输入包裹宽度" />-->
<!--        </el-form-item>-->
<!--        <el-form-item label="包裹高度" prop="packageHeight">-->
<!--          <el-input v-model="form.height" placeholder="请输入包裹高度" />-->
<!--        </el-form-item>-->
        <el-row>
        <el-form-item label="订单明细">

        </el-form-item>
        </el-row>
        <!-- <el-divider content-position="center" style="margin-left: 98px;">商品信息</el-divider> -->

        <el-table :data="orderItemList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" type="index" width="50"/>
          <el-table-column label="订单号" prop="orderNum" ></el-table-column>
          <el-table-column label="子订单号" prop="subOrderNum" ></el-table-column>
          <el-table-column label="商品标题" prop="goodsTitle" ></el-table-column>
          <el-table-column label="商品规格" prop="goodsSpec" ></el-table-column>
          <el-table-column label="商品图片" prop="goodsImg" width="150">
            <template slot-scope="scope">
              <!-- <el-input v-model="scope.row.goodsImg" placeholder="请输入商品图片" /> -->
              <el-image style="width: 70px; height: 70px" :src="scope.row.goodsImg"></el-image>
            </template>
          </el-table-column>
          <el-table-column label="SKU编码" prop="skuNum" ></el-table-column>
          <el-table-column label="数量" prop="quantity" ></el-table-column>
          <el-table-column label="退款状态" prop="refundStatus" width="150">
            <template slot-scope="scope">
              <el-tag size="small" v-if="scope.row.refundStatus === 1">无售后或售后关闭</el-tag>
              <el-tag size="small" v-if="scope.row.refundStatus === 2">售后处理中</el-tag>
              <el-tag size="small" v-if="scope.row.refundStatus === 3">退款中</el-tag>
              <el-tag size="small" v-if="scope.row.refundStatus === 4">退款成功</el-tag>
            </template>
          </el-table-column>

        </el-table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listLogistics, listLogisticsStatus, listShop} from "@/api/shop/shop";
import {MessageBox} from "element-ui";
import {handShip, listShipping, searchOrderConsignee, searchOrderItemByReceiverMobile} from "@/api/order/shipping";


export default {
  name: "Shop",
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
      // 店铺表格数据
      logisticsList: [],
      shopList: [],
      dataList: [],
      // 收货人列表
      consigneeList: [],
      //订单明细list
      orderItemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      consigneeListLoading: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNum:null,
        shipCode:null,
        shopId:null
      },
      // 表单参数
      form: {
        shopId:null,
        shipCompany:null,
        shipCode:null,
        receiverName:null,
        receiverMobile:null,
        province:null,
        city:null,
        town:null,
        address:null,
        itemIds:undefined
      },
      // 表单校验
      rules: {
        shopId: [{ required: true, message: "不能为空", trigger: "blur" }],
        receiver: [{ required: true, message: "不能为空", trigger: "blur" }],
        shipCompany: [{ required: true, message: "不能为空", trigger: "blur" }],
        shipCode: [{ required: true, message: "不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    listShop({}).then(response => {
      this.shopList = response.rows;
    });

    this.getList();
  },
  mounted() {
  },
  methods: {
    /** 查询店铺列表 */
    getList() {
      this.loading = true;
      listShipping(this.queryParams).then(response => {
        this.dataList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 店铺选择 */
    shopChange(){
      this.logisticsList = []
      let shop_type = this.shopList.find(x=>x.id == this.form.shopId)?this.shopList.find(x=>x.id == this.form.shopId).type:null;
      listLogisticsStatus({shopType:shop_type,status:1}).then(response => {
        this.logisticsList = response.rows;
      });
    },
    /** 搜索订单待发货联系人 */
    searchOrderConsignee(query){
      // console.log('====搜索订单待发货联系人,',this.form.consignee)
      if(query) {
        searchOrderConsignee({consignee: query}).then(response => {
          this.consigneeList = response.rows;
        });
      }
    },
    /** 收货人变更  */
    consigneeChange(receiver){
      console.log('====选中订单收件人,',receiver)
      // 根据手机号搜索订单信息
      if(receiver && receiver.receiverMobile) {
        this.form.receiverName = receiver.receiverName
        this.form.receiverMobile = receiver.receiverMobile
        this.form.province = receiver.province
        this.form.city = receiver.city
        this.form.town = receiver.town
        this.form.address = receiver.address

        searchOrderItemByReceiverMobile({receiverMobile: receiver.receiverMobile}).then(response => {
          this.orderItemList = response.rows;
        });
      }else{
        this.form.receiverName = null
        this.form.receiverMobile = null
        this.form.province = null
        this.form.city = null
        this.form.town = null
        this.form.address = null
      }
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
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
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {};
      this.resetForm("form");
    },
    handleShipping() {
      this.open = true
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if(this.ids && this.ids.length>0){
            this.form.itemIds = this.ids
            handShip(this.form).then(response => {
              this.$modal.msgSuccess("发货成功");
              this.open = false;
              this.getList();
            });
          }else{
            this.$modal.msgError("请选择订单明细！");
          }


        }
      });
    },
  }
};
</script>
