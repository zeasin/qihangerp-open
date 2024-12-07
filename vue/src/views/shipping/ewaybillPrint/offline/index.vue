<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="订单编号" prop="orderNum">
        <el-input
          v-model="queryParams.orderNum"
          placeholder="请输入订单编号"
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
            <span style="float: left">{{ item.name }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="下单时间" prop="orderTime">
          <el-date-picker clearable
            v-model="orderTime" value-format="yyyy-MM-dd"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleLogisticsUpdate"
        >手动填写物流单号</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-d-arrow-right"
          size="mini"
          :disabled="multiple"
          @click="handleBatchShipSend"
        >批量确认发货</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
<!--      <el-table-column label="订单ID" align="center" prop="id" />-->
      <el-table-column label="订单编号" align="left" prop="orderNum" />
      <el-table-column label="商品" >
          <template slot-scope="scope">
            <el-row v-for="item in scope.row.itemList" :key="item.id" :gutter="20">
            <div style="float: left;display: flex;align-items: center;" >
              <div style="margin-left:10px">
              <p>{{item.goodsTitle}}【{{item.goodsSpec}}&nbsp;】</p>
              <p>SKU编码：{{item.skuNum}}
                <el-tag size="small">x {{item.quantity}}</el-tag>
                </p>
              </div>
            </div>
            </el-row>
          </template>
      </el-table-column>
      <el-table-column label="收件人" prop="receiverName" >
        <template slot-scope="scope">
          {{scope.row.receiverName}}&nbsp; <br />
          {{scope.row.receiverMobile}}
        </template>
      </el-table-column>
      <el-table-column label="详细地址" prop="receiverName" >
        <template slot-scope="scope">
          {{scope.row.province}} {{scope.row.city}} {{scope.row.town}} <br />
          {{scope.row.address}}
        </template>
      </el-table-column>

      <el-table-column label="物流单号" align="center" prop="shippingNumber" >
        <template slot-scope="scope">
          {{scope.row.shippingNumber}}&nbsp; {{'【'+scope.row.shippingCompany+'】'}}<br />
          {{ parseTime(scope.row.shippingTime, '{y}-{m}-{d} {h}:{m}:{s}') }}
        </template>
      </el-table-column>
      <el-table-column label="下单时间" align="center" prop="orderTime" >
        <template slot-scope="scope">
          {{ parseTime(scope.row.orderTime) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="orderStatus" >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.orderStatus === 0" style="margin-bottom: 6px;">新订单</el-tag>
          <el-tag v-if="scope.row.orderStatus === 1" style="margin-bottom: 6px;">待发货</el-tag>
          <el-tag v-if="scope.row.orderStatus === 2" style="margin-bottom: 6px;">已发货</el-tag>
          <el-tag v-if="scope.row.orderStatus === 3" style="margin-bottom: 6px;">已完成</el-tag>
          <el-tag v-if="scope.row.orderStatus === 21" style="margin-bottom: 6px;">待付款</el-tag>
          <el-tag v-if="scope.row.orderStatus === 22" style="margin-bottom: 6px;">锁定</el-tag>
          <el-tag v-if="scope.row.orderStatus === 29" style="margin-bottom: 6px;">删除</el-tag>
          <el-tag v-if="scope.row.orderStatus === 11" style="margin-bottom: 6px;">已取消</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">

          <el-row>
            <el-button
              size="mini"
              plain
              type="success"
              icon="el-icon-edit"
              @click="handleLogisticsUpdate(scope.row)"
            >填写物流单号</el-button>
            <el-button
              size="mini"
              plain
              type="danger"
              icon="el-icon-d-arrow-right"
              @click="handleBatchShipSend(scope.row)"
            >确认发货</el-button>
          </el-row>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详情</el-button>
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
    <el-dialog title="手动填写物流单号发货" :visible.sync="shipOpen" width="500px" append-to-body>
      <el-form ref="form" :model="form"  :rules="rules" label-width="80px">
        <el-form-item label="快递公司" prop="shippingCompany">
          <el-select v-model="form.shippingCompany"  style="width: 300px;" placeholder="请选择快递公司" clearable >
            <el-option
              v-for="item in logisticsList"
              :key="item.id"
              :label="item.name"
              :value="item.code">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号" prop="shippingNumber">
          <el-input v-model="form.shippingNumber" placeholder="请输入快递单号" />
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="shippingSubmit">保存物流单号</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 订单详情对话框 -->
    <el-dialog :title="detailTitle" :visible.sync="detailOpen" width="1100px" append-to-body>
      <el-tabs v-model="activeName" >
        <el-tab-pane label="订单详情" name="orderDetail">
          <el-form ref="form" :model="form" :rules="rules" label-width="80px" inline>
            <el-descriptions title="订单信息">
              <el-descriptions-item label="ID">{{form.id}}</el-descriptions-item>
              <el-descriptions-item label="订单号">{{form.orderNum}}</el-descriptions-item>

              <el-descriptions-item label="店铺">
                {{ shopList.find(x=>x.id === form.shopId)?shopList.find(x=>x.id === form.shopId).name:'' }}
                <el-tag size="small" v-if="form.shopType === 1">天猫</el-tag>
                <el-tag size="small" v-if="form.shopType === 4">拼多多</el-tag>
                <el-tag size="small" v-if="form.shopType === 3">抖店</el-tag>
                <el-tag size="small" v-if="form.shopType === 2">京东POP</el-tag>
                <el-tag size="small" v-if="form.shopType === 5">京东自营</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="买家留言">
                {{form.buyerMemo}}
              </el-descriptions-item>
              <el-descriptions-item label="卖家留言">
                {{form.sellerMemo}}
              </el-descriptions-item>

              <el-descriptions-item label="备注">
                {{form.remark}}
              </el-descriptions-item>
              <el-descriptions-item label="创建时间">
                {{ parseTime(form.createTime) }}
                <!-- <el-date-picker
                disabled
                  v-model="form.orderCreateTime"
                  type="datetime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  placeholder="请选择订单创建时间">
                </el-date-picker> -->
              </el-descriptions-item>
              <el-descriptions-item label="支付时间"> {{ form.payTime }}</el-descriptions-item>
              <el-descriptions-item label="最后更新时间"> {{ form.updateTime }}</el-descriptions-item>

              <el-descriptions-item label="订单状态">
                <el-tag v-if="form.orderStatus === 1" style="margin-bottom: 6px;">待发货</el-tag>
                <el-tag v-if="form.orderStatus === 2" style="margin-bottom: 6px;">已出库</el-tag>
                <el-tag v-if="form.orderStatus === 3" style="margin-bottom: 6px;">已发货</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="退款状态">
                <el-tag v-if="form.refundStatus === 1">无售后或售后关闭</el-tag>
                <el-tag v-if="form.refundStatus === 2">售后处理中</el-tag>
                <el-tag v-if="form.refundStatus === 3">退款中</el-tag>
                <el-tag v-if="form.refundStatus === 4">退款成功</el-tag>
              </el-descriptions-item>

            </el-descriptions>
            <el-descriptions title="付款信息">
              <el-descriptions-item label="商品总额">{{amountFormatter(null,null,form.goodsAmount,0)}}</el-descriptions-item>
              <el-descriptions-item label="订单金额">{{amountFormatter(null,null,form.amount,0)}}</el-descriptions-item>
              <el-descriptions-item label="实际支付金额">{{amountFormatter(null,null,form.payment,0)}}</el-descriptions-item>
              <el-descriptions-item label="平台优惠金额">{{amountFormatter(null,null,form.platformDiscount,0)}}</el-descriptions-item>
              <el-descriptions-item label="商家优惠金额">{{amountFormatter(null,null,form.sellerDiscount,0)}}</el-descriptions-item>
            </el-descriptions>


            <el-descriptions title="收货信息">
              <el-descriptions-item label="收件人姓名">{{form.receiverName}}</el-descriptions-item>
              <el-descriptions-item label="收件人手机号">{{form.receiverMobile}}</el-descriptions-item>
              <el-descriptions-item label="省市区">{{form.province}}{{form.city}}{{form.town}}</el-descriptions-item>
              <el-descriptions-item label="详细地址">{{form.address}}</el-descriptions-item>
            </el-descriptions>
            <el-descriptions title="发货信息">

              <el-descriptions-item label="物流公司">{{form.shippingCompany}}</el-descriptions-item>
              <el-descriptions-item label="物流单号">{{form.shippingNumber}}</el-descriptions-item>
              <el-descriptions-item label="发货时间">{{form.shippingTime}}</el-descriptions-item>
            </el-descriptions>

          </el-form>

        </el-tab-pane>
        <el-tab-pane label="商品列表" name="orderItems" lazy>
          <el-table :data="form.itemList"  style="margin-bottom: 10px;">
            <!-- <el-table-column type="selection" width="50" align="center" /> -->
            <el-table-column label="序号" align="center" type="index" width="50"/>

            <el-table-column label="商品图片" prop="goodsImg" width="80">
              <template slot-scope="scope">
                <el-image style="width: 70px; height: 70px" :src="scope.row.goodsImg"></el-image>
              </template>
            </el-table-column>
            <el-table-column label="商品标题" prop="goodsTitle" ></el-table-column>
            <el-table-column label="SKU" prop="goodsSpec" width="150"></el-table-column>
            <el-table-column label="sku编码" prop="skuNum"></el-table-column>
            <el-table-column label="外部ERP Sku编码" prop="outerErpSkuId"></el-table-column>
            <el-table-column label="单价" prop="goodsPrice"></el-table-column>
            <el-table-column label="子订单金额" prop="itemAmount"></el-table-column>
            <el-table-column label="实付金额" prop="payment"></el-table-column>
            <el-table-column label="数量" prop="quantity"></el-table-column>
            <el-table-column label="状态" prop="orderStatus">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.orderStatus === 1">待发货</el-tag>
                <el-tag v-if="scope.row.orderStatus === 2">已发货</el-tag>
                <el-tag v-if="scope.row.orderStatus === 3">已完成</el-tag>
                <el-tag v-if="scope.row.orderStatus === 11">已取消</el-tag>
              </template>
            </el-table-column>
<!--            <el-table-column label="退款状态" prop="refundStatus">-->
<!--              <template slot-scope="scope">-->
<!--                &lt;!&ndash; 1：无售后或售后关闭，2：售后处理中，3：退款中，4： 退款成功 &ndash;&gt;-->
<!--                <el-tag v-if="scope.row.refundStatus === 1">无售后或售后关闭</el-tag>-->
<!--                <el-tag v-if="scope.row.refundStatus === 2">售后处理中</el-tag>-->
<!--                <el-tag v-if="scope.row.refundStatus === 3">退款中</el-tag>-->
<!--                <el-tag v-if="scope.row.refundStatus === 4">退款成功</el-tag>-->
<!--              </template>-->
<!--            </el-table-column>-->
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="优惠明细" name="orderCou" lazy>
          <el-table :data="form.discounts"  style="margin-bottom: 10px;">
            <el-table-column label="序号" align="center" type="index" width="50"/>
            <el-table-column label="优惠名称" prop="name" ></el-table-column>
            <el-table-column label="优惠金额" prop="discountAmount"></el-table-column>
            <el-table-column label="优惠描述" prop="description"></el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="物流单" name="orderLog" lazy>

        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script>
import {listOrder, getOrder, delOrder, addOrder, updateOrder, pushOms} from "@/api/offline/order";
import { listShop } from "@/api/shop/shop";
import {listLogisticsStatus} from "@/api/shop/shop";
import {batchOrderSend, logisticsUpdate, shipOrder} from "@/api/offline/ship";

export default {
  name: "printOffline",
  data() {
    return {
      // 遮罩层
      loading: true,
      pushLoading: false,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedSShopOrderItem: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 店铺订单表格数据
      orderList: [],
      logisticsList: [],
      shopList:[],
      // 弹出层标题
      detailTitle:'订单详情',
      detailOpen:false,
      shipOpen:false,
      isAudit:false,
      activeName: 'orderDetail',
      orderTime: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNum: null,
        shopId: null,
        startTime: null,
        endTime: null,
        refundStatus: 1,
        orderStatus: '1'
      },
      // 表单参数
      form: {
        orderNum:null,
        shippingCompany:null,
        shippingNumber:null
      },
      // 表单校验
      rules: {
        shippingCompany: [{ required: true, message: "不能为空", trigger: "blur" }],
        shippingNumber: [{ required: true, message: "不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
     listShop({type: 999}).then(response => {
        this.shopList = response.rows;
       this.getList();
      });
    listLogisticsStatus({shopType:999,status:1}).then(response => {
      this.logisticsList = response.rows;
    });

  },
  methods: {
    amountFormatter(row, column, cellValue, index) {
      return '￥' + parseFloat(cellValue).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
    },

    /** 查询店铺订单列表 */
    getList() {
      console.log('=====搜索条件：=====',this.queryParams)
      if(this.orderTime){
        this.queryParams.startTime = this.orderTime[0]
        this.queryParams.endTime = this.orderTime[1]
      }else {
        this.queryParams.startTime = null
        this.queryParams.endTime = null
      }
      this.loading = true;
      listOrder(this.queryParams).then(response => {
        this.orderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.orderTime=null
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.orderNum)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    handleBatchShipSend(row) {
      const orderNums = row.orderNum || this.ids;
      this.$modal.confirm('选中的订单有物流单号将会发货，没有物流单号将不会发货，是否确认操作？').then(function() {
        console.log("=======批量发货=====",orderNums)
        let orderNumsParam = [];
        if(Array.isArray(orderNums)){
          orderNumsParam = orderNums
        }else {
          orderNumsParam.push(orderNums)
        }

        batchOrderSend({orderNums:orderNumsParam}).then(resp=>{
           this.getList()
         });
      }).then(() => {
        // this.getList();
        this.$modal.msgSuccess("发货成功");
      }).catch(() => {});
    },
    shippingSubmit(){
      this.$refs["form"].validate(valid => {
        if (valid) {
          console.log("手动填写物流单号====提交=====",this.form)
          logisticsUpdate(this.form).then(resp=>{
            this.shipOpen=false
            this.$modal.msgSuccess("发货成功");
            this.getList()
          });
        }
      });
    },
    handleLogisticsUpdate(row){
      const orderNum = row.orderNum || this.ids
      console.log(orderNum)
      this.form.orderNum = Array.isArray(orderNum) ? orderNum[0] : orderNum
      this.shipOpen = true
    },
    cancel(){
      this.form.orderNum = null
      this.shipOpen = false
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      this.reset();
      const id = row.id || this.ids
      getOrder(id).then(response => {
        this.form = response.data;
        // this.$nextTick(()=>{
        //   this.form.shipType = response.data.shipType
        // })
        this.detailOpen = true;
        this.detailTitle = "订单详情";
      });
      this.isAudit = false
    },
  }
};
</script>
