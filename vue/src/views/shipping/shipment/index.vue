<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="待发货" name="waitShip">
        <shipment-wait></shipment-wait>
      </el-tab-pane>
      <el-tab-pane label="已发货" name="shipped" lazy>
        <shipment-shipped></shipment-shipped>
      </el-tab-pane>
      <el-tab-pane label="已分配供应商发货" name="assigned" lazy>
        <shipment-assigned></shipment-assigned>
      </el-tab-pane>


    </el-tabs>

  </div>
</template>

<script>
import ShipmentWait  from "@/views/shipping/shipment/wait_ship.vue";
import ShipmentShipped  from "@/views/shipping/shipment/shipped_list.vue";
import ShipmentAssigned  from "@/views/shipping/shipment/assigned_list.vue";

export default {
  name: "Shipment",
  components:{ShipmentWait,ShipmentShipped,ShipmentAssigned},
  data() {
    return {
      activeName: 'waitShip'
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
