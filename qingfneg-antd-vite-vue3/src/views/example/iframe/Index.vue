<!--  -->
<template>
  <div>
    <iframe
      style="width: 100%; height: 100%"
      frameborder="0"
      scrolling="yes"
      :src="bdTokenUrl"
      id="bdIframe"
    ></iframe>
  </div>
</template>

<script>
import { defineComponent, computed, ref, unref, onMounted } from "vue";
export default defineComponent({
  setup() {
    //Url
    const bdTokenUrl = ref("http://www.qingfeng.plus");

    onMounted(() => {
      /**
       * iframe-宽高自适应显示
       */
      const oIframe = document.getElementById("bdIframe");
      const deviceWidth = document.documentElement.clientWidth;
      const deviceHeight = document.documentElement.clientHeight;
      oIframe.style.width = Number(deviceWidth) - 220 + "px"; //数字是页面布局宽度差值
      oIframe.style.height = Number(deviceHeight) - 120 + "px"; //数字是页面布局高度差
    });

    return {
      bdTokenUrl
    };
  },
});
</script>
<style scoped>
/* @import url(); 引入css类 */
</style>