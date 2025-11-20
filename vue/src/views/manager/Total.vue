<template>
  <div style="display: flex;">
    <el-card style="width: 40%;">
      <template #header>商品统计</template>
      <div id="goods" style="height: 250%"></div>
    </el-card>
    <el-card style="width: 58%;margin-left: 2%">
      <template #header>销售数据</template>
      <div id="buys" style="height: 250%"></div>
    </el-card>
  </div>
  <el-card style="width: 100%;margin-top: 1%">
    <template #header>周期数据</template>
    <div id="months" style="height: 250px"></div>
  </el-card>

</template>

<script>
import * as echarts from "echarts";

export default {
  mounted() {
    this.initBie();
    this.initBuys();
    this.initMoths();
  },
  methods: {
    initBie() {
      let chartDom = document.getElementById('goods');
      let myChart = echarts.init(chartDom);
      let option;

      option = {

        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: 'Access From',
            type: 'pie',
            radius: '50%',
            data: [
              { value: 2, name: '蔬菜' },
              { value: 4, name: '水果' },
              { value: 6, name: '农副产品' },
              { value: 8, name: '菌菇' },
              { value: 10, name: '其他' }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      };

      option && myChart.setOption(option);
    },
    initBuys() {
      let chartDom = document.getElementById('buys');
      let myChart = echarts.init(chartDom);
      let option;

      option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            data: [
              '土豆',
              '西红柿',
              '黄桃',
              '车厘子',
              '鸡蛋',
              '大白菜',
              '玉米',
              '石榴',
              '香菇'
            ],
            axisTick: {
              alignWithLabel: true
            }
          }
        ],
        yAxis: [
          {
            type: 'value'
          }
        ],
        series: [
          {
            name: 'Direct',
            type: 'bar',
            barWidth: '60%',
            data: [10, 52, 200, 334, 390, 330, 220, 300, 350]
          }
        ]
      };

      option && myChart.setOption(option);
    },
    initMoths() {
      let chartDom = document.getElementById('months');
      let myChart = echarts.init(chartDom);
      let option;

      option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['土豆', '西红柿', '黄桃', '车厘子', '鸡蛋', '大白菜']
        },
        grid: {
          left: '0%',
          right: '2%',
          bottom: '0%',
          containLabel: true
        },
        toolbox: {
          feature: {
            saveAsImage: {}
          }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
          type: 'value',
          // min: 0,
          // max: 4500,
          // interval: 1000,
        },
        series: [
          {
            name: '土豆',
            type: 'line',
            stack: 'Total',
            data: [888, 132, 101, 134, 90, 230, 210]
          },
          {
            name: '西红柿',
            type: 'line',
            stack: 'Total',
            data: [220, 182, 191, 234, 290, 330, 310]
          },
          {
            name: '黄桃',
            type: 'line',
            stack: 'Total',
            data: [150, 232, 201, 154, 190, 330, 410]
          },
          {
            name: '车厘子',
            type: 'line',
            stack: 'Total',
            data: [320, 332, 301, 334, 390, 330, 320]
          },
          {
            name: '鸡蛋',
            type: 'line',
            stack: 'Total',
            data: [820, 932, 901, 934, 1290, 1330, 1320]
          },
          {
            name: '大白菜',
            type: 'line',
            stack: 'Total',
            data: [200, 320, 410, 200, 1320, 888, 999, 777]
          }
        ]
      };

      option && myChart.setOption(option);
    }
  }
}
</script>
