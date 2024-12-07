<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">

      <el-tab-pane label="备货订单" name="stockupOrder">
        <ship-stockup-order></ship-stockup-order>
      </el-tab-pane>
      <el-tab-pane label="备货商品明细" name="stockupOrderItem" lazy>
        <ship-stockup-order-item></ship-stockup-order-item>
      </el-tab-pane>
<!--      <el-tab-pane label="京东自营" name="jdvcOrder" lazy>-->
<!--        <order-jd-vc></order-jd-vc>-->
<!--      </el-tab-pane>-->
<!--      <el-tab-pane label="抖店" name="douOrder" lazy>-->
<!--        <order-dou></order-dou>-->
<!--      </el-tab-pane>-->
<!--      <el-tab-pane label="拼多多" name="pddOrder" lazy>-->
<!--        <order-pdd></order-pdd>-->
<!--      </el-tab-pane>-->
    </el-tabs>

  </div>
</template>

<script>
import ShipStockupOrder  from "@/views/shipping/stockup/order_list.vue";
import ShipStockupOrderItem  from "@/views/shipping/stockup/order_item_list.vue";

export default {
  name: "stockup",
  components:{ShipStockupOrder,ShipStockupOrderItem},
  data() {
    return {
      activeName: 'stockupOrder'
    };
  },
  created() {

  },
  mounted() {

  },
  methods: {
    handleClick(tab, event) {
      console.log(tab, event);
    }
  }
};
</script>
