<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
<!--      <el-form-item label="店铺" prop="shopId">-->
<!--        <el-select v-model="queryParams.shopId" filterable  placeholder="搜索店铺" >-->
<!--          <el-option v-for="item in shopList" :key="item.id" :label="item.name" :value="item.id">-->
<!--            <span style="float: left">{{ item.name }}</span>-->
<!--              <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 4">淘宝天猫</span>-->
<!--              <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 5">拼多多</span>-->
<!--              <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 6">抖店</span>-->
<!--              <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 7">小红书</span>-->
<!--              <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 13">快手小店</span>-->
<!--              <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 99">其他</span>-->
<!--          </el-option>-->
<!--          </el-select>-->
<!--      </el-form-item>-->
<!--      <el-form-item label="供应商" prop="supplierId">-->
<!--        <el-select-->
<!--          v-model="queryParams.supplierId"-->
<!--          filterable-->
<!--          placeholder="请输入供应商名称">-->
<!--          <el-option-->
<!--            v-for="item in supplierList"-->
<!--            :key="item.id"-->
<!--            :label="item.name"-->
<!--            :value="item.id">-->
<!--          </el-option>-->
<!--        </el-select>-->
<!--      </el-form-item>-->

      <el-form-item label="订单编号" prop="orderNum">
        <el-input
          v-model="queryParams.orderNum"
          placeholder="请输入订单编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="供应商" prop="shipSupplierId">
        <el-select v-model="queryParams.shipSupplierId" placeholder="请选择供应商" clearable @change="handleQuery">
          <el-option
            v-for="item in supplierList"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
<!--      <el-form-item label="订单日期" prop="orderDate">-->
<!--        <el-date-picker clearable-->
<!--          v-model="queryParams.orderDate"-->
<!--          type="date"-->
<!--          value-format="yyyy-MM-dd"-->
<!--          placeholder="请选择订单日期">-->
<!--        </el-date-picker>-->
<!--      </el-form-item>-->
<!--      <el-form-item label="商品ID" prop="goodsId">-->
<!--        <el-input-->
<!--          v-model="queryParams.goodsId"-->
<!--          placeholder="请输入erp系统商品id"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->

<!--      <el-form-item label="商品编码" prop="goodsNum">-->
<!--        <el-input-->
<!--          v-model="queryParams.goodsNum"-->
<!--          placeholder="请输入商品编码"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->

<!--      <el-form-item label="物流单号" prop="shipNo">-->
<!--        <el-input-->
<!--          v-model="queryParams.shipNo"-->
<!--          placeholder="请输入物流单号"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->

<!--      <el-form-item label="发货时间" prop="shipTime">-->
<!--        <el-date-picker clearable-->
<!--          v-model="queryParams.shipTime"-->
<!--          type="date"-->
<!--          value-format="yyyy-MM-dd"-->
<!--          placeholder="请选择发货时间">-->
<!--        </el-date-picker>-->
<!--      </el-form-item>-->

<!--      <el-form-item>-->
<!--        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>-->
<!--        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>-->
<!--      </el-form-item>-->
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-col>
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="danger"-->
<!--          plain-->
<!--          icon="el-icon-document-checked"-->
<!--          size="mini"-->
<!--          :disabled="multiple"-->
<!--          @click="handleDelete"-->
<!--        >批量付款确认</el-button>-->
<!--      </el-col>-->
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['scm:agentShipping:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="agentShippingList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="主键ID" align="center" prop="id" /> -->
      <el-table-column label="订单编号" align="left" prop="orderNum" width="200px">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
          >{{scope.row.orderNum}} </el-button>
          <i class="el-icon-copy-document tag-copy" :data-clipboard-text="scope.row.orderNum" @click="copyActiveCode($event,scope.row.orderNum)" ></i>
        </template>
      </el-table-column>
      <el-table-column label="供应商" align="center" prop="shipSupplier" width="220px">
        <template slot-scope="scope">
          <el-tag>{{scope.row.shipSupplier}}</el-tag>
<!--          <span>{{ shopList.find(x=>x.id === scope.row.shopId)?shopList.find(x=>x.id === scope.row.shopId).name:''  }}</span>-->
        </template>
      </el-table-column>
      <!-- <el-table-column label="店铺平台" align="center" prop="shopType" >
         <template slot-scope="scope">
          <el-tag size="small" v-if="scope.row.shopType === 4">淘宝</el-tag>
          <el-tag size="small" v-if="scope.row.shopType === 5">拼多多</el-tag>
          <el-tag size="small" v-if="scope.row.shopType === 6">抖店</el-tag>
          <el-tag size="small" v-if="scope.row.shopType === 7">小红书</el-tag>
         </template>
      </el-table-column> -->

<!--      <el-table-column label="子订单编号" align="center" prop="subOrderNum" />-->

      <el-table-column label="商品明细" align="center" width="900px" >
        <template slot="header">
          <table>
            <th>
              <td width="50px">图片</td>
              <td width="250px" align="left">标题</td>
              <td width="150" align="left">SKU名</td>
              <td width="200" align="left">Sku编码</td>
              <!--              <td width="150" align="left">平台SkuId</td>-->
              <td width="50" align="left">数量</td>
            </th>
          </table>
        </template>
        <template slot-scope="scope" >
          <el-table :data="scope.row.items" :show-header="false" :cell-style="{border:0 + 'px' }"  :row-style="{border:0 + 'px' }" >
            <el-table-column label="商品图片" width="50px">
              <template slot-scope="scope">
                <!--                <el-image  style="width: 40px; height: 40px;" :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]"></el-image>-->
                <image-preview :src="scope.row.goodsImg" :width="40" :height="40"/>
              </template>
            </el-table-column>
            <el-table-column label="商品名" align="left" width="250px" prop="goodsTitle" >
              <template slot-scope="scope">
                {{scope.row.goodsTitle}}
                <!--                <el-tag size="small" v-if="scope.row.refundStatus === 1">无售后或售后关闭</el-tag>-->
                <el-tag size="small" v-if="scope.row.refundStatus === 2">售后处理中</el-tag>
                <el-tag size="small" v-if="scope.row.refundStatus === 3">退款中</el-tag>
                <el-tag size="small" v-if="scope.row.refundStatus === 4">退款成功</el-tag>
                <el-tag size="small" v-if="scope.row.refundStatus === 11">已取消</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="SKU名" align="left" prop="skuName" width="150"  :show-overflow-tooltip="true">
              <template slot-scope="scope">
                {{ scope.row.skuName }}
              </template>
            </el-table-column>
            <el-table-column label="Sku编码" align="left" prop="skuNum" width="200"/>
            <!--            <el-table-column label="平台SkuId" align="left" prop="skuId" width="150"/>-->
            <el-table-column label="商品数量" align="center" prop="quantity" width="50px">
              <template slot-scope="scope">
                <el-tag size="small" type="danger">{{scope.row.quantity}}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="orderDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag size="small" v-if="scope.row.shipStatus === 1">待发货</el-tag>
          <el-tag size="small" v-if="scope.row.shipStatus === 2">已发货</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" >
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.shipStatus === 1"
            size="mini"
            plain
            type="primary"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['scm:agentShipping:edit']"
          >供应商发货</el-button>
<!--          <el-button-->
<!--            v-if="scope.row.status === 1"-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-document-checked"-->
<!--            @click="handleDelete(scope.row)"-->
<!--            v-hasPermi="['scm:agentShipping:remove']"-->
<!--          >付款确认</el-button>-->
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

    <!-- 添加或修改供应商代发货对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="订单编号" prop="orderNum">
          <el-input v-model="form.orderNum" placeholder="请输入订单编号" disabled/>
        </el-form-item>

<!--        <el-form-item label="订单日期" prop="orderDate">-->
<!--          <el-date-picker clearable-->
<!--            v-model="form.orderTime"-->
<!--                          disabled-->
<!--            type="datetime"-->
<!--            value-format="yyyy-MM-dd HH:mm:ss"-->
<!--            placeholder="请选择订单日期">-->
<!--          </el-date-picker>-->
<!--        </el-form-item>-->

<!--        <el-form-item label="商品总价" prop="goodsAmount">-->
<!--          <el-input type="number" v-model.number="form.goodsAmount"  placeholder="请输入商品总价" />-->
<!--        </el-form-item>-->
<!--        <el-form-item label="商品数量" prop="quantity">-->
<!--          <el-input v-model="form.quantity" placeholder="请输入商品数量" />-->
<!--        </el-form-item>-->

        <el-form-item label="物流公司" prop="shipCompanyId">
<!--          <el-input v-model="form.shipCompany" placeholder="请输入物流公司" />-->
          <el-select v-model="form.shipCompanyId" filterable r placeholder="选择快递公司" >
            <el-option v-for="item in logisticsList" :key="item.id" :label="item.name" :value="item.id">
              <span style="float: left">{{ item.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px" >{{item.number}}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号" prop="shipNo">
          <el-input v-model="form.shipNo" placeholder="请输入物流单号" />
        </el-form-item>
<!--        <el-form-item label="运费" prop="shipCost">-->
<!--          <el-input type="number" v-model.number="form.shipCost" placeholder="请输入运费" />-->
<!--        </el-form-item>-->
        <el-form-item label="发货时间" prop="shipTime">
          <el-date-picker clearable
            v-model="form.shipTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择发货时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
<!--        <el-form-item label="子订单金额" prop="itemAmount">-->
<!--          <el-input v-model="form.itemAmount" placeholder="请输入子订单金额" />-->
<!--        </el-form-item>-->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>

import {listSupplier} from "@/api/goods/supplier";
import { listShop } from "@/api/shop/shop";
import {listLogisticsStatus} from "@/api/shipping/logistics";
import {
  listShipStockupSupplier,supplierShipConfirm
} from "@/api/shipping/shipping";
import Clipboard from "clipboard";


export default {
  name: "supplierShipment",
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
      // 供应商代发货表格数据
      agentShippingList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        shopId: null,
        shopType: null,
        supplierId: null,
        orderNum: null,
        orderItemId: null
      },
      shopList: [],
      logisticsList:[],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        goodsAmount: [{ required: true, message: '不能为空' }],
        shipCompanyId: [{ required: true, message: '不能为空' }],
        shipNo: [{ required: true, message: '不能为空' }],
        // shipCost: [{ required: true, message: '不能为空' }],
        shipTime: [{ required: true, message: '不能为空' }],
      },
      supplierLoading:false,
      supplierList:[]
    };
  },
  created() {
    listSupplier().then(resp=>{
      this.supplierList = resp.rows
      this.getList();
    })

    // listShop({}).then(response => {
    //     this.shopList = response.rows;
    //   });
    // listSupplier({}).then(response => {
    //   this.supplierList = response.rows;
    //   });
    // listLogistics({}).then(resp=>{
    //   this.logisticsList = resp.rows
    // })
  },
  methods: {
    searchSupplier(query){
      this.supplierLoading = true;
      const qw = {
        name:query
      }
      listSupplier(qw).then(response => {
        this.supplierList = response.rows;
        this.supplierLoading = false;
      });
    },
    copyActiveCode(event,queryParams) {
      console.log(queryParams)
      const clipboard = new Clipboard(".tag-copy")
      clipboard.on('success', e => {
        this.$message({ type: 'success', message: '复制成功' })
        // 释放内存
        clipboard.destroy()
      })
      clipboard.on('error', e => {
        // 不支持复制
        this.$message({ type: 'waning', message: '该浏览器不支持自动复制' })
        // 释放内存
        clipboard.destroy()
      })
    },
    /** 查询供应商代发货列表 */
    getList() {
      this.loading = true;
      listShipStockupSupplier(this.queryParams).then(response => {
        this.agentShippingList = response.rows;
        console.log('=======',this.agentShippingList)
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
        shipmentId:null,
        orderNum:null,
        orderTime:null,
        goodsAmount:0.00,
        shipCompanyId: null,
        shipNo: null,
        shipCost: 0.00,
        shipTime: null,
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
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加供应商代发货";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      // getShippingDetail(id).then(response => {
        this.form = row;
      this.form.shipTime = new Date()
        // this.form.shipmentId=response.data.id
        // this.form.orderNum=response.data.orderNum
        // this.form.orderTime=response.data.orderTime
        // this.form.goodsAmount=response.data.goodsAmount
      listLogisticsStatus({}).then(resp=>{
          this.logisticsList = resp.rows
          this.open = true;
          this.title = "填写供应商发货物流信息";
        })

      // });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const form={
            id:this.form.id,
            logisticsCompany:this.form.shipCompanyId,
            logisticsCode:this.form.shipNo,
            shipTime:this.form.shipTime
          }
          supplierShipConfirm(form).then(response => {
              this.$modal.msgSuccess("供应商发货成功");
              this.open = false;
              this.getList();
            });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认编号为"' + ids + '"的订单批量付款呢？').then(function() {
        return delAgentShipping(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('scm/agentShipping/export', {
        ...this.queryParams
      }, `agentShipping_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
