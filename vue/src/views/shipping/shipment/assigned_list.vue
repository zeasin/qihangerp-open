<template>
  <div class="app-container">
    <el-form :model="printParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单号" prop="orderNum">
        <el-input
          v-model="queryParams.orderNum"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="店铺" prop="shopId">
        <el-select v-model="queryParams.shopId" placeholder="请选择店铺" @change="handleQuery">
          <el-option
            v-for="item in shopList"
            :key="item.id"
            :label="item.name"
            :value="item.id">
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 3">拼多多</span>
            <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 5">微信小店</span>
            <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 9">其他渠道</span>
          </el-option>
        </el-select>
      </el-form-item>
<!--      <el-form-item label="供应商" prop="shipSupplierId">-->
<!--        <el-select clearable-->
<!--                   v-model="queryParams.shipSupplierId"-->
<!--                   filterable-->
<!--                   placeholder="请输入供应商名称">-->
<!--          <el-option-->
<!--            v-for="item in supplierList"-->
<!--            :key="item.id"-->
<!--            :label="item.name"-->
<!--            :value="item.id">-->
<!--          </el-option>-->
<!--        </el-select>-->
<!--      </el-form-item>-->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="primary"-->
<!--          plain-->
<!--          :disabled="single"-->
<!--          icon="el-icon-download"-->
<!--          size="mini"-->
<!--          @click="handleShip"-->
<!--        >填写供应商发货物流</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type=""-->
<!--          plain-->
<!--          :disabled="single"-->
<!--          icon="el-icon-download"-->
<!--          size="mini"-->
<!--          @click="allocateShipmentToSupplier"-->
<!--        >分配给供应商发货</el-button>-->
<!--      </el-col>-->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
       <el-table-column type="selection" width="55" align="center" />
<!--      <el-table-column label="ID" align="center" prop="id" />-->
      <el-table-column label="订单号" align="left" prop="orderNum" width="200">
        <template slot-scope="scope">
          <span>{{scope.row.orderNum}}</span>
          <br/>
          <el-tag type="info">{{ shopList.find(x=>x.id == scope.row.shopId) ? shopList.find(x=>x.id == scope.row.shopId).name : '' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="商品" width="400">
        <template slot-scope="scope">
          <el-row v-for="item in scope.row.itemList" :key="item.id" :gutter="20">
            <div style="float: left;display: flex;align-items: center;" >
              <image-preview :src="item.goodsImg" :width="40" :height="40"/>
              <div style="margin-left:10px">
                <div>{{item.goodsTitle}}</div>
                <div>
                  <div>
                    {{ getSkuValues(item.goodsSpec)}}
                    数量：<el-tag size="small">x {{item.quantity}}</el-tag>
                  </div>
                </div>
              </div>
            </div>
          </el-row>
        </template>
      </el-table-column>
      <el-table-column label="订单金额" align="center" prop="orderAmount" :formatter="amountFormatter"/>
      <el-table-column label="下单时间" align="center" prop="orderCreateTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.orderTime) }}</span>
        </template>
      </el-table-column>


      <el-table-column label="收件人" prop="receiverName" width="200px">
        <template slot-scope="scope">
          {{scope.row.receiverName}}&nbsp;
          {{scope.row.receiverMobile}} <br />
          {{scope.row.province}} {{scope.row.city}} {{scope.row.town}} <br />
        </template>
      </el-table-column>
      <el-table-column label="订单备注" align="center" >
        <template slot-scope="scope">
          <div style="color: #ed5565">{{scope.row.remark}}</div>
          <div style="color: #ed5565">{{scope.row.buyerMemo}}</div>
          <div style="color: #ed5565">{{scope.row.sellerMemo}}</div>
        </template>
      </el-table-column>
      <el-table-column label="发货状态" align="center" prop="shipStatus" >
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.shipStatus === 0" style="margin-bottom: 6px;">待发货</el-tag>
          <el-tag type="success" v-if="scope.row.shipStatus === 1" style="margin-bottom: 6px;">已分配发货</el-tag>
          <el-tag type="success" v-if="scope.row.shipStatus === 2" style="margin-bottom: 6px;">已发货</el-tag>
          <br />
          <!-- 1：无售后或售后关闭，2：售后处理中，3：退款中，4： 退款成功 -->
          <el-tag v-if="scope.row.shipper == 0" type="info">自己发货</el-tag>
          <el-tag v-if="scope.row.shipper == 1" type="info">联合发货</el-tag>
          <el-tag v-if="scope.row.shipper == 2" type="info">供应商发货</el-tag>
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


    <!-- 打包发货对话框 -->
    <el-dialog title="打包发货" :visible.sync="shipOpen" width="1100px" append-to-body>

      <el-form ref="form" :model="form" :rules="rules" label-width="80px" >
        <el-descriptions title="订单信息">
          <el-descriptions-item label="ID">{{form.id}}</el-descriptions-item>
          <el-descriptions-item label="订单号">{{form.orderNum}}</el-descriptions-item>

          <el-descriptions-item label="店铺">
            {{ shopList.find(x=>x.id == form.shopId)?shopList.find(x=>x.id == form.shopId).name:'' }}
            <el-tag size="small" v-if="form.shopType === 5">微信小店</el-tag>

          </el-descriptions-item>
          <el-descriptions-item label="买家留言">
            {{form.buyerMemo}}
          </el-descriptions-item>
          <el-descriptions-item label="备注">
            {{form.remark}}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ parseTime(form.createTime) }}
          </el-descriptions-item>

          <el-descriptions-item label="订单状态">
            <el-tag v-if="form.orderStatus === 1" style="margin-bottom: 6px;">待发货</el-tag>
            <el-tag v-if="form.orderStatus === 2" style="margin-bottom: 6px;">已发货</el-tag>
            <el-tag v-if="form.orderStatus === 3" style="margin-bottom: 6px;">已签收</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="退款状态">
            <el-tag v-if="form.refundStatus === 1">无售后或售后关闭</el-tag>
            <el-tag v-if="form.refundStatus === 2">售后处理中</el-tag>
            <el-tag v-if="form.refundStatus === 3">退款中</el-tag>
            <el-tag v-if="form.refundStatus === 4">退款成功</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="">
<!--            <el-tag v-if="form.shipType === 1"  type="danger">供应商代发</el-tag>-->
<!--            <el-tag v-if="form.shipType === 0" type="danger">仓库发货</el-tag>-->
          </el-descriptions-item>
          <el-descriptions-item label="收件人姓名">{{form.receiverName}}</el-descriptions-item>
          <el-descriptions-item label="收件人手机号">{{form.receiverMobile}}</el-descriptions-item>
          <el-descriptions-item label="省市区">{{form.province}}{{form.city}}{{form.town}}</el-descriptions-item>
          <el-descriptions-item label="详细地址">{{form.address}}</el-descriptions-item>



        </el-descriptions>

        <el-divider content-position="center">商品明细</el-divider>
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
          <!--          <el-table-column label="单价" prop="goodsPrice"></el-table-column>-->
          <el-table-column label="数量" prop="quantity"></el-table-column>
          <!-- <el-table-column label="商品金额" prop="itemAmount"></el-table-column> -->
        </el-table>
        <el-form-item label="包裹尺寸" prop="height">
          <el-input type="number" v-model.number="form.length" placeholder="长mm" style="width:90px" /> x
          <el-input type="number"  v-model.number="form.width" placeholder="宽mm" style="width:90px" /> x
          <el-input type="number" v-model.number="form.height" placeholder="高mm" style="width:90px" />
        </el-form-item>
        <el-form-item label="包裹重量" prop="weight">
          <el-input type="number" v-model.number="form.weight" placeholder="请输入包裹重量（单位g）" style="width:300px" />
        </el-form-item>
        <el-row>
          <el-col>
            <el-form-item label="物流公司" prop="shippingCompany">
              <!--              <el-input v-model="form.shippingCompany" placeholder="请输入物流公司" style="width:300px" />-->
              <el-select v-model="form.shippingCompany" filterable r placeholder="选择快递公司" style="width:300px">
                <el-option v-for="item in logisticsList" :key="item.id" :label="item.name" :value="item.id">
                  <span style="float: left">{{ item.name }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px" >{{item.number}}</span>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="物流单号" prop="shippingNumber">
              <el-input v-model="form.shippingNumber" placeholder="请输入物流单号" style="width:300px" />
            </el-form-item>
            <el-form-item label="物流费用" prop="shippingCost">
              <el-input v-model="form.shippingCost" placeholder="请输入物流费用" style="width:300px" />
            </el-form-item>
            <el-form-item label="发货人" prop="shippingMan">
              <el-input v-model="form.shippingMan" placeholder="请输入发货人" style="width:300px" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitShipForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import {
  getOrder,
  manualShipmentOrder,
  allocateShipmentOrder,
  assignedShipmentList
} from "@/api/order/order";


import {listLogisticsStatus} from "@/api/api/logistics";
import {listShop} from "@/api/shop/shop";
import {amountFormatter, parseTime} from "@/utils/zhijian";

export default {
  name: "ShipmentAssigned",
  data() {
    return {
      // 遮罩层
      loading: true,
      getCodeOpen: false,
      // 选中数组
      ids: [],
      shopList: [],
      orderList: [],
      logisticsList: [],
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
      // 是否显示弹出层
      open: false,
      shipOpen: false,
      allocateShipmentOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        shopType:null,
        shopId:null,
      },
      // 打印参数
      printParams: {
        deliver: null,
        printer: null
      },
      // 表单参数
      form: {
        weight:0.0
      },
      printerList: [],
      deliverList: [],
      // 表单校验
      rules: {
        height: [{ required: true, message: '不能为空' }],
        weight: [{ required: true, message: '不能为空' }],
        shippingNumber: [{ required: true, message: '不能为空' }],
        shippingCompany: [{ required: true, message: '不能为空' }],
        shippingCost: [{ required: true, message: '不能为空' }],
        // shippingMan: [{ required: true, message: '不能为空' }],
        receiverName: [{ required: true, message: '不能为空' }],
        receiverMobile: [{ required: true, message: '不能为空' }],
        address: [{ required: true, message: '不能为空' }],
      }
    };
  },
  created() {

    listShop().then(response => {
      this.shopList = response.rows;

      this.getList();
    });

  },
  methods: {
    amountFormatter,
    parseTime,
    getSkuValues(spec){
      try {
        // 解析 JSON，返回一个数组
        const parsedSpec = JSON.parse(spec) || [];

        // 使用 map 提取所有 value，使用 join() 用逗号连接
        return parsedSpec.map(item => item.attr_value).join(', ') || '';
      } catch (error) {
        return spec; // 如果 JSON 解析出错，返回空字符串
      }
    },
    /** 查询列表 */
    getList() {
      this.loading = true;
      assignedShipmentList(this.queryParams).then(response => {
        this.orderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.allocateShipmentOpen = false;
      this.shipOpen = false;
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

    // 手动发货表单
    submitShipForm(){
      this.$refs["form"].validate(valid => {
        if (valid) {

        }
      })
    },

  }
};
</script>
