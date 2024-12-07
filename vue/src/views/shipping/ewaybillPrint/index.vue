<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane v-for="item in typeList" :label="item.name" :name="item.code" lazy>
        <print-tao v-if="item.id === 100"></print-tao>
        <print-jd v-if="item.id === 200"></print-jd>
        <print-jd-vc v-if="item.id === 280"></print-jd-vc>
        <print-pdd v-if="item.id === 300"></print-pdd>
        <print-dou v-if="item.id === 400"></print-dou>
        <print-wei v-if="item.id === 500"></print-wei>
        <print-offline v-if="item.id === 999"></print-offline>
      </el-tab-pane>

<!--      <el-tab-pane label="淘宝天猫" name="printTao" lazy>-->
<!--        <print-tao></print-tao>-->
<!--      </el-tab-pane>-->
<!--      <el-tab-pane label="京东" name="printJd" lazy>-->
<!--        <print-jd></print-jd>-->
<!--      </el-tab-pane>-->
<!--      <el-tab-pane label="拼多多" name="printPdd" lazy>-->
<!--        <print-pdd></print-pdd>-->
<!--      </el-tab-pane>-->
<!--      <el-tab-pane label="抖店" name="printDou" lazy>-->
<!--        <print-dou></print-dou>-->
<!--      </el-tab-pane>-->
<!--      <el-tab-pane label="视频号" name="printWei" lazy>-->
<!--        <print-wei></print-wei>-->
<!--      </el-tab-pane>-->
    </el-tabs>

  </div>
</template>

<script>
import printTao from "@/views/shipping/ewaybillPrint/tao/index.vue";
import printPdd from "@/views/shipping/ewaybillPrint/pdd/index.vue";
import printDou from "@/views/shipping/ewaybillPrint/dou/index.vue";
import printJd from "@/views/shipping/ewaybillPrint/jd/index.vue";
import printWei from "@/views/shipping/ewaybillPrint/wei/index.vue";
import printOffline from "@/views/shipping/ewaybillPrint/offline/index.vue";
import {listPlatform} from "@/api/shop/shop";

export default {
  name: "print",
  components:{
    printTao,printPdd,printDou,printJd,printWei,printOffline},
  data() {
    return {
      activeName: '',
      typeList: [],
    };
  },
  created() {

  },
  mounted() {
    listPlatform({status:0}).then(res => {
      this.typeList = res.rows;
      this.activeName = this.typeList[0].code
    })
  },
  methods: {
    handleClick(tab, event) {
      console.log(tab, event);
    }
  }
};
</script>
